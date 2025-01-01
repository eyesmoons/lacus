package com.lacus.service.dataCollect;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.dataCollect.entity.DataSyncSinkColumnEntity;

public interface IDataSyncSinkColumnService extends IService<DataSyncSinkColumnEntity> {
    void removeByJobId(Long jobId);
}
