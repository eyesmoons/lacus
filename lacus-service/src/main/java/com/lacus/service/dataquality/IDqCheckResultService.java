package com.lacus.service.dataquality;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.dataquality.entity.DqCheckResultEntity;

import java.util.List;

/**
 * 数据质量检测结果 Service 接口
 */
public interface IDqCheckResultService extends IService<DqCheckResultEntity> {

    /**
     * 根据执行记录ID查询结果列表
     */
    List<DqCheckResultEntity> listByLogId(Long logId);

    /**
     * 根据规则ID删除所有检测结果
     */
    void removeByRuleId(Long ruleId);
}
