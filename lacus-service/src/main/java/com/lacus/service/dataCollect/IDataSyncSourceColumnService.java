package com.lacus.service.dataCollect;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.dataCollect.entity.DataSyncSourceColumnEntity;

public interface IDataSyncSourceColumnService extends IService<DataSyncSourceColumnEntity> {
    void removeByJobId(Long jobId);
}
