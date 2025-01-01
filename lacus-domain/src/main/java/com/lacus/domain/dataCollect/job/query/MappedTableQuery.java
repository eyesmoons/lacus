package com.lacus.domain.dataCollect.job.query;

import lombok.Data;

import java.util.List;

@Data
public class MappedTableQuery {
    private Long jobId;
    private Long sourceDatasourceId;
    private String sourceDbName;
    private String sourceTableName;
    private List<String> sourceTableNames;
    private Long sinkDatasourceId;
    private String sinkDbName;
    private String sinkTableName;
    private List<String> sinkTableNames;
}
