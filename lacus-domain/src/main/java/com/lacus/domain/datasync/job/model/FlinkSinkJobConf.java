package com.lacus.domain.datasync.job.model;

import com.lacus.common.utils.yarn.FlinkParams;
import lombok.Data;

@Data
public class FlinkSinkJobConf {
    private FlinkParams flinkConf;
    private FlinkTaskSink sink;
    private FlinkJobSource source;
}
