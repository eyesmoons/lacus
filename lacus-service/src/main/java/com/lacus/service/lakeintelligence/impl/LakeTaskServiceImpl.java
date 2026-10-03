package com.lacus.service.lakeintelligence.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.lakeintelligence.entity.LakeTaskEntity;
import com.lacus.dao.lakeintelligence.mapper.LakeTaskMapper;
import com.lacus.service.lakeintelligence.ILakeTaskService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

/**
 * 训练任务 Service 实现
 */
@Service
public class LakeTaskServiceImpl extends ServiceImpl<LakeTaskMapper, LakeTaskEntity> implements ILakeTaskService {

    @Override
    public boolean isTaskNameDuplicated(Long id, String taskName) {
        LambdaQueryWrapper<LakeTaskEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LakeTaskEntity::getTaskName, taskName);
        if (ObjectUtils.isNotEmpty(id)) {
            wrapper.ne(LakeTaskEntity::getTaskId, id);
        }
        return this.count(wrapper) > 0;
    }
}
