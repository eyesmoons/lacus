package com.lacus.service.spark.impl;

import com.lacus.common.exception.CustomException;
import com.lacus.dao.spark.entity.SparkJobEntity;
import com.lacus.dao.spark.entity.SparkJobInstanceEntity;
import com.lacus.dao.system.entity.SysResourcesEntity;
import com.lacus.enums.SparkJobTypeEnum;
import com.lacus.enums.SparkStatusEnum;
import com.lacus.service.spark.ISparkJobInstanceService;
import com.lacus.service.spark.ISparkJobService;
import com.lacus.service.spark.ISparkOperationService;
import com.lacus.service.spark.handler.SparkAppHandler;
import com.lacus.service.spark.handler.SparkLauncherMonitor;
import com.lacus.service.system.ISysResourcesService;
import com.lacus.utils.CommonPropertyUtils;
import com.lacus.utils.time.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.spark.launcher.SparkLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.lacus.common.constant.Constants.HADOOP_CONF_DIR;
import static com.lacus.common.constant.Constants.JAVA_HOME;
import static com.lacus.common.constant.Constants.LACUS_APPLICATION_HOME;
import static com.lacus.common.constant.Constants.SPARK_CLIENT_HOME;
import static com.lacus.common.constant.Constants.SPARK_LOG_PATH;
import static com.lacus.common.constant.Constants.SPARK_SQL_FILE_DIR;
import static com.lacus.common.constant.Constants.SPARK_SQL_JOB_JAR;

@Slf4j
@Service
public class SparkOperationServiceImpl implements ISparkOperationService {

    private static final String SPARK_SQL_MAIN_CLASS = "com.lacus.manager.spark.SparkSqlMain";
    private static final String APP_NAME_PREFIX = "SPARK_JOB_";
    private static final String SPARK_SQL_FILE_PREFIX = "spark_sql_";
    private static final String SQL_FILE_SUFFIX = ".sql";
    private static final long SUBMIT_TIMEOUT_MS = 300000L;

    @Autowired
    private ISparkJobService sparkJobService;

    @Autowired
    private ISparkJobInstanceService instanceService;

    @Autowired
    private ISysResourcesService resourcesService;

    @Override
    public void start(Long jobId) {
        SparkJobEntity sparkJobEntity = validateAndGetJob(jobId);
        SparkJobInstanceEntity instance = createJobInstance(sparkJobEntity);

        log.info("开始提交Spark任务: {}", jobId);
        try {
            String appId = submitSparkJob(sparkJobEntity);
            updateJobStatus(sparkJobEntity, instance, appId, SparkStatusEnum.RUNNING);
        } catch (Exception e) {
            handleSubmitError(sparkJobEntity, instance, e);
        }
    }

    private SparkJobEntity validateAndGetJob(Long jobId) {
        SparkJobEntity sparkJobEntity = sparkJobService.getById(jobId);
        if (sparkJobEntity == null) {
            throw new CustomException("任务不存在");
        }
        if (SparkStatusEnum.RUNNING.equals(sparkJobEntity.getJobStatus())) {
            throw new CustomException("任务运行中,请先停止任务");
        }
        return sparkJobEntity;
    }

    private SparkJobInstanceEntity createJobInstance(SparkJobEntity job) {
        SparkJobInstanceEntity instance = new SparkJobInstanceEntity();
        instance.setJobId(job.getJobId());
        instance.setInstanceName(job.getJobName() + "_" + System.currentTimeMillis());
        instance.setDeployMode(job.getDeployMode());
        instance.setJobStatus(SparkStatusEnum.RUNNING);
        instance.setSubmitTime(DateUtils.getNowDate());
        instanceService.save(instance);
        return instance;
    }

    private String submitSparkJob(SparkJobEntity sparkJobEntity) throws IOException {
        SparkLauncher launcher = createSparkLauncher(sparkJobEntity);
        return launchSparkJob(launcher);
    }

    private SparkLauncher createSparkLauncher(SparkJobEntity sparkJobEntity) throws IOException {
        Map<String, String> env = createSparkEnvironment();
        SparkLauncher launcher = new SparkLauncher(env);

        // 设置基本配置
        configureBasicSettings(launcher, sparkJobEntity);

        // 设置资源配置
        configureResources(launcher, sparkJobEntity);

        // 处理SQL任务配置
        if (sparkJobEntity.getJobType().equals(SparkJobTypeEnum.BATCH_SQL)) {
            configureSqlJob(launcher, sparkJobEntity);
        }

        launcher.setVerbose(true);
        launcher.redirectError();
        return launcher;
    }

    private Map<String, String> createSparkEnvironment() {
        Map<String, String> env = new HashMap<>();
        env.put("HADOOP_CONF_DIR", CommonPropertyUtils.getString(HADOOP_CONF_DIR));
        env.put("JAVA_HOME", CommonPropertyUtils.getString(JAVA_HOME));
        return env;
    }

    private void configureBasicSettings(SparkLauncher launcher, SparkJobEntity job) throws IOException {
        String master = getMaster(job);
        launcher.setAppName(APP_NAME_PREFIX + job.getJobName());
        launcher.setSparkHome(CommonPropertyUtils.getString(SPARK_CLIENT_HOME));
        launcher.setMainClass(getMainClass(job));
        launcher.setAppResource(getMainJarPath(job));
        launcher.setMaster(master);
        launcher.setDeployMode(job.getDeployMode().name());
        if (master.startsWith("yarn")) {
            launcher.setConf("spark.yarn.submit.waitAppCompletion", "true");
        }
    }

    private void configureResources(SparkLauncher launcher, SparkJobEntity job) {
        launcher.setConf("spark.driver.memory", job.getDriverMemory() + "g");
        launcher.setConf("spark.executor.memory", job.getExecutorMemory() + "g");
        launcher.setConf("spark.executor.instances", String.valueOf(job.getNumExecutors()));
        launcher.setConf("spark.executor.cores", String.valueOf(job.getExecutorCores()));
        launcher.setConf("spark.default.parallelism", String.valueOf(job.getParallelism()));
        launcher.setConf("spark.driver.allowMultipleContexts", "true");
    }

    private String launchSparkJob(SparkLauncher launcher) throws IOException {
        SparkAppHandler handle = new SparkAppHandler();
        Process process = launcher.launch();
        handle.setProcess(process);

        SparkLauncherMonitor logMonitor = SparkLauncherMonitor.createLogMonitor(handle);
        logMonitor.setSubmitTimeoutMs(SUBMIT_TIMEOUT_MS);
        logMonitor.setRedirectLogPath(CommonPropertyUtils.getString(SPARK_LOG_PATH));

        try {
            logMonitor.start();
            logMonitor.join();
        } catch (InterruptedException e) {
            logMonitor.interrupt();
            throw new RuntimeException("启动spark任务超时: " + e.getMessage());
        }

        validateSparkJobState(handle.getState(), handle.getAppId());
        return handle.getAppId();
    }

    private void validateSparkJobState(SparkStatusEnum state, String appId) {
        if (Objects.equals(state, SparkStatusEnum.FAILED)
                || Objects.equals(state, SparkStatusEnum.KILLED)
                || Objects.equals(state, SparkStatusEnum.LOST)
                || appId == null) {
            throw new RuntimeException("启动spark任务失败，状态: " + state);
        }
    }

    private void updateJobStatus(SparkJobEntity job, SparkJobInstanceEntity instance, String appId, SparkStatusEnum status) {
        sparkJobService.updateStatus(job.getJobId(), appId, status);
        instanceService.updateStatus(instance.getInstanceId(), appId, status);
    }

    private void handleSubmitError(SparkJobEntity job, SparkJobInstanceEntity instance, Exception e) {
        log.error("提交Spark任务失败", e);
        updateJobStatus(job, instance, null, SparkStatusEnum.FAILED);
        throw new RuntimeException("任务提交出错：" + e.getMessage());
    }

    @Override
    public void stop(Long jobId) {
        SparkJobEntity sparkJobEntity = validateAndGetJob(jobId);
        log.info("开始停止Spark任务: {}", jobId);
        // TODO: 实现具体的Spark任务停止逻辑
        sparkJobService.updateStatus(jobId, SparkStatusEnum.KILLED);
    }

    private void configureSqlJob(SparkLauncher launcher, SparkJobEntity job) throws IOException {
        Path sqlDir = Paths.get(CommonPropertyUtils.getString(SPARK_SQL_FILE_DIR));
        if (!Files.exists(sqlDir)) {
            Files.createDirectories(sqlDir);
        }

        // 创建临时SQL文件
        String fileName = SPARK_SQL_FILE_PREFIX + UUID.randomUUID() + SQL_FILE_SUFFIX;
        Path sqlFile = sqlDir.resolve(fileName);
        Files.write(sqlFile, job.getSqlContent().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        launcher.addAppArgs(sqlFile.toAbsolutePath().toString());
    }

    private String getMainClass(SparkJobEntity job) {
        if (job.getJobType().equals(SparkJobTypeEnum.BATCH_SQL)) {
            return SPARK_SQL_MAIN_CLASS;
        } else if (job.getJobType().equals(SparkJobTypeEnum.JAR)) {
            return job.getMainClassName();
        }
        throw new CustomException("不支持的任务类型");
    }

    private String getMainJarPath(SparkJobEntity job) {
        String appHome = CommonPropertyUtils.getString(LACUS_APPLICATION_HOME);
        if (job.getJobType().equals(SparkJobTypeEnum.BATCH_SQL)) {
            return appHome + CommonPropertyUtils.getString(SPARK_SQL_JOB_JAR);
        } else if (job.getJobType().equals(SparkJobTypeEnum.JAR)) {
            Integer mainJarPathId = job.getMainJarPath();
            SysResourcesEntity sysResource = resourcesService.getById(mainJarPathId);
            if (ObjectUtils.isEmpty(sysResource)) {
                throw new CustomException("任务主Jar包不存在");
            }
            return sysResource.getFilePath();
        }
        throw new CustomException("不支持的任务类型");
    }

    private static String getMaster(SparkJobEntity job) {
        String master = job.getMaster();
        switch (job.getDeployMode()) {
            case LOCAL:
                return "local[*]";
            case STANDALONE_CLIENT:
            case STANDALONE_CLUSTER:
                return "spark://" + master;
            case YARN_CLIENT:
            case YARN_CLUSTER:
                return "yarn";
            case K8S_CLUSTER:
            case K8S_CLIENT:
                return "k8s://" + master;
            default:
                throw new CustomException("不支持的部署模式");
        }
    }
}
