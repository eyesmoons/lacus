package com.lacus.domain.rtc.job.model;

import lombok.Data;

@Data
public class FlinkTaskSink {
    private String sinkType;
    private FlinkTaskEngine engine;
}
