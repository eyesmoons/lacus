package com.lacus.service.rtc;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.rtc.entity.DataSyncSavedTable;
import com.lacus.dao.rtc.entity.DataSyncTableMappingEntity;

import java.util.LinkedList;
import java.util.List;

public interface IDataSyncTableMappingService extends IService<DataSyncTableMappingEntity> {
    LinkedList<DataSyncSavedTable> listSavedTables(DataSyncSavedTable query);
    LinkedList<DataSyncSavedTable> listSavedTables(List<DataSyncSavedTable> params);
    void removeByJobId(Long jobId);
}
