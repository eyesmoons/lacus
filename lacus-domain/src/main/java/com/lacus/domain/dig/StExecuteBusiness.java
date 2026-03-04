package com.lacus.domain.dig;

import com.lacus.common.exception.CustomException;
import com.lacus.dao.dig.entity.StJobEntity;
import com.lacus.dao.dig.entity.StJobInstanceEntity;
import com.lacus.dao.dig.entity.StTaskEntity;
import com.lacus.dao.dig.entity.StTaskRelationEntity;
import com.lacus.enums.FlinkVersion;
import com.lacus.enums.SparkVersion;
import com.lacus.service.dig.IStJobInstanceService;
import com.lacus.service.dig.IStJobService;
import com.lacus.service.dig.IStTaskRelationService;
import com.lacus.service.dig.IStTaskService;
import com.lacus.utils.CommonPropertyUtils;
import com.lacus.utils.SeatunnelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import static com.lacus.common.constant.Constants.SEATUNNEL_HOME;

@Slf4j
@Service
public class StExecuteBusiness {

    private static final Logger logger = LoggerFactory.getLogger(StExecuteBusiness.class);

    @Autowired
    private StJobInstanceBusiness stJobInstanceBusiness;

    @Autowired
    private StJobBusiness stJobBusiness;

    @Autowired
    private IStJobService jobService;

    @Autowired
    private IStJobInstanceService instanceService;

    public void start(Long jobId) {
        logger.info("Attempting to start job with ID: {}", jobId);
        StJobEntity job = jobService.getById(jobId);
        if (ObjectUtils.isEmpty(job)) {
            logger.error("Job with ID {} does not exist", jobId);
            throw new CustomException("任务[" + jobId + "]不存在");
        }
        // 1. 构建任务json
        String jobConfig = createJobConfig(job);
        logger.debug("Generated job config for job {}: {}", jobId, jobConfig);

        // 2. 创建任务实例
        StJobInstanceEntity instance = stJobInstanceBusiness.createInstance(jobId, jobConfig);
        logger.info("Created job instance {} for job {}", instance.getInstanceId(), jobId);

        // 3. 生成配置文件
        String configFile = writeJobConfigIntoConfFile(jobConfig, instance.getInstanceId());
        // 4. 执行任务
        try {
            executeJob(job, instance, configFile);
            logger.info("Successfully started job {} with instance {}", jobId, instance.getInstanceId());
        } catch (RuntimeException e) {
            throw new CustomException("任务执行出错: " + e.getMessage());
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String writeJobConfigIntoConfFile(String jobConfig, Long jobDefineId) {
        String projectRoot = System.getProperty("user.dir");
        String filePath =
                projectRoot + File.separator + "profile" + File.separator + jobDefineId + ".conf";
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }

            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(jobConfig);
            bufferedWriter.close();

            log.info("File created and content written successfully.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return filePath;
    }

    private String createJobConfig(StJobEntity job) {
        return stJobBusiness.getJobHocon(job);
    }

    private void executeJob(StJobEntity job, StJobInstanceEntity instance, String jobConfigFile) throws IOException, ExecutionException, InterruptedException {
        logger.info("Executing job {} with engine {}", job.getJobId(), job.getEngineName());
        String seatunnelHome = CommonPropertyUtils.getString(SEATUNNEL_HOME);
        // 根据引擎类型执行任务
        String startupScript;
        switch (job.getEngineName()) {
            case "seatunnel":
                startupScript = "seatunnel.sh";
                break;
            case "flink":
                FlinkVersion flinkVersion = FlinkVersion.fromVersion(job.getEngineVersion());
                startupScript = flinkVersion.getStarupScript();
                break;
            case "spark":
                SparkVersion sparkVersion = SparkVersion.fromVersion(job.getEngineVersion());
                startupScript = sparkVersion.getStarupScript();
                break;
            default:
                throw new CustomException("不支持的引擎类型: " + job.getEngineName());
        }

        SeatunnelUtils.SeatunnelCommand command = SeatunnelUtils.buildCommand(
                seatunnelHome,
                startupScript,
                jobConfigFile,
                job.getEngineParam()
        );
        log.info("Generated Command: {}，Starting asynchronous execution...", command);
        AtomicReference<String> message = new AtomicReference<>();
        CompletableFuture<SeatunnelUtils.SeatunnelExecutionResult> asyncFuture = SeatunnelUtils.executeCommandAsync(command, line -> {
            log.info("[SeaTunnel Log] {}", line);
            String msg = message.get();
            if (ObjectUtils.isEmpty(msg)) {
                message.getAndSet(line);
            } else {
                message.getAndSet(msg + "\n" + line);
            }
        });
        // 阻塞等待异步结果
        SeatunnelUtils.SeatunnelExecutionResult asyncResult = asyncFuture.get();
        int exitCode = asyncResult.getExitCode();
        log.info("Async Exit Code: {}", exitCode);
        instance.setLogInfo(message.get());
        if (exitCode > 0) {
            instance.setStatus(1);
        } else {
            instance.setStatus(2);
        }
        instanceService.updateById(instance);
    }
}
