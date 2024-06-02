package com.lacus.domain.datasync.job.model;

import com.lacus.utils.yarn.FlinkConf;
import lombok.Data;

@Data
public class DataSyncJobConf {
    private FlinkConf flinkConf;
    private FlinkTaskSink sink;
    private FlinkJobSource source;
}
