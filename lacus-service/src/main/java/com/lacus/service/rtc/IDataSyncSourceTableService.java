package com.lacus.service.rtc;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.rtc.entity.DataSyncSourceTableEntity;

import java.util.List;

public interface IDataSyncSourceTableService extends IService<DataSyncSourceTableEntity> {
    void removeByJobId(Long jobId);
    List<DataSyncSourceTableEntity> listByJobIdsAndDbName(List<Long> jobIds, String dbName);
    List<DataSyncSourceTableEntity> listByJobId(Long jobId);
    List<DataSyncSourceTableEntity> listByJobIds(List<Long> jobIds);
}
