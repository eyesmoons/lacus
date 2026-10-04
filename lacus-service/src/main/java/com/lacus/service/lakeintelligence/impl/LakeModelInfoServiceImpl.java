package com.lacus.service.lakeintelligence.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.lakeintelligence.entity.LakeModelInfoEntity;
import com.lacus.dao.lakeintelligence.mapper.LakeModelInfoMapper;
import com.lacus.service.lakeintelligence.ILakeModelInfoService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

/**
 * 模型元信息 Service 实现
 */
@Service
public class LakeModelInfoServiceImpl extends ServiceImpl<LakeModelInfoMapper, LakeModelInfoEntity> implements ILakeModelInfoService {

    @Override
    public boolean isModelNameDuplicated(Long id, String modelName) {
        LambdaQueryWrapper<LakeModelInfoEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LakeModelInfoEntity::getModelName, modelName);
        if (ObjectUtils.isNotEmpty(id)) {
            wrapper.ne(LakeModelInfoEntity::getModelId, id);
        }
        return this.count(wrapper) > 0;
    }
}
