package com.lacus.service.rtc;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.rtc.entity.DataSyncColumnMappingEntity;
import com.lacus.dao.rtc.entity.DataSyncSavedColumn;

import java.util.List;

public interface IDataSyncColumnMappingService extends IService<DataSyncColumnMappingEntity> {
    List<DataSyncSavedColumn> querySavedColumns(DataSyncSavedColumn tpl);
    void removeByJobId(Long jobId);
}
