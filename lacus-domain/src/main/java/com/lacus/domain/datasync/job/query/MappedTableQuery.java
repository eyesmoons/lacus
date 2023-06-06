package com.lacus.domain.datasync.job.query;

import lombok.Data;

import java.util.List;

@Data
public class MappedTableQuery {
    private Long jobId;
    private Long sourceDatasourceId;
    private String sourceDbName;
    private List<String> sourceTableNames;
    private Long sinkDatasourceId;
    private String sinkDbName;
}
