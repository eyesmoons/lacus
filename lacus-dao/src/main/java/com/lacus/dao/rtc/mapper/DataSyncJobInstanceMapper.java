package com.lacus.dao.rtc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lacus.dao.rtc.entity.DataSyncJobInstanceEntity;
import org.apache.ibatis.annotations.Param;

public interface DataSyncJobInstanceMapper extends BaseMapper<DataSyncJobInstanceEntity> {
    DataSyncJobInstanceEntity getLastInstanceByJobId(@Param("jobId") Long jobId);
}
