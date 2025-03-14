package com.lacus.service.rtc;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.rtc.entity.DataSyncSinkTableEntity;

import java.util.List;

public interface IDataSyncSinkTableService extends IService<DataSyncSinkTableEntity> {
    void removeByJobId(Long jobId);

    List<DataSyncSinkTableEntity> listByJobId(Long jobId);

    List<DataSyncSinkTableEntity> listByJobIds(List<Long> jobIds);
}
