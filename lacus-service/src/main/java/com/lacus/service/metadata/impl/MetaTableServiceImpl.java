package com.lacus.service.metadata.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.metadata.entity.MetaTableEntity;
import com.lacus.dao.metadata.mapper.MetaTableMapper;
import com.lacus.service.metadata.IMetaTableService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetaTableServiceImpl extends ServiceImpl<MetaTableMapper, MetaTableEntity> implements IMetaTableService {
    @Override
    public boolean isMetaTableExists(Long dbId, String tableName) {
        LambdaQueryWrapper<MetaTableEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectUtils.isNotEmpty(dbId), MetaTableEntity::getDbId, dbId);
        wrapper.eq(ObjectUtils.isNotEmpty(tableName), MetaTableEntity::getTableName, tableName);
        return baseMapper.exists(wrapper);
    }

    @Override
    public MetaTableEntity getMetaTable(Long dbId, String tableName) {
        LambdaQueryWrapper<MetaTableEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectUtils.isNotEmpty(dbId), MetaTableEntity::getDbId, dbId);
        wrapper.eq(ObjectUtils.isNotEmpty(tableName), MetaTableEntity::getTableName, tableName);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public List<MetaTableEntity> getMetaTables(List<Long> dbIds) {
        LambdaQueryWrapper<MetaTableEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ObjectUtils.isNotEmpty(dbIds), MetaTableEntity::getDbId, dbIds);
        return baseMapper.selectList(wrapper);
    }
}
