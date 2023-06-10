package com.lacus.domain.datasync.job.query;

import lombok.Data;

import java.util.List;

@Data
public class MappedColumnQuery {
    private String jobId;
    private Long sourceDatasourceId;
    private String sourceDbName;
    private String sourceTableName;
    private Long sinkDatasourceId;
    private String sinkDbName;
    private String sinkTableName;
}
