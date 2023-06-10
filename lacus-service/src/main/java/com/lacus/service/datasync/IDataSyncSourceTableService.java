package com.lacus.service.datasync;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.datasync.entity.DataSyncSourceTableEntity;

import java.util.List;

public interface IDataSyncSourceTableService extends IService<DataSyncSourceTableEntity> {
    void removeByJobId(String jobId);
    List<DataSyncSourceTableEntity> listByJobIdsAndDbName(List<String> jobIds, String dbName);
    List<DataSyncSourceTableEntity> listByJobId(String jobId);
    List<DataSyncSourceTableEntity> listByJobIds(List<String> jobIds);
}
