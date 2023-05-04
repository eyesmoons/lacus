package com.lacus.domain.metadata.table.dto;

import com.lacus.dao.metadata.entity.MetaTableEntity;
import lombok.Data;

import java.util.Date;

@Data
public class TableDTO {
    public TableDTO(MetaTableEntity entity) {
        if (entity != null) {
            tableId = entity.getTableId();
            tableName = entity.getTableName();
            comment = entity.getComment();
            type = entity.getType();
            engine = entity.getEngine();
            tableCreateTime = entity.getTableCreateTime();
            createTime = entity.getCreateTime();
            dbName = entity.getDbName();
        }
    }

    private Long datasourceId;
    private String datasourceName;
    private Long tableId;
    private String dbName;
    private String tableName;
    private String comment;
    private String type;
    private String engine;
    private Date tableCreateTime;
    private Date createTime;
}
