package com.lacus.service.dataCollect;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.dataCollect.entity.DataSyncColumnMappingEntity;
import com.lacus.dao.dataCollect.entity.DataSyncSavedColumn;

import java.util.List;

public interface IDataSyncColumnMappingService extends IService<DataSyncColumnMappingEntity> {
    List<DataSyncSavedColumn> querySavedColumns(DataSyncSavedColumn tpl);
    void removeByJobId(Long jobId);
}
