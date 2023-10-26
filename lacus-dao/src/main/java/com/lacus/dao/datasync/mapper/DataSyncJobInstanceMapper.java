package com.lacus.dao.datasync.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lacus.dao.datasync.entity.DataSyncJobInstanceEntity;
import org.apache.ibatis.annotations.Param;

public interface DataSyncJobInstanceMapper extends BaseMapper<DataSyncJobInstanceEntity> {
    DataSyncJobInstanceEntity getLastInstanceByJobId(@Param("catalogId") String catalogId);
}
