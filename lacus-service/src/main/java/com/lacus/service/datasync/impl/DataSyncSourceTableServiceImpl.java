package com.lacus.service.datasync.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.datasync.entity.DataSyncSourceTableEntity;
import com.lacus.dao.datasync.mapper.DataSyncSourceTableMapper;
import com.lacus.service.datasync.IDataSyncSourceTableService;
import org.springframework.stereotype.Service;

@Service
public class DataSyncSourceTableServiceImpl extends ServiceImpl<DataSyncSourceTableMapper, DataSyncSourceTableEntity> implements IDataSyncSourceTableService {
}
