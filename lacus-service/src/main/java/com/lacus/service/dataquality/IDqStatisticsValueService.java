package com.lacus.service.dataquality;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.dataquality.entity.DqStatisticsValueEntity;

/**
 * 数据质量统计值 Service 接口
 */
public interface IDqStatisticsValueService extends IService<DqStatisticsValueEntity> {

    /**
     * 根据规则ID删除所有统计值快照
     */
    void removeByRuleId(Long ruleId);
}
