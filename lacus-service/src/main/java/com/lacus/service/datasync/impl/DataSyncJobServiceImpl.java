package com.lacus.service.datasync.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.datasync.entity.DataSyncJobEntity;
import com.lacus.dao.datasync.mapper.DataSyncJobMapper;
import com.lacus.service.datasync.IDataSyncJobService;
import org.springframework.stereotype.Service;

@Service
public class
DataSyncJobServiceImpl extends ServiceImpl<DataSyncJobMapper, DataSyncJobEntity> implements IDataSyncJobService {
}
