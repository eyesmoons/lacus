package com.lacus.domain.datasync.instance;

import com.lacus.common.utils.time.DateUtils;
import com.lacus.common.utils.yarn.FlinkJobDetail;
import com.lacus.dao.datasync.entity.DataSyncJobEntity;
import com.lacus.dao.datasync.entity.DataSyncJobInstanceEntity;
import com.lacus.dao.datasync.enums.FlinkStatusEnum;
import com.lacus.domain.common.utils.JobUtil;
import com.lacus.domain.datasync.job.JobMonitorService;
import com.lacus.service.datasync.IDataSyncJobInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
 * @created by shengyu on 2024/2/26 21:32
 */

@Service
public class JobInstanceService {

    @Autowired
    private IDataSyncJobInstanceService dataSyncJobInstanceService;

    @Autowired
    private JobMonitorService monitorService;

    @Autowired
    private JobUtil jobUtil;

    public void updateInstance(DataSyncJobInstanceEntity instance, String applicationId) {
        try {
            if (Objects.isNull(applicationId)) {
                instance.setFinishedTime(new Date());
                instance.setStatus(FlinkStatusEnum.STOP.getStatus());
                dataSyncJobInstanceService.saveOrUpdate(instance);
            }
            instance.setApplicationId(applicationId);
            FlinkJobDetail jobDetail = monitorService.flinkJobDetail(applicationId);
            instance.setSubmitTime(DateUtils.getDate(jobDetail.getStartTime()));
            instance.setStatus(jobUtil.convertTaskStatus(jobDetail.getState()));
            if (jobDetail.getEndTime() > 0) {
                instance.setFinishedTime(DateUtils.getDate(jobDetail.getEndTime()));
            }
            instance.setFlinkJobId(jobDetail.getJid());
            dataSyncJobInstanceService.saveOrUpdate(instance);
        } catch (Exception e) {
            jobUtil.updateStopStatusForInstance(instance);
        }
    }

    public DataSyncJobInstanceEntity saveInstance(DataSyncJobEntity job, String syncType, String timeStamp, String jobScript) {
        DataSyncJobInstanceEntity entity = new DataSyncJobInstanceEntity();
        entity.setJobId(job.getJobId());
        entity.setInstanceName(jobUtil.genInstanceNam(job.getJobName()));
        entity.setSyncType(syncType);
        entity.setTimeStamp(timeStamp);
        entity.setJobScript(jobScript);
        entity.insert();
        return entity;
    }
}