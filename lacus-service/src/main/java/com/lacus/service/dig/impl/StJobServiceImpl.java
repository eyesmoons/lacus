package com.lacus.service.dig.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.dig.entity.StJobEntity;
import com.lacus.dao.dig.mapper.StJobMapper;
import com.lacus.service.dig.IStJobService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

@Service
public class StJobServiceImpl extends ServiceImpl<StJobMapper, StJobEntity> implements IStJobService {

    @Override
    public boolean isJobNameDuplicated(Long jobId, String jobName) {
        LambdaQueryWrapper<StJobEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StJobEntity::getJobName, jobName);
        if (ObjectUtils.isNotEmpty(jobId)) {
            queryWrapper.ne(StJobEntity::getJobId, jobId);
        }
        return this.count(queryWrapper) > 0;
    }
}
