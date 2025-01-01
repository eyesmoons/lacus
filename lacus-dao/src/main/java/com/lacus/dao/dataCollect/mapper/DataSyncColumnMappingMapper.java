package com.lacus.dao.dataCollect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lacus.dao.dataCollect.entity.DataSyncColumnMappingEntity;
import com.lacus.dao.dataCollect.entity.DataSyncSavedColumn;

import java.util.List;

public interface DataSyncColumnMappingMapper extends BaseMapper<DataSyncColumnMappingEntity> {
    List<DataSyncSavedColumn> querySavedColumns(DataSyncSavedColumn model);
}
