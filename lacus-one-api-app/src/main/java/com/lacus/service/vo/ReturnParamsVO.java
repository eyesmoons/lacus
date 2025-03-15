package com.lacus.service.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ReturnParamsVO {

    @ApiModelProperty("字段名")
    private String columnName;

    @ApiModelProperty("字段类型")
    private String columnType;

    @ApiModelProperty("字段描述")
    private String columnDesc;
}
