package com.lacus.dao.dataCollect.entity;

import lombok.Data;

@Data
public class DataSyncSavedColumn {
    private Long jobId;
    private Long sourceDatasourceId;
    private String sourceDbName;
    private String sourceTableName;
    private String sourceColumnName;
    private Long sinkDatasourceId;
    private String sinkDbName;
    private String sinkTableName;
    private String sinkColumnName;
    private Integer flag;
}
