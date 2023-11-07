package com.lacus.service.datasync;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.common.utils.yarn.FlinkJobDetail;
import com.lacus.dao.datasync.entity.DataSyncJobInstanceEntity;

public interface IDataSyncJobInstanceService extends IService<DataSyncJobInstanceEntity> {
    DataSyncJobInstanceEntity getLastInstanceByJobId(Long jobId);
    void saveInstance(Long jobId, String syncType, String applicationId, FlinkJobDetail flinkJobDetail);
    void failInstance(Long jobId, String syncType, String applicationId);
}
