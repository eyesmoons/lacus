package com.lacus.service.dig.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.dig.entity.StTaskEntity;
import com.lacus.dao.dig.entity.StTaskRelationEntity;
import com.lacus.dao.dig.mapper.StTaskRelationMapper;
import com.lacus.service.dig.IStTaskRelationService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class StTaskRelationServiceImpl extends ServiceImpl<StTaskRelationMapper, StTaskRelationEntity> implements IStTaskRelationService {
    @Override
    public List<StTaskRelationEntity> getTaskRelationsByJobId(Long jobId) {
        LambdaQueryWrapper<StTaskRelationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StTaskRelationEntity::getJobId, jobId);
        return this.list(wrapper);
    }
}
