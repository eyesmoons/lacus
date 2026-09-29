package com.lacus.domain.rtc.job.query;

import lombok.Data;

@Data
public class MappedColumnQuery {
    private Long jobId;
    private Long sourceDatasourceId;
    private String sourceDbName;
    private String sourceTableName;
    private Long sinkDatasourceId;
    private String sinkDbName;
    private String sinkTableName;
}
