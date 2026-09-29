package com.lacus.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @created by shengyu on 2023/9/6 10:01
 */
@Data
public class JobConf implements Serializable {
    private static final long serialVersionUID = 1560138527352748105L;
    private JobInfo jobInfo;
    private FlinkConf flinkConf;
    private SinkConfig sink;
    private SourceConfig source;
}
