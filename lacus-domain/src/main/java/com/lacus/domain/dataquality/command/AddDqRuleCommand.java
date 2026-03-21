package com.lacus.domain.dataquality.command;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 新增数据质量规则命令
 */
@Data
public class AddDqRuleCommand {

    @ApiModelProperty(value = "规则名称", required = true)
    @NotBlank(message = "规则名称不能为空")
    private String ruleName;

    @ApiModelProperty(value = "规则模板ID（关联 dq_rule_template.id）", required = true)
    @NotNull(message = "规则模板不能为空")
    private Long templateId;

    @ApiModelProperty("规则描述")
    private String description;

    @ApiModelProperty("是否启用：1启用 0禁用")
    private Integer enabled = 1;

    @ApiModelProperty(value = "校验规则参数（步骤四：checkMethod/operator/expectedType等）", required = true)
    @NotNull(message = "校验规则参数不能为空")
    @Valid
    private RuleCheckParams ruleCheckParams;

    @ApiModelProperty("数据源ID")
    private Long datasourceId;

    @ApiModelProperty("数据库名称")
    private String dbName;

    @ApiModelProperty("数据表名称")
    private String tableName;

    @ApiModelProperty("检测字段名称（单个字段）")
    private String fieldNames;

    @ApiModelProperty("Spark 任务参数 JSON（包含 deployMode/driverCores/driverMemory/numExecutors/executorMemory/executorCores/queue/otherParams）")
    private String sparkParams;
}
