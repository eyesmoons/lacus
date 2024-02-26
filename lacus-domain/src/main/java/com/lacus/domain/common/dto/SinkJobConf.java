package com.lacus.domain.common.dto;

import com.lacus.common.utils.yarn.FlinkConf;
import lombok.Data;

/**
 * @created by shengyu on 2023/9/6 10:01
 */
@Data
public class SinkJobConf {
    private JobInfo jobInfo;
    private FlinkConf flinkConf;
    private Sink sink;
    private Source source;
}