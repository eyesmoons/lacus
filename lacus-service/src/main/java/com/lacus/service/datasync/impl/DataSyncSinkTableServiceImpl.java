package com.lacus.service.datasync.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.datasync.entity.DataSyncSinkTableEntity;
import com.lacus.dao.datasync.mapper.DataSyncSinkTableMapper;
import com.lacus.service.datasync.IDataSyncSinkTableService;
import org.springframework.stereotype.Service;

@Service
public class DataSyncSinkTableServiceImpl extends ServiceImpl<DataSyncSinkTableMapper, DataSyncSinkTableEntity> implements IDataSyncSinkTableService {
}
