package com.lacus.domain.dataCollect.job.dto;

import lombok.Data;

@Data
public class ColumnDTO {
    private Long datasourceId;
    private String dbName;
    private String tableName;
    private String columnName;
    private String comment;
    private String dataType;
    private Long columnLength;
}
