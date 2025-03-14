package com.lacus.service.rtc.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.rtc.entity.DataSyncSourceColumnEntity;
import com.lacus.dao.rtc.mapper.DataSyncSourceColumnMapper;
import com.lacus.service.rtc.IDataSyncSourceColumnService;
import org.springframework.stereotype.Service;

@Service
public class DataSyncSourceColumnServiceImpl extends ServiceImpl<DataSyncSourceColumnMapper, DataSyncSourceColumnEntity> implements IDataSyncSourceColumnService {

    @Override
    public void removeByJobId(Long jobId) {
        LambdaQueryWrapper<DataSyncSourceColumnEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataSyncSourceColumnEntity::getJobId, jobId);
        this.remove(wrapper);
    }
}
