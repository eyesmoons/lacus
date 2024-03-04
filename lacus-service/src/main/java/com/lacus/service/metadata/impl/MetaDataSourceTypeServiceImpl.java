package com.lacus.service.metadata.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.metadata.entity.MetaDatasourceTypeEntity;
import com.lacus.dao.metadata.mapper.MetaDatasourceTypeMapper;
import com.lacus.service.metadata.IMetaDataSourceTypeService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetaDataSourceTypeServiceImpl extends ServiceImpl<MetaDatasourceTypeMapper, MetaDatasourceTypeEntity> implements IMetaDataSourceTypeService {
    @Override
    public List<MetaDatasourceTypeEntity> listDatasourceType(String typeName) {
        LambdaQueryWrapper<MetaDatasourceTypeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(ObjectUtils.isNotEmpty(typeName), MetaDatasourceTypeEntity::getTypeName, typeName);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public boolean isTypeNameDuplicated(Long typeId, String typeName) {
        LambdaQueryWrapper<MetaDatasourceTypeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(typeId != null, MetaDatasourceTypeEntity::getTypeId, typeId);
        queryWrapper.eq(MetaDatasourceTypeEntity::getTypeName, typeName);
        return this.baseMapper.exists(queryWrapper);
    }
}
