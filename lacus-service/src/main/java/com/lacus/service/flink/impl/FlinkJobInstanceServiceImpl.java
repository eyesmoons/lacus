package com.lacus.service.flink.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.flink.entity.FlinkJobInstanceEntity;
import com.lacus.dao.flink.mapper.FlinkJobInstanceMapper;
import com.lacus.enums.FlinkStatusEnum;
import com.lacus.service.flink.IFlinkJobInstanceService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author shengyu
 * @date 2024/10/26 17:28
 */
@Service
public class FlinkJobInstanceServiceImpl extends ServiceImpl<FlinkJobInstanceMapper, FlinkJobInstanceEntity> implements IFlinkJobInstanceService {
    @Override
    public void updateStatus(Long instanceId, FlinkStatusEnum flinkStatusEnum) {
        FlinkJobInstanceEntity instance = baseMapper.selectById(instanceId);
        instance.setStatus(flinkStatusEnum);
        baseMapper.updateById(instance);
    }

    @Override
    public void updateStatusByJobId(Long jobId, FlinkStatusEnum flinkStatusEnum, Date finishedTime) {
        LambdaQueryWrapper<FlinkJobInstanceEntity> wrapper =
                new LambdaQueryWrapper<FlinkJobInstanceEntity>().eq(FlinkJobInstanceEntity::getJobId, jobId);
        List<FlinkJobInstanceEntity> list = baseMapper.selectList(wrapper);
        if (ObjectUtils.isNotEmpty(list)) {
            for (FlinkJobInstanceEntity instance : list) {
                if (ObjectUtils.isNotEmpty(finishedTime)) {
                    instance.setFinishedTime(finishedTime);
                }
                instance.setStatus(flinkStatusEnum);
                baseMapper.updateById(instance);
            }
        }
    }
}
