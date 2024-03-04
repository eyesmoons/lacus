package com.lacus.domain.metadata.datasourceType.command;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AddMetaDatasourceTypeCommand {
    @NotBlank(message = "名称不能为空")
    private String typeName;

    @NotBlank(message = "编码不能为空")
    private String typeCode;
    @NotBlank(message = "分类不能为空")
    private String typeCatalog;
    @NotBlank(message = "驱动名称不能为空")
    private String driverName;
    private String icon;
    private String jdbcUrl;
    private String remark;
}
