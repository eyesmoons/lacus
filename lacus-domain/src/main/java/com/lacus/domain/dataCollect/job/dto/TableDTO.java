package com.lacus.domain.dataCollect.job.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TableDTO {
    @ApiModelProperty(value = "数据源Id")
    private Long datasourceId;
    @ApiModelProperty(value = "数据库名称")
    private String dbName;
    @ApiModelProperty(value = "表名称")
    private String tableName;
    @ApiModelProperty(value = "表ID")
    private Long metaTableId;
}
