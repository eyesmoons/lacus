package com.lacus.service.dataquality.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.dataquality.entity.DqRuleTemplateEntity;
import com.lacus.dao.dataquality.mapper.DqRuleTemplateMapper;
import com.lacus.service.dataquality.IDqRuleTemplateService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DqRuleTemplateServiceImpl extends ServiceImpl<DqRuleTemplateMapper, DqRuleTemplateEntity>
        implements IDqRuleTemplateService {

    @Override
    public List<DqRuleTemplateEntity> listEnabled() {
        LambdaQueryWrapper<DqRuleTemplateEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DqRuleTemplateEntity::getEnabled, 1)
               .eq(DqRuleTemplateEntity::getDeleted, 0)
               .orderByAsc(DqRuleTemplateEntity::getSortOrder);
        return this.list(wrapper);
    }

    @Override
    public DqRuleTemplateEntity getByCode(String templateCode) {
        LambdaQueryWrapper<DqRuleTemplateEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DqRuleTemplateEntity::getTemplateCode, templateCode)
               .eq(DqRuleTemplateEntity::getDeleted, 0);
        return this.getOne(wrapper);
    }
}
