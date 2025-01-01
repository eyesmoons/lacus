package com.lacus.dao.dataCollect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lacus.dao.dataCollect.entity.DataSyncJobInstanceEntity;
import org.apache.ibatis.annotations.Param;

public interface DataSyncJobInstanceMapper extends BaseMapper<DataSyncJobInstanceEntity> {
    DataSyncJobInstanceEntity getLastInstanceByJobId(@Param("jobId") Long jobId);
}
