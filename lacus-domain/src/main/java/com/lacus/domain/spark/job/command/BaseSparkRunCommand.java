package com.lacus.domain.spark.job.command;

import lombok.Data;

/**
 * Spark任务通用运行配置
 */
@Data
public class BaseSparkRunCommand {
    private Integer driverCores;
    private Integer driverMemory;
    private Integer numExecutors;
    private Integer executorCores;
    private Integer executorMemory;
    private String queue;
    private String namespace;
}
