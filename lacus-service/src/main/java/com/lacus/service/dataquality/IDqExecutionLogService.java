package com.lacus.service.dataquality;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.dataquality.entity.DqExecutionLogEntity;

/**
 * 数据质量执行记录 Service 接口
 */
public interface IDqExecutionLogService extends IService<DqExecutionLogEntity> {

    /**
     * 根据规则ID删除所有执行记录
     */
    void removeByRuleId(Long ruleId);
}
