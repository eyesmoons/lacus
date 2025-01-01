package com.lacus.dao.dataCollect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lacus.dao.dataCollect.entity.DataSyncSavedTable;
import com.lacus.dao.dataCollect.entity.DataSyncTableMappingEntity;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedList;
import java.util.List;

public interface DataSyncTableMappingMapper extends BaseMapper<DataSyncTableMappingEntity> {
    LinkedList<DataSyncSavedTable> querySavedTables(DataSyncSavedTable query);

    LinkedList<DataSyncSavedTable> batchQuerySavedTables(@Param("list") List<DataSyncSavedTable> list);
}
