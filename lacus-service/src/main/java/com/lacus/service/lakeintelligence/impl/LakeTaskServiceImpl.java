package com.lacus.service.lakeintelligence.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.lakeintelligence.entity.LakeTaskEntity;
import com.lacus.dao.lakeintelligence.mapper.LakeTaskMapper;
import com.lacus.service.lakeintelligence.ILakeTaskService;
import org.springframework.stereotype.Service;

/**
 * 训练任务 Service 实现
 */
@Service
public class LakeTaskServiceImpl extends ServiceImpl<LakeTaskMapper, LakeTaskEntity> implements ILakeTaskService {
}
