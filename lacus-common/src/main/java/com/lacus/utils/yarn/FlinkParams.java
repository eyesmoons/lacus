package com.lacus.utils.yarn;

import lombok.Data;

@Data
public class FlinkParams {

    private String jobName = "";

    // jobManager 内存
    private Integer masterMemoryMB = 2048;

    // taskManager 内存
    private Integer taskManagerMemoryMB = 2048;

    // slots数量
    private Integer slotsPerTaskManager = 1;

    // 并行度
    private Integer parallelism = 1;
}
