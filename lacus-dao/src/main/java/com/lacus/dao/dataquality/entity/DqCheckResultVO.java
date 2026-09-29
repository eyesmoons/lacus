package com.lacus.dao.dataquality.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 数据质量检测结果明细展示 VO（在 DqCheckResultEntity 基础上补充中文展示字段）
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DqCheckResultVO {

    @ApiModelProperty("结果ID")
    private Long id;

    @ApiModelProperty("关联执行记录ID")
    private Long logId;

    @ApiModelProperty("规则ID")
    private Long ruleId;

    @ApiModelProperty("规则名称快照")
    private String ruleName;

    @ApiModelProperty("规则模板编码")
    private String templateCode;

    @ApiModelProperty("规则模板名称（关联查询）")
    private String templateName;

    @ApiModelProperty("实际执行的检测SQL")
    private String checkSql;

    @ApiModelProperty("检测到的实际值")
    private BigDecimal actualValue;

    @ApiModelProperty("期望值")
    private BigDecimal expectedValue;

    @ApiModelProperty("期望值类型（原始值）")
    private String expectedType;

    @ApiModelProperty("期望值类型（中文）")
    private String expectedTypeLabel;

    @ApiModelProperty("校验方式（原始值）")
    private String checkMethod;

    @ApiModelProperty("校验方式（中文）")
    private String checkMethodLabel;

    @ApiModelProperty("校验操作符")
    private String operator;

    @ApiModelProperty("公式计算结果")
    private BigDecimal formulaResult;

    @ApiModelProperty("是否通过：1通过 0不通过")
    private Integer passFlag;

    @ApiModelProperty("写入时间")
    private Date createTime;

    /** 从 Entity 构建 VO */
    public static DqCheckResultVO from(DqCheckResultEntity entity) {
        if (entity == null) return null;
        DqCheckResultVO vo = new DqCheckResultVO();
        vo.setId(entity.getId());
        vo.setLogId(entity.getLogId());
        vo.setRuleId(entity.getRuleId());
        vo.setRuleName(entity.getRuleName());
        vo.setTemplateCode(entity.getTemplateCode());
        vo.setCheckSql(entity.getCheckSql());
        vo.setActualValue(entity.getActualValue());
        vo.setExpectedValue(entity.getExpectedValue());
        vo.setExpectedType(entity.getExpectedType());
        vo.setCheckMethod(entity.getCheckMethod());
        vo.setOperator(entity.getOperator());
        vo.setFormulaResult(entity.getFormulaResult());
        vo.setPassFlag(entity.getPassFlag());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
