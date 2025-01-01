package com.lacus.service.dataCollect.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.dataCollect.entity.DataSyncSinkTableEntity;
import com.lacus.dao.dataCollect.mapper.DataSyncSinkTableMapper;
import com.lacus.service.dataCollect.IDataSyncSinkTableService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataSyncSinkTableServiceImpl extends ServiceImpl<DataSyncSinkTableMapper, DataSyncSinkTableEntity> implements IDataSyncSinkTableService {
    @Override
    public void removeByJobId(Long jobId) {
        LambdaQueryWrapper<DataSyncSinkTableEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataSyncSinkTableEntity::getJobId, jobId);
        this.remove(wrapper);
    }

    @Override
    public List<DataSyncSinkTableEntity> listByJobId(Long jobId) {
        LambdaQueryWrapper<DataSyncSinkTableEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataSyncSinkTableEntity::getJobId, jobId);
        return this.list(wrapper);
    }

    @Override
    public List<DataSyncSinkTableEntity> listByJobIds(List<Long> jobIds) {
        LambdaQueryWrapper<DataSyncSinkTableEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(DataSyncSinkTableEntity::getJobId, jobIds);
        return this.list(wrapper);
    }
}
