package com.lacus.service.datasync;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.datasync.entity.DataSyncSinkTableEntity;

import java.util.List;

public interface IDataSyncSinkTableService extends IService<DataSyncSinkTableEntity> {
    void removeByJobId(String jobId);

    List<DataSyncSinkTableEntity> listByJobId(String jobId);
}
