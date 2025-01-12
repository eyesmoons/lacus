package com.lacus.service.flink.dto;

import lombok.Data;

@Data
public class YarnApplicationDTO {

    private String id;

    private String user;

    private String name;

    private String queue;

    private String state;

    private String finalStatus;

    private Integer progress;

    private String trackingUI;

    private String trackingUrl;

    private String applicationType;

    private Long startedTime;

    private Long finishedTime;

    private String amContainerLogs;

}
