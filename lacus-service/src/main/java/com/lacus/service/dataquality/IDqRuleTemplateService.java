package com.lacus.service.dataquality;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.dataquality.entity.DqRuleTemplateEntity;

import java.util.List;

/**
 * 数据质量规则模板 Service 接口
 */
public interface IDqRuleTemplateService extends IService<DqRuleTemplateEntity> {

    /**
     * 查询所有启用的模板（按 sort_order 排序）
     */
    List<DqRuleTemplateEntity> listEnabled();

    /**
     * 根据模板编码查询
     */
    DqRuleTemplateEntity getByCode(String templateCode);
}
