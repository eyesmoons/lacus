package com.lacus.domain.common.dto;

import com.lacus.utils.yarn.FlinkConf;
import lombok.Data;

/**
 * @created by shengyu on 2023/9/6 10:01
 */
@Data
public class JobConf {
    private JobInfo jobInfo;
    private FlinkConf flinkConf;
    private Sink sink;
    private Source source;
}