package com.lacus.model;

import lombok.Data;

import java.util.List;

/**
 * @created by shengyu on 2023/9/6 10:01
 */
@Data
public class SourceJobConf {
    private String jobName;
    private String bootStrapServer;
    private String topic;
    private String hostname;
    private String port;
    private String username;
    private String password;
    private List<String> databaseList;
    private List<String> tableList;
    private String syncType;
    private Long timeStamp;
}
