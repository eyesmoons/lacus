package com.lacus.service.lakeintelligence.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.lakeintelligence.entity.LakeDatasetEntity;
import com.lacus.dao.lakeintelligence.mapper.LakeDatasetMapper;
import com.lacus.service.lakeintelligence.ILakeDatasetService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

/**
 * 图片库元信息 Service 实现
 */
@Service
public class LakeDatasetServiceImpl extends ServiceImpl<LakeDatasetMapper, LakeDatasetEntity> implements ILakeDatasetService {

    @Override
    public boolean isDatasetNameDuplicated(Long id, String datasetName) {
        LambdaQueryWrapper<LakeDatasetEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LakeDatasetEntity::getDatasetName, datasetName);
        if (ObjectUtils.isNotEmpty(id)) {
            wrapper.ne(LakeDatasetEntity::getDatasetId, id);
        }
        return this.count(wrapper) > 0;
    }
}
