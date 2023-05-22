package com.lacus.service.metadata.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.metadata.entity.MetaDbEntity;
import com.lacus.dao.metadata.mapper.MetaDbMapper;
import com.lacus.service.metadata.IMetaDbService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetaDbServiceImpl extends ServiceImpl<MetaDbMapper, MetaDbEntity> implements IMetaDbService {

    @Override
    public boolean isMetaDbExists(Long datasourceId, String dbName) {
        LambdaQueryWrapper<MetaDbEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectUtils.isNotEmpty(datasourceId), MetaDbEntity::getDatasourceId, datasourceId);
        wrapper.eq(ObjectUtils.isNotEmpty(dbName), MetaDbEntity::getDbName, dbName);
        return baseMapper.exists(wrapper);
    }

    @Override
    public MetaDbEntity getMetaDb(Long datasourceId, String dbName) {
        LambdaQueryWrapper<MetaDbEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectUtils.isNotEmpty(datasourceId), MetaDbEntity::getDatasourceId, datasourceId);
        wrapper.eq(ObjectUtils.isNotEmpty(dbName), MetaDbEntity::getDbName, dbName);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public List<MetaDbEntity> listByDatasourceId(Long datasourceId) {
        LambdaQueryWrapper<MetaDbEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectUtils.isNotEmpty(datasourceId), MetaDbEntity::getDatasourceId, datasourceId);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<MetaDbEntity> getMetaDbs(Long datasourceId, List<String> dbNames) {
        LambdaQueryWrapper<MetaDbEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectUtils.isNotEmpty(datasourceId), MetaDbEntity::getDatasourceId, datasourceId);
        wrapper.in(ObjectUtils.isNotEmpty(dbNames), MetaDbEntity::getDbName, dbNames);
        return baseMapper.selectList(wrapper);
    }
}
