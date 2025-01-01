package com.lacus.dao.dataCollect.entity;

import lombok.Data;

@Data
public class DataSyncSavedTable {
    private Long jobId;
    private Long sourceDatasourceId;
    private String sourceDbName;
    private String sourceTableName;
    private Long sinkDatasourceId;
    private String sinkDbName;
    private String sinkTableName;
}
