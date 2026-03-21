package com.lacus.dao.dataquality.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据质量规则视图对象（关联 template/datasource 后的展示模型）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DqRuleVO extends DqRuleEntity {

    @ApiModelProperty("模板名称（关联 dq_rule_template.template_name）")
    private String templateName;

    @ApiModelProperty("模板编码（关联 dq_rule_template.template_code）")
    private String templateCode;

    @ApiModelProperty("模板图标")
    private String templateIcon;

    @ApiModelProperty("模板颜色")
    private String templateColor;

    @ApiModelProperty("数据源名称（关联 meta_datasource.datasource_name）")
    private String datasourceName;

    public DqRuleVO() {}

    public DqRuleVO(DqRuleEntity entity) {
        if (entity == null) return;
        setId(entity.getId());
        setRuleName(entity.getRuleName());
        setTemplateId(entity.getTemplateId());
        setRuleConfig(entity.getRuleConfig());
        setDatasourceId(entity.getDatasourceId());
        setDbName(entity.getDbName());
        setTableName(entity.getTableName());
        setFieldNames(entity.getFieldNames());
        setDescription(entity.getDescription());
        setEnabled(entity.getEnabled());
        setCreateTime(entity.getCreateTime());
        setUpdateTime(entity.getUpdateTime());
    }
}
