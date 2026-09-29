package com.lacus.service.dataquality.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.dataquality.entity.DqRuleEntity;
import com.lacus.dao.dataquality.mapper.DqRuleMapper;
import com.lacus.service.dataquality.IDqRuleService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

@Service
public class DqRuleServiceImpl extends ServiceImpl<DqRuleMapper, DqRuleEntity> implements IDqRuleService {

    @Override
    public boolean isRuleNameDuplicated(Long id, String ruleName) {
        LambdaQueryWrapper<DqRuleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DqRuleEntity::getRuleName, ruleName);
        if (ObjectUtils.isNotEmpty(id)) {
            wrapper.ne(DqRuleEntity::getId, id);
        }
        return this.count(wrapper) > 0;
    }
}
