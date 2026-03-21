package com.lacus.service.dataquality.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.dataquality.entity.DqExecutionLogEntity;
import com.lacus.dao.dataquality.mapper.DqExecutionLogMapper;
import com.lacus.service.dataquality.IDqExecutionLogService;
import org.springframework.stereotype.Service;

@Service
public class DqExecutionLogServiceImpl extends ServiceImpl<DqExecutionLogMapper, DqExecutionLogEntity>
        implements IDqExecutionLogService {

    @Override
    public void removeByRuleId(Long ruleId) {
        LambdaQueryWrapper<DqExecutionLogEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DqExecutionLogEntity::getRuleId, ruleId);
        this.remove(wrapper);
    }
}
