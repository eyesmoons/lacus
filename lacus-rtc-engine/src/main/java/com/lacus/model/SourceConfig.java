package com.lacus.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @created by shengyu on 2023/9/6 10:01
 */
@Data
public class SourceConfig implements Serializable {
    private static final long serialVersionUID = -662762323409934001L;
    private String hostname;
    private String port;
    private String username;
    private String password;
    private List<String> databaseList;
    private List<String> tableList;
    private String syncType;
    private Long timeStamp;
    private String bootStrapServers;
    private List<String> topics;
    private String groupId;

    private String sourceName;

}
