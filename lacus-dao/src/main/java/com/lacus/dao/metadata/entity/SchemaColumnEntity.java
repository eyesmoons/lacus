package com.lacus.dao.metadata.entity;

import lombok.Data;

@Data
public class SchemaColumnEntity {
    private String tableSchema;
    private String tableName;
    private String columnName;
    private String columnDefault;
    private String isNullable;
    private String dataType;
    private String columnType;
    private Integer characterOctetLength;
    private Long numericPrecision;
    private Long numericScale;
    private String columnComment;
}
