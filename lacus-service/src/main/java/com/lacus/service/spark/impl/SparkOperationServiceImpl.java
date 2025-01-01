package com.lacus.service.spark.impl;

import com.lacus.common.exception.CustomException;
import com.lacus.dao.spark.entity.SparkJobEntity;
import com.lacus.enums.SparkStatusEnum;
import com.lacus.service.spark.ISparkJobService;
import com.lacus.service.spark.ISparkOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SparkOperationServiceImpl implements ISparkOperationService {

    @Autowired
    private ISparkJobService sparkJobService;

    @Override
    public void start(Long jobId) {
        SparkJobEntity sparkJobEntity = sparkJobService.getById(jobId);
        if (sparkJobEntity == null) {
            throw new CustomException("任务不存在");
        }

        // 检查任务状态
        if (SparkStatusEnum.RUNNING.equals(sparkJobEntity.getJobStatus())) {
            throw new CustomException("任务运行中,请先停止任务");
        }

        // TODO: 实现具体的Spark任务提交逻辑
        log.info("开始提交Spark任务: {}", jobId);
        
        // 更新任务状态为运行中
        sparkJobService.updateStatus(jobId, SparkStatusEnum.RUNNING);
    }

    @Override
    public void stop(Long jobId) {
        SparkJobEntity sparkJobEntity = sparkJobService.getById(jobId);
        if (sparkJobEntity == null) {
            throw new CustomException("任务不存在");
        }

        // TODO: 实现具体的Spark任务停止逻辑
        log.info("开始停止Spark任务: {}", jobId);

        // 更新任务状态为已停止
        sparkJobService.updateStatus(jobId, SparkStatusEnum.KILLED);
    }
} 