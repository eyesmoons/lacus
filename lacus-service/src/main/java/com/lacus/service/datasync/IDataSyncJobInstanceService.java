package com.lacus.service.datasync;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.datasync.entity.DataSyncJobInstanceEntity;

public interface IDataSyncJobInstanceService extends IService<DataSyncJobInstanceEntity> {
    DataSyncJobInstanceEntity getLastInstanceByJobId(Long jobId);
}
