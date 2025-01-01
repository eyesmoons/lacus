package com.lacus.service.dataCollect;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.dataCollect.entity.DataSyncJobInstanceEntity;

public interface IDataSyncJobInstanceService extends IService<DataSyncJobInstanceEntity> {
    DataSyncJobInstanceEntity getLastInstanceByJobId(Long jobId);
}
