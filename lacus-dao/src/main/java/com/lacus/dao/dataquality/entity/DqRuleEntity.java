package com.lacus.dao.dataquality.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据质量规则定义实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dq_rule")
public class DqRuleEntity extends BaseEntity<DqRuleEntity> {

    @ApiModelProperty("规则ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("规则名称")
    @TableField("rule_name")
    private String ruleName;

    @ApiModelProperty("规则模板ID（关联 dq_rule_template.id）")
    @TableField("template_id")
    private Long templateId;

    @ApiModelProperty("校验规则配置JSON（仅含步骤四：checkMethod/operator/expectedType等）")
    @TableField("rule_config")
    private String ruleConfig;

    @ApiModelProperty("数据源ID")
    @TableField("datasource_id")
    private Long datasourceId;

    @ApiModelProperty("数据库名称")
    @TableField("db_name")
    private String dbName;

    @ApiModelProperty("数据表名称")
    @TableField("table_name")
    private String tableName;

    @ApiModelProperty("检测字段列表（逗号分隔）")
    @TableField("field_names")
    private String fieldNames;

    @ApiModelProperty("规则描述")
    @TableField("description")
    private String description;

    @ApiModelProperty("是否启用：1启用 0禁用")
    @TableField("enabled")
    private Integer enabled;

    @ApiModelProperty("Spark 任务参数 JSON（deployMode/driverCores/driverMemory/numExecutors/executorMemory/executorCores/queue/otherParams）")
    @TableField("spark_params")
    private String sparkParams;
}
