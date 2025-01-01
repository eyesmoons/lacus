package com.lacus.service.dataCollect.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.dataCollect.entity.DataSyncSinkColumnEntity;
import com.lacus.dao.dataCollect.mapper.DataSyncSinkColumnMapper;
import com.lacus.service.dataCollect.IDataSyncSinkColumnService;
import org.springframework.stereotype.Service;

@Service
public class DataSyncSinkColumnServiceImpl extends ServiceImpl<DataSyncSinkColumnMapper, DataSyncSinkColumnEntity> implements IDataSyncSinkColumnService {

    @Override
    public void removeByJobId(Long jobId) {
        LambdaQueryWrapper<DataSyncSinkColumnEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataSyncSinkColumnEntity::getJobId, jobId);
        this.remove(wrapper);
    }
}
