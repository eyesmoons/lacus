package com.lacus.service.datasync;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.datasync.entity.DataSyncSinkColumnEntity;

public interface IDataSyncSinkColumnService extends IService<DataSyncSinkColumnEntity> {
    void removeByJobId(Long jobId);
}
