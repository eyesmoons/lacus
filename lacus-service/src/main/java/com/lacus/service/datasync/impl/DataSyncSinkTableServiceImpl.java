package com.lacus.service.datasync.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.datasync.entity.DataSyncSinkTableEntity;
import com.lacus.dao.datasync.mapper.DataSyncSinkTableMapper;
import com.lacus.service.datasync.IDataSyncSinkTableService;
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
}
