package com.lacus.service.dataCollect;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.dataCollect.entity.DataSyncSinkTableEntity;

import java.util.List;

public interface IDataSyncSinkTableService extends IService<DataSyncSinkTableEntity> {
    void removeByJobId(Long jobId);

    List<DataSyncSinkTableEntity> listByJobId(Long jobId);

    List<DataSyncSinkTableEntity> listByJobIds(List<Long> jobIds);
}
