package com.lacus.service.datasync;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.datasync.entity.DataSyncColumnMappingEntity;
import com.lacus.dao.datasync.entity.DataSyncSavedColumn;

import java.util.List;

public interface IDataSyncColumnMappingService extends IService<DataSyncColumnMappingEntity> {
    List<DataSyncSavedColumn> querySavedColumns(DataSyncSavedColumn tpl);
    void removeByJobId(String jobId);
}
