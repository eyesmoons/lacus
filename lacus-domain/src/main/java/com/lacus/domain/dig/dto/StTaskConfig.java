package com.lacus.domain.dig.dto;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StTaskConfig {

    private String taskId;

    private String taskName;

    private Long jobId;

    private String connectorType;

    private String connectorName;

    private String connectorConfig;

    private JSONObject position;

}
