package com.lacus.service.rtc;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.rtc.entity.DataSyncSinkColumnEntity;

public interface IDataSyncSinkColumnService extends IService<DataSyncSinkColumnEntity> {
    void removeByJobId(Long jobId);
}
