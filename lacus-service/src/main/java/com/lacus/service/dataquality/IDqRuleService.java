package com.lacus.service.dataquality;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.dataquality.entity.DqRuleEntity;

/**
 * 数据质量规则 Service 接口
 */
public interface IDqRuleService extends IService<DqRuleEntity> {

    /**
     * 校验规则名称是否重复
     *
     * @param id       规则ID（更新时传入，新增时传 null）
     * @param ruleName 规则名称
     * @return true=重复
     */
    boolean isRuleNameDuplicated(Long id, String ruleName);
}
