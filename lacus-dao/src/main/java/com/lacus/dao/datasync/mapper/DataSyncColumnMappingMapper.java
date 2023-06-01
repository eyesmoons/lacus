package com.lacus.dao.datasync.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lacus.dao.datasync.entity.DataSyncColumnMappingEntity;
import com.lacus.dao.datasync.entity.DataSyncSavedColumn;

import java.util.List;

public interface DataSyncColumnMappingMapper extends BaseMapper<DataSyncColumnMappingEntity> {
    List<DataSyncSavedColumn> querySavedColumns(DataSyncSavedColumn model);
}
