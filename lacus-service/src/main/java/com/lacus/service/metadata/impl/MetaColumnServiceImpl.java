package com.lacus.service.metadata.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.metadata.entity.MetaColumnEntity;
import com.lacus.dao.metadata.mapper.MetaColumnMapper;
import com.lacus.service.metadata.IMetaColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetaColumnServiceImpl extends ServiceImpl<MetaColumnMapper, MetaColumnEntity> implements IMetaColumnService {

    @Autowired
    private MetaColumnMapper metaColumnMapper;

    @Override
    public List<MetaColumnEntity> getColumnsByTableId(Long tableId) {
        LambdaQueryWrapper<MetaColumnEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MetaColumnEntity::getTableId, tableId);
        return this.list(wrapper);
    }

    @Override
    public void removeColumnsByTableIds(List<Long> tableIds) {
        LambdaQueryWrapper<MetaColumnEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(MetaColumnEntity::getTableId, tableIds);
        this.remove(wrapper);
    }

    @Override
    public boolean removeColumnsByTableId(Long tableId) {
        LambdaQueryWrapper<MetaColumnEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MetaColumnEntity::getTableId, tableId);
        return this.remove(wrapper);
    }

    @Override
    public List<MetaColumnEntity> getColumnsByTableName(Long datasourceId, String dbName, String tableName) {
        return metaColumnMapper.selectColumnsByTableName(datasourceId, dbName, tableName);
    }
}
