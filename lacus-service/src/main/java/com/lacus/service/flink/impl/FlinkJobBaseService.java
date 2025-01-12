package com.lacus.service.flink.impl;

import cn.hutool.core.date.DateUtil;
import com.lacus.common.config.FlinkRunThreadPoolConfig;
import com.lacus.common.exception.CustomException;
import com.lacus.dao.flink.entity.FlinkJobEntity;
import com.lacus.dao.flink.entity.FlinkJobInstanceEntity;
import com.lacus.dao.system.entity.SysEnvEntity;
import com.lacus.enums.FlinkDeployModeEnum;
import com.lacus.enums.FlinkJobTypeEnum;
import com.lacus.enums.FlinkStatusEnum;
import com.lacus.service.flink.IFlinkJobInstanceService;
import com.lacus.service.flink.IFlinkJobService;
import com.lacus.service.flink.dto.JobRunParamDTO;
import com.lacus.service.flink.dto.StandaloneFlinkJobInfo;
import com.lacus.service.system.ISysEnvService;
import com.lacus.utils.JobExecuteThreadPoolUtil;
import com.lacus.utils.PropertyUtils;
import com.lacus.utils.file.FileUtil;
import com.lacus.utils.hdfs.HdfsUtil;
import com.lacus.utils.time.DateUtils;
import com.lacus.utils.yarn.YarnUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

import static com.lacus.common.constant.Constants.FLINK_CLIENT_HOME;
import static com.lacus.common.constant.Constants.FLINK_JOB_EXECUTE_HOME;
import static com.lacus.common.constant.Constants.FLINK_SQL_JOB_JAR;
import static com.lacus.enums.FlinkDeployModeEnum.YARN_APPLICATION;
import static com.lacus.enums.FlinkDeployModeEnum.YARN_PER;

@Component
@Slf4j
public class FlinkJobBaseService {

    private static final ThreadLocal<String> APPID_THREAD_LOCAL = new ThreadLocal<>();
    private static final String APP_CLASS_NAME = "com.lacus.flink.FlinkSqlJobApplication";

    @Autowired
    private YarnRestService yarnRestService;
    @Autowired
    private StandaloneRestService standaloneRestService;
    @Autowired
    private IFlinkJobService flinkJobService;
    @Autowired
    private IFlinkJobInstanceService flinkJobInstanceService;
    @Autowired
    private ISysEnvService envService;

    public String submitJob(String command, FlinkDeployModeEnum flinkDeployModeEnum) throws Exception {
        log.info("flink任务提交命令：{} ", command);
        Process process = Runtime.getRuntime().exec(command);
        this.clearLogStream(process.getErrorStream(), String.format("[%s]start local error：[%s]", DateUtil.now(), flinkDeployModeEnum.name()));
        String applicationId = this.getApplicationIdFromLog(process.getInputStream(), flinkDeployModeEnum);
        int result = process.waitFor();
        if (result != 0) {
            throw new RuntimeException("flink命令运行异常: " + command);
        }
        return applicationId;
    }

    public void savepointForYarn(String jobId, String targetDirectory, String yarnAppId) throws Exception {
        this.doSavepoint(buildSavepointCommand(jobId, targetDirectory, yarnAppId, PropertyUtils.getString(FLINK_CLIENT_HOME)));
    }

    public void savepointForStandalone(String jobId, String targetDirectory) throws Exception {
        this.doSavepoint(buildSavepointCommand(jobId, targetDirectory, PropertyUtils.getString(FLINK_CLIENT_HOME)));
    }

    private void doSavepoint(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(command);
        this.clearLogStream(process.getInputStream(), "savepoint success！");
        this.clearLogStream(process.getErrorStream(), "savepoint error！");
        int result = process.waitFor();
        if (result != 0) {
            throw new Exception("执行savepoint失败！");
        }
    }

    private void clearLogStream(InputStream stream, final String threadName) {
        FlinkRunThreadPoolConfig.getInstance().getThreadPoolExecutor().execute(() -> {
                    BufferedReader reader = null;
                    try {
                        Thread.currentThread().setName(threadName);
                        String result;
                        reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                        while ((result = reader.readLine()) != null) {
                            log.info(result);
                        }
                    } catch (Exception e) {
                        log.error(Arrays.toString(e.getStackTrace()));
                    } finally {
                        this.quiteClose(reader, stream, "clearLogStream");
                    }
                }
        );
    }

    /**
     * 从日志中获取appId
     */
    private String getApplicationIdFromLog(InputStream stream, FlinkDeployModeEnum flinkDeployModeEnum) {
        String applicationId = null;
        BufferedReader reader = null;
        try {
            String result;
            reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            while ((result = reader.readLine()) != null) {
                log.info("运行日志：{}", result);
                if (StringUtils.isEmpty(applicationId) && result.contains("job submitted success:")) {
                    applicationId = result.replace("job submitted success:", " ").trim();
                    log.info("job submitted success，applicationId： {}", applicationId);
                }
                if (StringUtils.isEmpty(applicationId) && result.contains("Job has been submitted with JobID")) {
                    applicationId = result.replace("Job has been submitted with JobID", "").trim();
                    log.info("从日志：{} 中解析到的appId：{}", result, applicationId);
                }
            }

            if (FlinkDeployModeEnum.YARN_APPLICATION == flinkDeployModeEnum || FlinkDeployModeEnum.YARN_PER == flinkDeployModeEnum) {
                log.info("yarn 模式不需要解析applicationId，可以通过rest接口获取");
            } else if (StringUtils.isEmpty(applicationId)) {
                throw new RuntimeException("解析applicationId异常");
            }
            FlinkJobBaseService.APPID_THREAD_LOCAL.set(applicationId);
            log.info("解析到的applicationId：{}", applicationId);
            return applicationId;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.quiteClose(reader, stream, "clearInfoLogStream");
        }
    }

    private void quiteClose(BufferedReader reader, InputStream stream, String typeName) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("[{}] reader关闭失败 ", typeName, e);
            }
        }
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                log.error("[{}] stream关闭失败 ", typeName, e);
            }
        }
    }

    public String buildFlinkRunCommand(JobRunParamDTO jobRunParamDTO, FlinkJobEntity flinkJobEntity, String savepointPath, String address) {
        StringBuilder command = new StringBuilder();
        FlinkDeployModeEnum deployMode = flinkJobEntity.getDeployMode();
        // 构建flink基础环境
        buildFlinkEnvCommand(flinkJobEntity.getEnvId(), jobRunParamDTO.getFlinkBinPath(), command);
        if (deployMode == FlinkDeployModeEnum.STANDALONE || deployMode == FlinkDeployModeEnum.LOCAL) { // 构建STANDALONE或LOCAL模式命令
            command.append(" run -d");
            command.append(" -m ").append(address);
        } else if (FlinkDeployModeEnum.YARN_APPLICATION == deployMode) { // 构建YARN_APPLICATION模式命令
            command.append("  run-application -t yarn-application  ");
        } else if (FlinkDeployModeEnum.YARN_PER == deployMode) { // 构建YARN_PER模式命令
            command.append(" run -d -t yarn-per-job ");
        }
        // 构建savepoint命令
        if (StringUtils.isNotEmpty(savepointPath)) {
            command.append(" -s ").append(savepointPath);
        }
        // 构建flink运行参数
        if (deployMode != FlinkDeployModeEnum.LOCAL) {
            command.append(" ").append(flinkJobEntity.getFlinkRunConfig());
            command.append(" -Dyarn.application.name=").append(flinkJobEntity.getJobName());
        }
        // 构建extJar命令
        buildExtJarCommand(flinkJobEntity.getExtJarPath(), command);
        // 构建mainClass命令
        buildMainJarCommand(flinkJobEntity, jobRunParamDTO, command);
        log.info("flink deployModel:{}, flink run command: {}", deployMode, command);
        return command.toString();
    }

    /**
     * 构建extJar命令
     */
    private static void buildExtJarCommand(String extJarPath, StringBuilder command) {
        if (StringUtils.isNotEmpty(extJarPath)) {
            String[] urlJarsList = extJarPath.split(",");
            for (String url : urlJarsList) {
                command.append(" -C ").append(url.trim());
            }
        }
    }

    /**
     * 构建mainClass命令
     */
    private void buildMainJarCommand(FlinkJobEntity flinkJobEntity, JobRunParamDTO jobRunParamDTO, StringBuilder command) {
        switch (flinkJobEntity.getJobType()) {
            case STREAMING_SQL:
            case BATCH_SQL:
                command.append(" -c ").append(APP_CLASS_NAME);
                command.append(" ").append(jobRunParamDTO.getFlinkSqlAppHome()).append(PropertyUtils.getString(FLINK_SQL_JOB_JAR));
                command.append(" -sql ").append(jobRunParamDTO.getSqlPath());
                if (StringUtils.isNotEmpty(jobRunParamDTO.getFlinkCheckpointConfig())
                        && (Objects.equals(FlinkDeployModeEnum.YARN_APPLICATION, flinkJobEntity.getDeployMode()) || Objects.equals(FlinkDeployModeEnum.YARN_PER, flinkJobEntity.getDeployMode()))) {
                    command.append(" -checkpointDir ").append(jobRunParamDTO.getFlinkCheckpointConfig());
                }
                command.append(" -jobType ").append(flinkJobEntity.getJobType());
                break;
            case JAR:
                command.append(" -c ").append(flinkJobEntity.getMainClassName());
                command.append(" ").append(jobRunParamDTO.getMainJarPath());
                command.append(" ").append(flinkJobEntity.getCustomArgs());
                break;
            default:
                log.warn("不支持的部署模式: {}", flinkJobEntity.getJobType());
        }
    }

    /**
     * 构建flink基础环境
     */
    private void buildFlinkEnvCommand(Long envId, String flinkBinPath, StringBuilder command) {
        SysEnvEntity env = envService.getById(envId);
        if (ObjectUtils.isNotEmpty(env)) {
            command.append(env.getConfig()).append("\n");
        }
        command.append(flinkBinPath);
    }

    public String buildSavepointCommand(String jobId, String targetDirectory, String yarnAppId, String flinkHome) {
        return flinkHome + "/bin/flink" + " savepoint " + jobId + " " + targetDirectory + " " + "-yid " + yarnAppId;
    }

    public String buildSavepointCommand(String jobId, String targetDirectory, String flinkHome) {
        return flinkHome + "/bin/flink" + " savepoint " + jobId + " " + targetDirectory + " ";
    }

    public void checkStartParams(FlinkJobEntity flinkJobEntity) {
        if (Objects.isNull(flinkJobEntity)) {
            throw new CustomException("任务不存在");
        }
        if (FlinkStatusEnum.RUNNING.equals(flinkJobEntity.getJobStatus())) {
            throw new CustomException("任务运行中");
        }
        if (FlinkStatusEnum.STARTING.equals(flinkJobEntity.getJobStatus())) {
            throw new CustomException("任务正在启动");
        }
        if (Objects.equals(flinkJobEntity.getDeployMode(), YARN_PER) || Objects.equals(flinkJobEntity.getDeployMode(), YARN_APPLICATION)) {
            checkYarnQueueName(flinkJobEntity);
        }
    }

    public void checkSavepoint(FlinkJobEntity flinkJobEntity) {
        if (Objects.isNull(flinkJobEntity)) {
            throw new CustomException("任务不存在");
        }
        if (FlinkJobTypeEnum.BATCH_SQL.equals(flinkJobEntity.getJobType())) {
            throw new CustomException("BATCH任务不支持savePoint：" + flinkJobEntity.getJobName());
        }
        if (StringUtils.isEmpty(flinkJobEntity.getAppId())) {
            throw new CustomException("任务未处于运行状态，不能保存savepoint");
        }
    }

    public JobRunParamDTO generateSqlFile(FlinkJobEntity flinkJobEntity) {
        String sqlPath = PropertyUtils.getString(FLINK_JOB_EXECUTE_HOME) + "flink_sql_job_" + flinkJobEntity.getJobId() + ".sql";
        boolean result = FileUtil.writeToFile(flinkJobEntity.getFlinkSql(), sqlPath);
        if (result) {
            return JobRunParamDTO.buildJobRunParam(flinkJobEntity, sqlPath);
        } else {
            throw new CustomException("任务sql文件写入失败");
        }
    }

    public void submitJobAsync(JobRunParamDTO jobRunParamDTO, FlinkJobEntity flinkJobEntity, FlinkJobInstanceEntity instance, String savepointPath) {
        ThreadPoolExecutor threadPoolExecutor = JobExecuteThreadPoolUtil.getInstance().getThreadPoolExecutor();
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String appId = "";
                APPID_THREAD_LOCAL.set(appId);
                try {
                    String command;
                    // 如果是自定义提交jar模式下载文件到本地
                    this.downJar(jobRunParamDTO, flinkJobEntity);
                    switch (flinkJobEntity.getDeployMode()) {
                        case YARN_PER:
                        case YARN_APPLICATION:
                            // 构建执行命令
                            command = buildFlinkRunCommand(jobRunParamDTO, flinkJobEntity, savepointPath, null);
                            instance.setJobScript(command);
                            // 提交任务
                            appId = this.submitYarnJob(command, flinkJobEntity);
                            APPID_THREAD_LOCAL.set(appId);
                            break;
                        case LOCAL:
                        case STANDALONE:
                            String address = standaloneRestService.getFlinkRestAddress(flinkJobEntity.getDeployMode());
                            // 构建执行命令
                            command = buildFlinkRunCommand(jobRunParamDTO, flinkJobEntity, savepointPath, address.replace("http://", ""));
                            instance.setJobScript(command);
                            // 提交任务
                            appId = this.submitStandaloneJob(command, flinkJobEntity);
                            break;
                        default:
                            log.warn("不支持的部署模式 {}", flinkJobEntity.getDeployMode());
                    }
                    if (StringUtils.isNotBlank(appId)) {
                        instance.setApplicationId(appId);
                        flinkJobEntity.setAppId(appId);
                        flinkJobEntity.setJobStatus(FlinkStatusEnum.RUNNING);
                    }
                    flinkJobService.saveOrUpdate(flinkJobEntity);
                    flinkJobInstanceService.saveOrUpdate(instance);
                } catch (Exception e) {
                    log.error("任务[{}]执行异常！", flinkJobEntity.getJobId(), e);
                    instance.setStatus(FlinkStatusEnum.FAILED);
                    instance.setFinishedTime(DateUtils.getNowDate());
                    flinkJobInstanceService.saveOrUpdate(instance);
                    flinkJobService.updateStatus(flinkJobEntity.getJobId(), FlinkStatusEnum.FAILED);
                }
            }

            /**
             * 下载文件到本地并且setMainJarPath
             */
            private void downJar(JobRunParamDTO jobRunParamDTO, FlinkJobEntity flinkJobEntity) {
                if (Objects.equals(FlinkJobTypeEnum.JAR, flinkJobEntity.getJobType())) {
                    String mainJarPath = flinkJobEntity.getMainJarPath();
                    String mainJarName = new File(mainJarPath).getName();
                    String localJarPath = PropertyUtils.getString(FLINK_JOB_EXECUTE_HOME) + "/download/" + flinkJobEntity.getJobId() + File.separator;
                    try {
                        HdfsUtil.copyFileFromHdfs(mainJarPath, localJarPath);
                        jobRunParamDTO.setMainJarPath(localJarPath + mainJarName);
                    } catch (IOException e) {
                        throw new CustomException("下载hdfs文件出错：" + e.getMessage());
                    }
                }
            }

            private String submitStandaloneJob(String command, FlinkJobEntity flinkJobEntity) throws Exception {
                String appId = submitJob(command, flinkJobEntity.getDeployMode());
                StandaloneFlinkJobInfo standaloneJobInfo = standaloneRestService.getJobInfoByAppId(appId, flinkJobEntity.getDeployMode());
                if (Objects.isNull(standaloneJobInfo) || StringUtils.isNotEmpty(standaloneJobInfo.getErrors())) {
                    log.error("任务提交失败, standaloneJobInfo: {}", standaloneJobInfo);
                    throw new CustomException("任务提交失败");
                } else {
                    if (!FlinkStatusEnum.RUNNING.name().equals(standaloneJobInfo.getState()) && !FlinkStatusEnum.FINISHED.name().equals(standaloneJobInfo.getState())) {
                        throw new CustomException("任务提交失败");
                    }
                }
                return appId;
            }

            private String submitYarnJob(String command, FlinkJobEntity jobConfigDTO) throws Exception {
                submitJob(command, jobConfigDTO.getDeployMode());
                return yarnRestService.getAppIdByYarn(jobConfigDTO.getJobName(), YarnUtil.getQueueName(jobConfigDTO.getFlinkRunConfig()));
            }
        });
    }

    private void checkYarnQueueName(FlinkJobEntity flinkJobEntity) {
        try {
            String queueName = YarnUtil.getQueueName(flinkJobEntity.getFlinkRunConfig());
            if (StringUtils.isEmpty(queueName)) {
                throw new CustomException("无法获取队列名称");
            }
            String appId = yarnRestService.getAppIdByYarn(flinkJobEntity.getJobName(), queueName);
            if (StringUtils.isNotEmpty(appId)) {
                throw new CustomException("任务处于运行状态，任务名称:" + flinkJobEntity.getJobName() + " 队列名称:" + queueName);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
