package com.lacus.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @created by shengyu on 2023/9/7 10:16
 */
@Data
public class JobInfo implements Serializable {
    private static final long serialVersionUID = -8504815838385198227L;
    private Long jobId;
    private Long instanceId;
    private String jobName;
}