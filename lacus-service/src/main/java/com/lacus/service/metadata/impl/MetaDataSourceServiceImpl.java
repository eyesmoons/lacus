package com.lacus.service.metadata.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.metadata.entity.MetaDatasourceEntity;
import com.lacus.dao.metadata.mapper.MetaDatasourceMapper;
import com.lacus.service.metadata.IMetaDataSourceService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetaDataSourceServiceImpl extends ServiceImpl<MetaDatasourceMapper, MetaDatasourceEntity> implements IMetaDataSourceService {
    @Override
    public boolean isDatasourceNameDuplicated(Long datasourceId, String datasourceName) {
        LambdaQueryWrapper<MetaDatasourceEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(datasourceId != null, MetaDatasourceEntity::getDatasourceId, datasourceId);
        queryWrapper.eq(MetaDatasourceEntity::getDatasourceName, datasourceName);
        return this.baseMapper.exists(queryWrapper);
    }

    @Override
    public List<MetaDatasourceEntity> getDatasourceList(String datasourceName, String sourceType) {
        LambdaQueryWrapper<MetaDatasourceEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ObjectUtils.isNotEmpty(datasourceName), MetaDatasourceEntity::getDatasourceName, datasourceName);
        queryWrapper.eq(ObjectUtils.isNotEmpty(sourceType), MetaDatasourceEntity::getSourceType, sourceType);
        return baseMapper.selectList(queryWrapper);
    }
}
