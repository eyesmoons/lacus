package com.lacus.service.datasync.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.datasync.entity.DataSyncJobCatelogEntity;
import com.lacus.dao.datasync.mapper.DataSyncJobCatelogMapper;
import com.lacus.service.datasync.IDataSyncJobCatelogService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataSyncJobCatelogServiceImpl extends ServiceImpl<DataSyncJobCatelogMapper, DataSyncJobCatelogEntity> implements IDataSyncJobCatelogService {
    @Override
    public List<DataSyncJobCatelogEntity> listByName(String catelogName) {
        LambdaQueryWrapper<DataSyncJobCatelogEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(ObjectUtils.isNotEmpty(catelogName), DataSyncJobCatelogEntity::getCatelogName, catelogName);
        return this.list(wrapper);
    }
}
