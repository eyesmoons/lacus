package com.lacus.dao.rtc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lacus.dao.rtc.entity.DataSyncColumnMappingEntity;
import com.lacus.dao.rtc.entity.DataSyncSavedColumn;

import java.util.List;

public interface DataSyncColumnMappingMapper extends BaseMapper<DataSyncColumnMappingEntity> {
    List<DataSyncSavedColumn> querySavedColumns(DataSyncSavedColumn model);
}
