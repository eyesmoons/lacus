package com.lacus.service.datasync.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.datasync.entity.DataSyncJobEntity;
import com.lacus.dao.datasync.mapper.DataSyncJobMapper;
import com.lacus.service.datasync.IDataSyncJobService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class
DataSyncJobServiceImpl extends ServiceImpl<DataSyncJobMapper, DataSyncJobEntity> implements IDataSyncJobService {
    @Override
    public List<DataSyncJobEntity> listBySourceDatasourceId(Long datasourceId) {
        LambdaQueryWrapper<DataSyncJobEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataSyncJobEntity::getSourceDatasourceId, datasourceId);
        return this.list(wrapper);
    }

    @Override
    public List<DataSyncJobEntity> listSavedTableByJobId(Long jobId) {
        LambdaQueryWrapper<DataSyncJobEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataSyncJobEntity::getJobId, jobId);
        return this.list(wrapper);
    }
}
