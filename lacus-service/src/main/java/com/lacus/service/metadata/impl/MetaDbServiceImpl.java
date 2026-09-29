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
        wrapper.eq(MetaDbEntity::getDeleted, 0);

        // 修复：使用 selectList 并取第一条，避免多条记录时报错
        List<MetaDbEntity> list = baseMapper.selectList(wrapper);
        if (list == null || list.isEmpty()) {
            return null;
        }

        // 如果有多条记录，返回第一条并记录警告日志
        if (list.size() > 1) {
            log.warn(String.format("发现 %s 条重复的数据库记录，datasourceId=%s, dbName=%s，将使用第一条",
                    list.size(), datasourceId, dbName));
        }

        return list.get(0);
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
