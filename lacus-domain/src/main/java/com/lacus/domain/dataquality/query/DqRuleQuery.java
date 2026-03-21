package com.lacus.domain.dataquality.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lacus.dao.system.query.AbstractPageQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据质量规则分页查询条件（使用关联查询，不再依赖 QueryWrapper）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DqRuleQuery extends AbstractPageQuery {

    @ApiModelProperty("规则名称（模糊查询）")
    private String ruleName;

    @ApiModelProperty("规则模板ID")
    private Long templateId;

    @ApiModelProperty("是否启用：1启用 0禁用")
    private Integer enabled;

    @Override
    public QueryWrapper toQueryWrapper() {
        // 规则列表使用 DqRuleMapper.selectPageWithJoin 关联查询，此处不使用 QueryWrapper
        return new QueryWrapper<>();
    }
}

