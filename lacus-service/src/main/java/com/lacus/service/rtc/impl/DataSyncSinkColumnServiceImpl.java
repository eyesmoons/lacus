package com.lacus.service.rtc.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.rtc.entity.DataSyncSinkColumnEntity;
import com.lacus.dao.rtc.mapper.DataSyncSinkColumnMapper;
import com.lacus.service.rtc.IDataSyncSinkColumnService;
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
