package com.lacus.domain.scheduler.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ProcessInstanceDetail implements Serializable {
    private static final long serialVersionUID = -6188254299943270550L;

    private String cmdTypeIfComplement;

    private String commandParam;

    private Date commandStartTime;

    private String commandType;

    private Boolean complementData;

    private String connects;

    private String dependenceScheduleTimes;

    private String duration;

    private Date endTime;

    private Integer executorId;

    private String executorName;

    private String failureStrategy;

    private String globalParams;

    private String historyCmd;

    private String host;

    private Integer id;

    private String isSubProcess;

    private String locations;

    private Integer maxTryTimes;

    private String name;

    private String processDefinition;

    private Integer processDefinitionId;

    private String processInstanceJson;

    private String processInstancePriority;

    private Boolean processInstanceStop;

    private String queue;

    private String receivers;

    private String receiversCc;

    private String recovery;

    private Integer runTimes;

    private Date scheduleTime;

    private Date startTime;

    private String state;

    private String taskDependType;

    private String tenantCode;

    private Integer tenantId;

    private String timeout;

    private Integer warningGroupId;

    private String warningType;

    private String workerGroup;
}
