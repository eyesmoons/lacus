package com.lacus.domain.datasync.job;

import com.alibaba.fastjson2.JSON;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.CustomException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.common.utils.yarn.FlinkParams;
import com.lacus.common.utils.yarn.YarnUtil;
import com.lacus.dao.datasync.entity.DataSyncJobEntity;
import com.lacus.dao.datasync.entity.DataSyncJobInstanceEntity;
import com.lacus.dao.datasync.enums.FlinkStatusEnum;
import com.lacus.domain.common.dto.JobConf;
import com.lacus.domain.common.utils.JobUtil;
import com.lacus.domain.datasync.instance.JobInstanceService;
import com.lacus.service.datasync.IDataSyncColumnMappingService;
import com.lacus.service.datasync.IDataSyncJobInstanceService;
import com.lacus.service.datasync.IDataSyncJobService;
import com.lacus.service.datasync.IDataSyncSinkTableService;
import com.lacus.service.datasync.IDataSyncSourceTableService;
import com.lacus.service.datasync.IDataSyncTableMappingService;
import com.lacus.service.metadata.IMetaDataSourceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class JobOperationService {

    @Autowired
    private IDataSyncJobService jobService;

    @Autowired
    private JobInstanceService instanceService;

    @Autowired
    private IDataSyncJobInstanceService dataSyncJobInstanceService;

    @Autowired
    private IDataSyncSourceTableService sourceTableService;

    @Autowired
    private IDataSyncSinkTableService sinkTableService;

    @Autowired
    private IDataSyncTableMappingService tableMappingService;

    @Autowired
    private IDataSyncColumnMappingService columnMappingService;

    @Autowired
    private IMetaDataSourceService dataSourceService;

    @Autowired
    private JobMonitorService monitorService;

    @Autowired
    private JobUtil jobUtil;

    private static final String JOB_MAIN_CLASS = "com.lacus.job.flink.impl.DataSyncJob";

    @Value("${flink.jar-name}")
    private String flinkJobJarName;

    @Value("${flink.conf-path}")
    private String flinkConfPath;

    @Value("${hdfs.defaultFS}")
    private String defaultHdfs;

    @Value("${flink.lib-path}")
    private String flinkLibs;

    @Value("${flink.dist-jar-path}")
    private String flinkDistJar;

    /**
     * 启动任务
     *
     * @param jobId     任务ID
     * @param syncType  启动方式
     * @param timeStamp 指定时间戳
     */
    public void submitJob(Long jobId, String syncType, String timeStamp) {
        DataSyncJobEntity job = jobService.getById(jobId);
        if (ObjectUtils.isEmpty(job)) {
            throw new ApiException(ErrorCode.Internal.DB_INTERNAL_ERROR, "未查询到任务信息");
        }

        String jobName = job.getJobName();
        FlinkParams flinkParams = new FlinkParams();
        flinkParams.setMasterMemoryMB(job.getJobManager() * 1024);
        flinkParams.setTaskManagerMemoryMB(job.getTaskManager() * 1024);
        flinkParams.setJobName(jobName);

        // 构建任务json
        JobConf jobConf = jobUtil.buildJobConf(job, syncType, timeStamp);
        log.info("jobConf：{}", JSON.toJSONString(jobConf));
        try {
            DataSyncJobInstanceEntity instance = instanceService.saveInstance(job, syncType, timeStamp, JSON.toJSONString(jobConf));
            jobConf.getJobInfo().setInstanceId(instance.getInstanceId());
            String flinkJobPath = jobUtil.getJobJarPath(flinkJobJarName, defaultHdfs);
            String applicationId = YarnUtil.deployOnYarn(JOB_MAIN_CLASS,
                    new String[]{"mysql", jobName, JSON.toJSONString(jobConf)},
                    jobName,
                    flinkParams,
                    flinkJobPath,
                    flinkConfPath,
                    jobConf.getSource().getSavePoints(),
                    defaultHdfs,
                    flinkLibs,
                    flinkDistJar);

            if (Objects.nonNull(applicationId)) {
                instanceService.updateInstance(instance, applicationId);
            } else {
                log.error("任务提交失败");
            }
        } catch (Exception e) {
            log.error("任务提交失败", e);
            // 停止任务
            jobUtil.doStop(jobId, 1);
        }
    }

    public void stopJob(Long jobId) {
        try {
            DataSyncJobInstanceEntity lastInstance = dataSyncJobInstanceService.getLastInstanceByJobId(jobId);
            if (ObjectUtils.isNotEmpty(lastInstance)) {
                doStopWithoutSavePoint(lastInstance);
            }
        } catch (Exception e) {
            log.error("任务停止失败：{}", e.getMessage());
        }
    }

    /**
     * 停止任务
     *
     * @param instance 任务实例
     */
    private void doStopWithoutSavePoint(DataSyncJobInstanceEntity instance) {
        DataSyncJobEntity job = jobService.getById(instance.getJobId());
        if (Objects.isNull(job)) {
            throw new CustomException("任务不存在");
        }
        if (!FlinkStatusEnum.couldStop(instance.getStatus())) {
            log.warn("当前状态无法停止：{}", instance.getStatus());
            jobUtil.updateStopStatusForInstance(instance);
        } else {
            String applicationId = instance.getApplicationId();
            String flinkJobId = monitorService.getFlinkJobIdWithRetry(applicationId);
            try {
                for (int i = 0; i < 5; i++) {
                    // 停止flink任务
                    YarnUtil.cancelYarnJob(applicationId, flinkJobId, flinkConfPath, defaultHdfs);
                }
                // 修改任务状态
                jobUtil.updateStopStatusForInstance(instance);
            } catch (Exception e) {
                log.error("flink任务停止失败：", e);
                // 修改任务状态
                jobUtil.updateStopStatusForInstance(instance);
                throw new CustomException("flink任务停止失败");
            }
        }
    }
}
