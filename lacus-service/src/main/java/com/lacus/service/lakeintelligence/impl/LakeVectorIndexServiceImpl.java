package com.lacus.service.lakeintelligence.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.lakeintelligence.entity.LakeVectorIndexEntity;
import com.lacus.dao.lakeintelligence.mapper.LakeVectorIndexMapper;
import com.lacus.service.lakeintelligence.ILakeVectorIndexService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

/**
 * 向量库索引 Service 实现
 */
@Service
public class LakeVectorIndexServiceImpl extends ServiceImpl<LakeVectorIndexMapper, LakeVectorIndexEntity> implements ILakeVectorIndexService {

    @Override
    public boolean isIndexNameDuplicated(Long id, String indexName) {
        LambdaQueryWrapper<LakeVectorIndexEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LakeVectorIndexEntity::getIndexName, indexName);
        if (ObjectUtils.isNotEmpty(id)) {
            wrapper.ne(LakeVectorIndexEntity::getIndexId, id);
        }
        return this.count(wrapper) > 0;
    }
}
