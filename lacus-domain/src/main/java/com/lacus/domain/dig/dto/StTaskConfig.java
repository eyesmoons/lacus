package com.lacus.domain.dig.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.seatunnel.common.constants.PluginType;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StTaskConfig {

    private Long taskId;

    private String taskName;

    private Long jobId;

    private PluginType connectorType;

    private String connectorName;

    private Long datasourceId;

    private String taskConfig;

    private DatasourceConfig datasourceConfig;

    private SourceFieldsConfig sourceFieldsConfig;

    private Map<String, Object> transformConfig;

    private List<DatabaseTableSchema> sinkFieldsConfig;

}
