package com.lacus.domain.datasync.job.model;

import com.lacus.common.utils.yarn.FlinkConf;
import lombok.Data;

@Data
public class DataSyncJobConf {
    private FlinkConf flinkConf;
    private FlinkTaskSink sink;
    private FlinkJobSource source;
}
