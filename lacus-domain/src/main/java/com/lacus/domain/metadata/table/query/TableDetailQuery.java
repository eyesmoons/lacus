package com.lacus.domain.metadata.table.query;

import lombok.Data;

@Data
public class TableDetailQuery {
    private Long datasourceId;
    private String dbName;
    private String tableName;
}
