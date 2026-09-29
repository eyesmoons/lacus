package com.lacus.service.dataquality.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.dataquality.entity.DqCheckResultEntity;
import com.lacus.dao.dataquality.mapper.DqCheckResultMapper;
import com.lacus.service.dataquality.IDqCheckResultService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DqCheckResultServiceImpl extends ServiceImpl<DqCheckResultMapper, DqCheckResultEntity>
        implements IDqCheckResultService {

    @Override
    public List<DqCheckResultEntity> listByLogId(Long logId) {
        LambdaQueryWrapper<DqCheckResultEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DqCheckResultEntity::getLogId, logId)
               .orderByDesc(DqCheckResultEntity::getId);
        return this.list(wrapper);
    }

    @Override
    public void removeByRuleId(Long ruleId) {
        LambdaQueryWrapper<DqCheckResultEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DqCheckResultEntity::getRuleId, ruleId);
        this.remove(wrapper);
    }
}
