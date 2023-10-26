package com.lacus.job.model;

import lombok.Data;

@Data
public class DataSyncJobConf {
    private FlinkConf flinkConf;
    private FlinkTaskSink sink;
    private FlinkJobSource source;
}
