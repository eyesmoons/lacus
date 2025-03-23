package com.lacus.service.spark.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.common.exception.CustomException;
import com.lacus.dao.spark.entity.SparkJobInstanceEntity;
import com.lacus.dao.spark.mapper.SparkJobInstanceMapper;
import com.lacus.enums.SparkStatusEnum;
import com.lacus.service.spark.ISparkJobInstanceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service(value = "sparkJobInstanceService")
public class SparkJobInstanceServiceImpl extends ServiceImpl<SparkJobInstanceMapper, SparkJobInstanceEntity> implements ISparkJobInstanceService {

    @Override
    public void updateStatus(Long instanceId, SparkStatusEnum sparkStatusEnum) {
        SparkJobInstanceEntity instance = baseMapper.selectById(instanceId);
        if (instance == null) {
            throw new CustomException("任务实例不存在");
        }

        // 更新状态
        instance.setJobStatus(sparkStatusEnum);

        // 如果是终态,设置完成时间
        if (isTerminalStatus(sparkStatusEnum)) {
            instance.setFinishedTime(new Date());
        }

        baseMapper.updateById(instance);
        log.info("更新任务实例状态成功, instanceId: {}, status: {}", instanceId, sparkStatusEnum);
    }

    @Override
    public void updateStatus(Long instanceId, String appId, SparkStatusEnum sparkStatusEnum) {
        SparkJobInstanceEntity instance = baseMapper.selectById(instanceId);
        if (instance == null) {
            throw new CustomException("任务实例不存在");
        }

        // 更新状态
        instance.setJobStatus(sparkStatusEnum);

        // 如果是终态,设置完成时间
        if (isTerminalStatus(sparkStatusEnum)) {
            instance.setFinishedTime(new Date());
        }

        if (ObjectUtils.isNotEmpty(appId)) {
            instance.setApplicationId(appId);
        }
        baseMapper.updateById(instance);
        log.info("更新任务实例状态成功, instanceId: {}, status: {}", instanceId, sparkStatusEnum);
    }

    @Override
    public void updateStatusByJobId(Long jobId, SparkStatusEnum sparkStatusEnum, Date finishedTime) {
        LambdaQueryWrapper<SparkJobInstanceEntity> wrapper =
                new LambdaQueryWrapper<SparkJobInstanceEntity>()
                        .eq(SparkJobInstanceEntity::getJobId, jobId)
                        .eq(SparkJobInstanceEntity::getJobStatus, SparkStatusEnum.RUNNING);

        List<SparkJobInstanceEntity> runningInstances = baseMapper.selectList(wrapper);
        if (ObjectUtils.isNotEmpty(runningInstances)) {
            for (SparkJobInstanceEntity instance : runningInstances) {
                instance.setJobStatus(sparkStatusEnum);
                if (finishedTime != null) {
                    instance.setFinishedTime(finishedTime);
                } else if (isTerminalStatus(sparkStatusEnum)) {
                    instance.setFinishedTime(new Date());
                }
                baseMapper.updateById(instance);
            }
            log.info("批量更新任务实例状态成功, jobId: {}, status: {}, count: {}",
                    jobId, sparkStatusEnum, runningInstances.size());
        }
    }

    /**
     * 判断是否是终态
     */
    private boolean isTerminalStatus(SparkStatusEnum status) {
        return SparkStatusEnum.FINISHED.equals(status)
                || SparkStatusEnum.FAILED.equals(status)
                || SparkStatusEnum.KILLED.equals(status);
    }
}
