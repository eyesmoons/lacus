package com.lacus.domain.datasync.job.query;

import com.lacus.domain.datasync.job.dto.TableMapping;
import lombok.Data;

import java.util.List;

@Data
public class MappedTableColumnQuery {
    private String jobId;
    private Long sourceDatasourceId;
    private String sourceDbName;
    private Long sinkDatasourceId;
    private String sinkDbName;
    private List<TableMapping> tableMappings;
}
