package com.lacus.service.datasync;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.common.utils.yarn.FlinkJobDetail;
import com.lacus.dao.datasync.entity.DataSyncJobInstanceEntity;

public interface IDataSyncJobInstanceService extends IService<DataSyncJobInstanceEntity> {
    DataSyncJobInstanceEntity getLastInstanceByJobId(String catalogId);
    void saveInstance(String catalogId, String syncType, String applicationId, FlinkJobDetail flinkJobDetail);
    void failInstance(String catalogId, String syncType, String applicationId);
}
