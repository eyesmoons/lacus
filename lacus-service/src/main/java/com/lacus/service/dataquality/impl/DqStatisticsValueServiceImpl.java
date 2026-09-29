package com.lacus.service.dataquality.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.dataquality.entity.DqStatisticsValueEntity;
import com.lacus.dao.dataquality.mapper.DqStatisticsValueMapper;
import com.lacus.service.dataquality.IDqStatisticsValueService;
import org.springframework.stereotype.Service;

@Service
public class DqStatisticsValueServiceImpl extends ServiceImpl<DqStatisticsValueMapper, DqStatisticsValueEntity>
        implements IDqStatisticsValueService {

    @Override
    public void removeByRuleId(Long ruleId) {
        LambdaQueryWrapper<DqStatisticsValueEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DqStatisticsValueEntity::getRuleId, ruleId);
        this.remove(wrapper);
    }
}
