package com.lacus.domain.datasync.job.query;

import lombok.Data;

import java.util.List;

@Data
public class MappedTableQuery {
    private String jobId;
    private Long sourceDatasourceId;
    private String sourceDbName;
    private List<String> sourceTableNames;
    private Long sinkDatasourceId;
    private String sinkDbName;
    private List<String> sinkTableNames;
}
