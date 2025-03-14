package com.lacus.service.rtc.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.rtc.entity.DataSyncJobInstanceEntity;
import com.lacus.dao.rtc.mapper.DataSyncJobInstanceMapper;
import com.lacus.service.rtc.IDataSyncJobInstanceService;
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
