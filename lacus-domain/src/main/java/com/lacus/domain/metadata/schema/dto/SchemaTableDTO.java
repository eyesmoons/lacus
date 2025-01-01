package com.lacus.domain.metadata.schema.dto;

import com.lacus.dao.metadata.entity.SchemaTableEntity;
import lombok.Data;
import java.util.Date;

@Data
public class SchemaTableDTO {
    private String dbName;
    private String tableName;
    private String tableType;
    private String engine;
    private Long tableRows;
    private Long dataLength;
    private String tableComment;
    private Date createTime;
    private String label;
    private String uniqueFlag;
    private Boolean isLeaf = true;

    public SchemaTableDTO(SchemaTableEntity entity) {
        dbName = entity.getTableSchema();
        tableName = entity.getTableName();
        tableType = entity.getTableType();
        engine = entity.getEngine();
        tableRows = entity.getTableRows();
        dataLength = entity.getDataLength();
        tableComment = entity.getTableComment();
        createTime = entity.getCreateTime();
        label = entity.getTableName();
        uniqueFlag = entity.getTableSchema() + "." + entity.getTableName();
    }
}
