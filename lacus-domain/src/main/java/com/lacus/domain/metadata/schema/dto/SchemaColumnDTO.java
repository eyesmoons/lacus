package com.lacus.domain.metadata.schema.dto;

import com.lacus.dao.metadata.entity.SchemaColumnEntity;
import lombok.Data;

@Data
public class SchemaColumnDTO {
    private String tableSchema;
    private String tableName;
    private String columnName;
    private String columnDefault;
    private String isNullable;
    private String dataType;
    private String columnType;
    private Long characterOctetLength;
    private Long numericPrecision;
    private Long numericScale;
    private String columnComment;

    public SchemaColumnDTO(SchemaColumnEntity entity) {
        this.tableSchema = entity.getTableSchema();
        this.tableName = entity.getTableName();
        this.columnName = entity.getColumnName();
        this.columnDefault = entity.getColumnDefault();
        this.isNullable = entity.getIsNullable();
        this.dataType = entity.getDataType();
        this.columnType = entity.getColumnType();
        this.characterOctetLength = entity.getCharacterOctetLength();
        this.numericPrecision = entity.getNumericPrecision();
        this.numericScale = entity.getNumericScale();
        this.columnComment = entity.getColumnComment();
    }
}