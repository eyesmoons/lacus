package com.lacus.service.datasync.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.datasync.entity.DataSyncJobInstanceEntity;
import com.lacus.dao.datasync.mapper.DataSyncJobInstanceMapper;
import com.lacus.service.datasync.IDataSyncJobInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataSyncJobInstanceServiceImpl extends ServiceImpl<DataSyncJobInstanceMapper, DataSyncJobInstanceEntity> implements IDataSyncJobInstanceService{

    @Autowired
    private DataSyncJobInstanceMapper mapper;

    @Override
    public DataSyncJobInstanceEntity getLastInstanceByJobId(Long jobId) {
        return mapper.getLastInstanceByJobId(jobId);
    }
}
