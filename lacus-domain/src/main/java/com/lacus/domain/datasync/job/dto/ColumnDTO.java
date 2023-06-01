package com.lacus.domain.datasync.job.dto;

import lombok.Data;

import java.io.Serializable;

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
