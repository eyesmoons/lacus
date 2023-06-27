package com.lacus.domain.datasync.job.dto;

import lombok.Data;

import java.util.List;

@Data
public class SourceConf {
    private String jobName;
    private String bootStrapServer;
    private String topic;
    private String hostname;
    private Integer port;
    private String username;
    private String password;
    private List<String> databaseList;
    private List<String> tableList;
    private String syncType;
    private Long timeStamp;
}
