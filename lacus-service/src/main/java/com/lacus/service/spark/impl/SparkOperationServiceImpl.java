package com.lacus.service.spark.impl;

import com.lacus.common.exception.CustomException;
import com.lacus.dao.spark.entity.SparkJobEntity;
import com.lacus.dao.spark.entity.SparkJobInstanceEntity;
import com.lacus.dao.system.entity.SysResourcesEntity;
import com.lacus.enums.SparkDeployModeEnum;
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

    private static final String SPARK_SQL_MAIN_CLASS = "com.lacus.service.spark.SparkSqlMain";

    @Autowired
    private ISparkJobService sparkJobService;

    @Autowired
    private ISparkJobInstanceService instanceService;

    @Autowired
    private ISysResourcesService resourcesService;

    @Override
    public void start(Long jobId) {
        SparkJobEntity sparkJobEntity = sparkJobService.getById(jobId);
        if (sparkJobEntity == null) {
            throw new CustomException("任务不存在");
        }
        SparkJobInstanceEntity instance = new SparkJobInstanceEntity();
        instance.setJobId(jobId);
        instance.setInstanceName(sparkJobEntity.getJobName() + "_" + System.currentTimeMillis());
        instance.setDeployMode(sparkJobEntity.getDeployMode());
        instance.setStatus(SparkStatusEnum.RUNNING);
        instance.setSubmitTime(DateUtils.getNowDate());
        instanceService.save(instance);

        // 检查任务状态
        if (SparkStatusEnum.RUNNING.equals(sparkJobEntity.getJobStatus())) {
            throw new CustomException("任务运行中,请先停止任务");
        }

        log.info("开始提交Spark任务: {}", jobId);
        try {
            String appId = submitSparkJob(sparkJobEntity);
            sparkJobEntity.setAppId(appId);
            sparkJobService.updateStatus(jobId, appId, SparkStatusEnum.RUNNING);
            instanceService.updateStatus(instance.getInstanceId(), appId, SparkStatusEnum.RUNNING);
        } catch (IOException e) {
            sparkJobService.updateStatus(jobId, SparkStatusEnum.FAILED);
            instanceService.updateStatus(instance.getInstanceId(), SparkStatusEnum.FAILED);
            throw new RuntimeException("任务提交出错：", e);
        }
    }

    /**
     * 提交Spark任务
     *
     * @param sparkJobEntity Spark任务实体
     */
    private String submitSparkJob(SparkJobEntity sparkJobEntity) throws IOException {
        Map<String, String> env = new HashMap<>();
        env.put("HADOOP_CONF_DIR", CommonPropertyUtils.getString(HADOOP_CONF_DIR));
        env.put("JAVA_HOME", CommonPropertyUtils.getString(JAVA_HOME));

        String appHome = CommonPropertyUtils.getString(LACUS_APPLICATION_HOME);
        String mainJarPath = "";
        String mainClass = "";
        if (sparkJobEntity.getJobType().equals(SparkJobTypeEnum.BATCH_SQL)) {
            mainJarPath = appHome + CommonPropertyUtils.getString(SPARK_SQL_JOB_JAR);
            mainClass = SPARK_SQL_MAIN_CLASS;
        } else if (sparkJobEntity.getJobType().equals(SparkJobTypeEnum.JAR)) {
            Integer mainJarPathId = sparkJobEntity.getMainJarPath();
            SysResourcesEntity sysResource = resourcesService.getById(mainJarPathId);
            if (ObjectUtils.isEmpty(sysResource)) {
                throw new CustomException("任务主Jar包不存在");
            }
            mainJarPath = sysResource.getFilePath();
            mainClass = sparkJobEntity.getMainClassName();
        }
        String master = getMaster(sparkJobEntity);
        SparkLauncher launcher = new SparkLauncher(env);
        launcher.setAppName("SPARK_JOB_" + sparkJobEntity.getJobName());
        launcher.setSparkHome(CommonPropertyUtils.getString(SPARK_CLIENT_HOME));
        launcher.setAppResource(mainJarPath);
        launcher.setMainClass(mainClass);
        launcher.setMaster(master);
        launcher.setDeployMode(sparkJobEntity.getDeployMode().name());
        launcher.setConf("spark.driver.memory", sparkJobEntity.getDriverMemory() + "g");
        launcher.setConf("spark.executor.memory", sparkJobEntity.getExecutorMemory() + "g");
        launcher.setConf("spark.executor.instances", String.valueOf(sparkJobEntity.getNumExecutors()));
        launcher.setConf("spark.executor.cores", String.valueOf(sparkJobEntity.getExecutorCores()));
        launcher.setConf("spark.default.parallelism", String.valueOf(sparkJobEntity.getParallelism()));
        launcher.setConf("spark.driver.allowMultipleContexts", "true");
        if (sparkJobEntity.getJobType().equals(SparkJobTypeEnum.BATCH_SQL)) {
            Path sqlDir = Paths.get(CommonPropertyUtils.getString(SPARK_SQL_FILE_DIR));
            if (!Files.exists(sqlDir)) {
                Files.createDirectories(sqlDir);
            }
            // 创建临时SQL文件
            String fileName = "spark_sql_" + UUID.randomUUID() + ".sql";
            Path sqlFile = sqlDir.resolve(fileName);
            Files.write(sqlFile, sparkJobEntity.getSqlContent().getBytes(), StandardOpenOption.APPEND);
            launcher.addAppArgs(sqlFile.toAbsolutePath().toString());
        }

        if (master.startsWith("yarn")) {
            launcher.setConf("spark.yarn.submit.waitAppCompletion", "true");
        }
        launcher.setVerbose(true);
        launcher.redirectError();

        // 开始启动spark任务
        SparkStatusEnum sparkState;
        String appId;
        String errMsg = "启动spark任务出错: ";
        try {
            SparkAppHandler handle = new SparkAppHandler();
            Process process = launcher.launch();
            handle.setProcess(process);
            SparkLauncherMonitor logMonitor = SparkLauncherMonitor.createLogMonitor(handle);
            logMonitor.setSubmitTimeoutMs(300000L);
            logMonitor.setRedirectLogPath(CommonPropertyUtils.getString(SPARK_LOG_PATH));
            logMonitor.start();
            try {
                logMonitor.join();
            } catch (InterruptedException e) {
                logMonitor.interrupt();
                throw new RuntimeException(errMsg + e.getMessage());
            }
            appId = handle.getAppId();
            sparkState = handle.getState();
            log.info("appId：{}，spark任务状态：{}", appId, sparkState);
        } catch (IOException e) {
            log.warn(errMsg, e);
            throw new RuntimeException(errMsg + e.getMessage());
        }

        if (Objects.equals(sparkState, SparkStatusEnum.FAILED) || Objects.equals(sparkState, SparkStatusEnum.KILLED) || Objects.equals(sparkState, SparkStatusEnum.LOST)) {
            throw new RuntimeException(errMsg + "spark任务状态: " + sparkState);
        }

        if (appId == null) {
            throw new RuntimeException(errMsg + "spark任务状态: " + sparkState);
        }
        return appId;
    }

    private static String getMaster(SparkJobEntity sparkJobEntity) {
        String master = sparkJobEntity.getMaster();
        if (sparkJobEntity.getDeployMode().equals(SparkDeployModeEnum.LOCAL)) {
            master = "local[*]";
        } else if (sparkJobEntity.getDeployMode().equals(SparkDeployModeEnum.STANDALONE_CLIENT) || sparkJobEntity.getDeployMode().equals(SparkDeployModeEnum.STANDALONE_CLUSTER)) {
            master = "spark://" + master;
        } else if (sparkJobEntity.getDeployMode().equals(SparkDeployModeEnum.YARN_CLIENT) || sparkJobEntity.getDeployMode().equals(SparkDeployModeEnum.YARN_CLUSTER)) {
            master = "yarn";
        } else if (sparkJobEntity.getDeployMode().equals(SparkDeployModeEnum.K8S_CLUSTER) || sparkJobEntity.getDeployMode().equals(SparkDeployModeEnum.K8S_CLIENT)) {
            master = "k8s://" + master;
        }
        return master;
    }

    @Override
    public void stop(Long jobId) {
        SparkJobEntity sparkJobEntity = sparkJobService.getById(jobId);
        if (sparkJobEntity == null) {
            throw new CustomException("任务不存在");
        }

        // TODO: 实现具体的Spark任务停止逻辑
        log.info("开始停止Spark任务: {}", jobId);

        // 更新任务状态为已停止
        sparkJobService.updateStatus(jobId, SparkStatusEnum.KILLED);
    }
}
