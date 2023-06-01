package com.lacus.service.datasync.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.datasync.entity.DataSyncSourceColumnEntity;
import com.lacus.dao.datasync.mapper.DataSyncSourceColumnMapper;
import com.lacus.service.datasync.IDataSyncSourceColumnService;
import org.springframework.stereotype.Service;

@Service
public class DataSyncSourceColumnServiceImpl extends ServiceImpl<DataSyncSourceColumnMapper, DataSyncSourceColumnEntity> implements IDataSyncSourceColumnService {
}
