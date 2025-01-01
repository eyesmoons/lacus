package com.lacus.service.dataCollect.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.dataCollect.entity.DataSyncJobCatalogEntity;
import com.lacus.dao.dataCollect.mapper.DataSyncJobCatalogMapper;
import com.lacus.service.dataCollect.IDataSyncJobCatalogService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DataSyncJobCatalogServiceImpl extends ServiceImpl<DataSyncJobCatalogMapper, DataSyncJobCatalogEntity> implements IDataSyncJobCatalogService {
    @Override
    public List<DataSyncJobCatalogEntity> listByName(String catalogName) {
        LambdaQueryWrapper<DataSyncJobCatalogEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(ObjectUtils.isNotEmpty(catalogName), DataSyncJobCatalogEntity::getCatalogName, catalogName);
        wrapper.orderByDesc(DataSyncJobCatalogEntity::getUpdateTime);
        return this.list(wrapper);
    }
}
