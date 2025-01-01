package com.lacus.domain.common.dto;

import lombok.Data;

/**
 * @created by shengyu on 2023/9/7 10:16
 */
@Data
public class JobInfo {
    private Long jobId;
    private Long instanceId;
    private String jobName;
}