package com.lacus.job.model;

import lombok.Data;
import java.io.Serializable;

@Data
public class FlinkConf implements Serializable {
    private static final long serialVersionUID = 2245013876169546131L;
    private String jobName;
    private Integer maxBatchInterval;
    private Long maxBatchSize;
    private Integer maxBatchRows;
}
