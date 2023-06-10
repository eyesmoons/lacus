package com.lacus.dao.datasync.entity;

import lombok.Data;

@Data
public class DataSyncSavedTable {
    private String jobId;
    private Long sourceDatasourceId;
    private String sourceDbName;
    private String sourceTableName;
    private Long sinkDatasourceId;
    private String sinkDbName;
    private String sinkTableName;
}
