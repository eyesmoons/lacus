package com.lacus.service.spark.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.common.exception.CustomException;
import com.lacus.dao.spark.entity.SparkJobEntity;
import com.lacus.dao.spark.mapper.SparkJobMapper;
import com.lacus.enums.SparkStatusEnum;
import com.lacus.service.spark.ISparkJobService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

/**
 * @author shengyu
 * @date 2024/12/31 17:52
 */
@Slf4j
@Service(value = "sparkJobService")
public class SparkJobServiceImpl extends ServiceImpl<SparkJobMapper, SparkJobEntity> implements ISparkJobService {

    @Override
    public boolean isJobNameDuplicated(Long jobId, String jobName) {
        LambdaQueryWrapper<SparkJobEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(jobId != null, SparkJobEntity::getJobId, jobId);
        queryWrapper.eq(SparkJobEntity::getJobName, jobName);
        return this.baseMapper.exists(queryWrapper);
    }

    @Override
    public void updateStatus(Long jobId, SparkStatusEnum sparkStatusEnum) {
        SparkJobEntity sparkJobEntity = baseMapper.selectById(jobId);
        if (sparkJobEntity == null) {
            throw new CustomException("任务不存在");
        }
        sparkJobEntity.setJobStatus(sparkStatusEnum);
        baseMapper.updateById(sparkJobEntity);
    }

    @Override
    public void updateStatus(Long jobId, String appId, SparkStatusEnum sparkStatusEnum) {
        SparkJobEntity sparkJobEntity = baseMapper.selectById(jobId);
        if (sparkJobEntity == null) {
            throw new CustomException("任务不存在");
        }
        sparkJobEntity.setJobStatus(sparkStatusEnum);
        if (ObjectUtils.isNotEmpty(appId)) {
            sparkJobEntity.setApplicationId(appId);
        }
        baseMapper.updateById(sparkJobEntity);
    }
}
