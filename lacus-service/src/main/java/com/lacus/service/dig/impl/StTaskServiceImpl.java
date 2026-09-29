package com.lacus.service.dig.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.dig.entity.StTaskEntity;
import com.lacus.dao.dig.mapper.StTaskMapper;
import com.lacus.service.dig.IStTaskService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StTaskServiceImpl extends ServiceImpl<StTaskMapper, StTaskEntity> implements IStTaskService {

    @Override
    public List<StTaskEntity> getTaskListByJobId(Long jobId) {
        LambdaQueryWrapper<StTaskEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StTaskEntity::getJobId, jobId);
        return this.list(wrapper);
    }
}
