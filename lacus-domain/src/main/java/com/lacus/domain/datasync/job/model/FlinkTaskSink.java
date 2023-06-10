package com.lacus.domain.datasync.job.model;

import lombok.Data;

@Data
public class FlinkTaskSink {
    private String sinkType;
    private FlinkTaskEngine engine;
}
