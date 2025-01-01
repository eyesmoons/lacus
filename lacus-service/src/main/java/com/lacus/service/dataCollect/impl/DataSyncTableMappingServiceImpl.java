package com.lacus.service.dataCollect.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.dataCollect.entity.DataSyncSavedTable;
import com.lacus.dao.dataCollect.entity.DataSyncTableMappingEntity;
import com.lacus.dao.dataCollect.mapper.DataSyncTableMappingMapper;
import com.lacus.service.dataCollect.IDataSyncTableMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class DataSyncTableMappingServiceImpl extends ServiceImpl<DataSyncTableMappingMapper, DataSyncTableMappingEntity> implements IDataSyncTableMappingService {

    @Autowired
    private DataSyncTableMappingMapper tableMappingMapper;

    @Override
    public LinkedList<DataSyncSavedTable> listSavedTables(DataSyncSavedTable query) {
        return tableMappingMapper.querySavedTables(query);
    }

    @Override
    public LinkedList<DataSyncSavedTable> listSavedTables(List<DataSyncSavedTable> list) {
        return tableMappingMapper.batchQuerySavedTables(list);
    }

    @Override
    public void removeByJobId(Long jobId) {
        LambdaQueryWrapper<DataSyncTableMappingEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataSyncTableMappingEntity::getJobId, jobId);
        this.remove(wrapper);
    }
}
