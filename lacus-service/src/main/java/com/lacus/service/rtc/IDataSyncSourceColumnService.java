package com.lacus.service.rtc;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.rtc.entity.DataSyncSourceColumnEntity;

public interface IDataSyncSourceColumnService extends IService<DataSyncSourceColumnEntity> {
    void removeByJobId(Long jobId);
}
