package com.lacus.service.metadata.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.metadata.entity.MetaDbTableEntity;
import com.lacus.dao.metadata.entity.MetaTableEntity;
import com.lacus.dao.metadata.mapper.MetaTableMapper;
import com.lacus.service.metadata.IMetaTableService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MetaTableServiceImpl extends ServiceImpl<MetaTableMapper, MetaTableEntity> implements IMetaTableService {

    @Autowired
    private MetaTableMapper tableMapper;

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
        wrapper.eq(MetaTableEntity::getDeleted, 0);

        List<MetaTableEntity> list = baseMapper.selectList(wrapper);
        if (ObjectUtils.isEmpty(list)) {
            return null;
        }
        // 如果有多条记录，返回第一条并记录警告日志
        if (list.size() > 1) {
            log.warn(String.format("发现 %s 条重复的数据库记录，dbId=%s, tableName=%s，将使用第一条",
                    list.size(), dbId, tableName));
        }
        return list.get(0);
    }

    @Override
    public List<MetaTableEntity> getMetaTables(List<Long> dbIds) {
        LambdaQueryWrapper<MetaTableEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ObjectUtils.isNotEmpty(dbIds), MetaTableEntity::getDbId, dbIds);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<MetaTableEntity> searchTablesByKeyword(String tableName) {
        if (ObjectUtils.isEmpty(tableName)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<MetaTableEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(MetaTableEntity::getTableName, tableName)
                .orderByAsc(MetaTableEntity::getTableId)
                .last("limit 200");
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<MetaDbTableEntity> getMetaTables(Long datasourceId, String dbName) {
        return tableMapper.listMetaDbTable(datasourceId, dbName, null);
    }

    @Override
    public List<MetaDbTableEntity> listMetaTable(List<MetaDbTableEntity> params) {
        return tableMapper.listMetaTable(params);
    }
}
