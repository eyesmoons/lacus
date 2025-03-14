package com.lacus.service.rtc.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.rtc.entity.DataSyncSourceTableEntity;
import com.lacus.dao.rtc.mapper.DataSyncSourceTableMapper;
import com.lacus.service.rtc.IDataSyncSourceTableService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataSyncSourceTableServiceImpl extends ServiceImpl<DataSyncSourceTableMapper, DataSyncSourceTableEntity> implements IDataSyncSourceTableService {

    @Override
    public void removeByJobId(Long jobId) {
        LambdaQueryWrapper<DataSyncSourceTableEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataSyncSourceTableEntity::getJobId, jobId);
        this.remove(wrapper);
    }

    @Override
    public List<DataSyncSourceTableEntity> listByJobIdsAndDbName(List<Long> jobIds, String dbName) {
        LambdaQueryWrapper<DataSyncSourceTableEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(DataSyncSourceTableEntity::getJobId, jobIds);
        wrapper.eq(DataSyncSourceTableEntity::getSourceDbName, dbName);
        return this.list(wrapper);
    }

    @Override
    public List<DataSyncSourceTableEntity> listByJobId(Long jobId) {
        LambdaQueryWrapper<DataSyncSourceTableEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataSyncSourceTableEntity::getJobId, jobId);
        return this.list(wrapper);
    }

    @Override
    public List<DataSyncSourceTableEntity> listByJobIds(List<Long> jobIds) {
        LambdaQueryWrapper<DataSyncSourceTableEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(DataSyncSourceTableEntity::getJobId, jobIds);
        return this.list(wrapper);
    }
}
