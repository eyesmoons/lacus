package com.lacus.domain.scheduler.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ProcessTaskInstance implements Serializable {
    private static final long serialVersionUID = 8699059547273622666L;

    // 任务id
    private String id;

    // 任务名称
    private String name;

    // 任务类型
    private String taskType;

    // 流程定义id
    private Integer processDefinitionId;

    // 流程实例id
    private Integer processInstanceId;

    // 任务json
    private String taskJson;

    // 任务状态
    private String state;

    // 任务提交时间
    private Date submitTime;

    // 任务开始时间
    private Date startTime;

    // 任务结束时间
    private Date endTime;

    // 任务重试次数
    private Integer retryTimes;

    // 是否告警
    private String alertFlag;

    // 父id
    private String pid;

    // 是否容错
    private String flag;

    // 依赖
    private String dependency;

    // 运行时长
    private String duration;

    // 最大重试次数
    private Integer maxRetryTimes;

    // 重试间隔
    private Integer retryInterval;

    // 任务优先级
    private String taskInstancePriority;

    // worker组
    private String workerGroup;

    // 资源
    private String resources;

    // 任务是否成功
    private Boolean taskSuccess;

    // 任务是否完成
    private Boolean taskComplete;

    // 是否未depend任务
    private Boolean dependTask;

    // 是否为condition任务
    private Boolean conditionsTask;

    // 是否为子流程
    private Boolean subProcess;
}
