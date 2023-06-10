package com.lacus.dao.datasync.entity;

import lombok.Data;

@Data
public class DataSyncSavedColumn {
    private String jobId;
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
