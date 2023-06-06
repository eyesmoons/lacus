package com.lacus.service.datasync.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.datasync.entity.DataSyncSavedTable;
import com.lacus.dao.datasync.entity.DataSyncTableMappingEntity;
import com.lacus.dao.datasync.mapper.DataSyncTableMappingMapper;
import com.lacus.service.datasync.IDataSyncTableMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class DataSyncTableMappingServiceImpl extends ServiceImpl<DataSyncTableMappingMapper, DataSyncTableMappingEntity> implements IDataSyncTableMappingService {

    @Autowired
    private DataSyncTableMappingMapper tableMappingMapper;

    @Override
    public LinkedList<DataSyncSavedTable> listSavedTables(Long jobId) {
        return tableMappingMapper.querySavedTables(jobId);
    }

    @Override
    public LinkedList<DataSyncSavedTable> listSavedTables(List<DataSyncSavedTable> list) {
        return tableMappingMapper.batchQuerySavedTables(list);
    }
}
