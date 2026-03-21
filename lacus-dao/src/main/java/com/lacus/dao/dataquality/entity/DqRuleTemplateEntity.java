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
 * 数据质量规则模板实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dq_rule_template")
public class DqRuleTemplateEntity extends BaseEntity<DqRuleTemplateEntity> {

    @ApiModelProperty("模板ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("模板编码，如 NULL_CHECK")
    @TableField("template_code")
    private String templateCode;

    @ApiModelProperty("模板名称，如 字段空值校验")
    @TableField("template_name")
    private String templateName;

    @ApiModelProperty("质量维度：completeness/uniqueness/timeliness/validity/consistency/stability")
    @TableField("dimension")
    private String dimension;

    @ApiModelProperty("前端图标名称")
    @TableField("template_icon")
    private String templateIcon;

    @ApiModelProperty("前端图标颜色")
    @TableField("template_color")
    private String templateColor;

    @ApiModelProperty("模板描述")
    @TableField("description")
    private String description;

    @ApiModelProperty("检测SQL模板，支持 {templateCode} 占位符（聚合统计 SQL，引用 {templateCode}_items 结果集）")
    @TableField("check_sql_pattern")
    private String checkSqlPattern;

    @ApiModelProperty("明细行过滤SQL模板，输出问题数据行（SELECT * WHERE 条件），支持 {outputTable}/{field}/{minValue}/{maxValue}/{enumValues}/{regexPattern} 等占位符")
    @TableField("items_sql_pattern")
    private String itemsSqlPattern;

    @ApiModelProperty("模板专属额外配置字段的JSON Schema")
    @TableField("extra_config_schema")
    private String extraConfigSchema;

    @ApiModelProperty("排序序号")
    @TableField("sort_order")
    private Integer sortOrder;

    @ApiModelProperty("是否启用：1启用 0禁用")
    @TableField("enabled")
    private Integer enabled;
}
