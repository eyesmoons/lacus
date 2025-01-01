package com.lacus.domain.dataCollect.job.query;

import com.lacus.domain.dataCollect.job.dto.TableMapping;
import lombok.Data;

import java.util.List;

@Data
public class MappedTableColumnQuery {
    private Long jobId;
    private Long sourceDatasourceId;
    private String sourceDbName;
    private Long sinkDatasourceId;
    private String sinkDbName;
    private List<TableMapping> tableMappings;
}
