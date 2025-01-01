package com.lacus.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class FlinkConf implements Serializable {
    private static final long serialVersionUID = 4472669927458484795L;
    private Integer maxBatchInterval;
    private Integer maxBatchSize;
    private Integer maxBatchRows;
}
