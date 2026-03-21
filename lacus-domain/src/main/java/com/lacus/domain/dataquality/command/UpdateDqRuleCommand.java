package com.lacus.domain.dataquality.command;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 修改数据质量规则命令（patch 语义：只更新非 null 字段）
 */
@Data
public class UpdateDqRuleCommand {

    @ApiModelProperty(value = "规则ID", required = true)
    @NotNull(message = "规则ID不能为空")
    private Long id;

    @ApiModelProperty("规则名称（可选，传则更新）")
    private String ruleName;

    @ApiModelProperty("规则模板ID（可选，传则更新）")
    private Long templateId;

    @ApiModelProperty("校验规则参数（可选，不传则不更新）")
    @Valid
    private RuleCheckParams ruleCheckParams;

    @ApiModelProperty("数据源ID（可选）")
    private Long datasourceId;

    @ApiModelProperty("数据库名称（可选）")
    private String dbName;

    @ApiModelProperty("数据表名称（可选）")
    private String tableName;

    @ApiModelProperty("检测字段名称（单个字段，可选）")
    private String fieldNames;

    @ApiModelProperty("规则描述（可选）")
    private String description;

    @ApiModelProperty("是否启用：1启用 0禁用（可选）")
    private Integer enabled;

    @ApiModelProperty("Spark 任务参数 JSON（可选，传则更新）")
    private String sparkParams;
}

