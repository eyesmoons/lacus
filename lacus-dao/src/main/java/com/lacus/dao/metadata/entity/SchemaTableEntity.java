package com.lacus.dao.metadata.entity;

import lombok.Data;

import java.util.Date;

@Data
public class SchemaTableEntity {
    private String tableSchema;
    private String tableName;
    private String tableType;
    private String engine;
    private Long tableRows;
    private Long dataLength;
    private Date createTime;
    private String tableComment;
}
