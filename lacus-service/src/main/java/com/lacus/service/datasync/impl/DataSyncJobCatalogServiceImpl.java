package com.lacus.service.datasync.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.datasync.entity.DataSyncJobCatalogEntity;
import com.lacus.dao.datasync.mapper.DataSyncJobCatalogMapper;
import com.lacus.service.datasync.IDataSyncJobCatalogService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DataSyncJobCatalogServiceImpl extends ServiceImpl<DataSyncJobCatalogMapper, DataSyncJobCatalogEntity> implements IDataSyncJobCatalogService {
    @Override
    public List<DataSyncJobCatalogEntity> listByName(String catalogName) {
        LambdaQueryWrapper<DataSyncJobCatalogEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(ObjectUtils.isNotEmpty(catalogName), DataSyncJobCatalogEntity::getCatalogName, catalogName);
        return this.list(wrapper);
    }
}
