package com.lacus.common.utils.yarn;

import lombok.Data;

@Data
public class FlinkConf {

    private String jobName;

    private Integer maxBatchInterval;

    private Long maxBatchSize;

    private Integer maxBatchRows;
}
