package com.lacus.domain.dataquality;

import com.lacus.common.exception.CustomException;
import com.lacus.dao.dataquality.entity.DqRuleTemplateEntity;
import com.lacus.service.dataquality.IDqRuleTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据质量规则模板业务逻辑
 */
@Slf4j
@Service
public class DqRuleTemplateBusiness {

    @Autowired
    private IDqRuleTemplateService dqRuleTemplateService;

    /**
     * 查询所有启用的模板（供前端选择器使用）
     */
    public List<DqRuleTemplateEntity> listEnabled() {
        return dqRuleTemplateService.listEnabled();
    }

    /**
     * 查询全部模板（管理用）
     */
    public List<DqRuleTemplateEntity> listAll() {
        return dqRuleTemplateService.list();
    }

    /**
     * 新增模板
     */
    public DqRuleTemplateEntity addTemplate(DqRuleTemplateEntity entity) {
        if (ObjectUtils.isEmpty(entity.getTemplateCode())) {
            throw new CustomException("模板编码不能为空");
        }
        DqRuleTemplateEntity existing = dqRuleTemplateService.getByCode(entity.getTemplateCode());
        if (existing != null) {
            throw new CustomException("模板编码[" + entity.getTemplateCode() + "]已存在");
        }
        if (entity.getSortOrder() == null) {
            entity.setSortOrder(0);
        }
        if (entity.getEnabled() == null) {
            entity.setEnabled(1);
        }
        entity.insert();
        return entity;
    }

    /**
     * 更新模板
     */
    public void updateTemplate(DqRuleTemplateEntity entity) {
        DqRuleTemplateEntity existing = dqRuleTemplateService.getById(entity.getId());
        if (ObjectUtils.isEmpty(existing)) {
            throw new CustomException("模板[" + entity.getId() + "]不存在");
        }
        dqRuleTemplateService.updateById(entity);
    }

    /**
     * 删除模板
     */
    public void deleteTemplate(Long id) {
        DqRuleTemplateEntity existing = dqRuleTemplateService.getById(id);
        if (ObjectUtils.isEmpty(existing)) {
            throw new CustomException("模板[" + id + "]不存在");
        }
        dqRuleTemplateService.removeById(id);
    }

    /**
     * 根据编码查询单个模板（供 DqRuleBusiness 使用）
     */
    public DqRuleTemplateEntity getByCode(String templateCode) {
        return dqRuleTemplateService.getByCode(templateCode);
    }
}
