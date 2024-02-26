package com.lacus.domain.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @created by shengyu on 2023/9/6 10:01
 */
@Data
public class SourceV2 implements Serializable {
    private static final long serialVersionUID = 7830490112239790483L;
    private String sourceName;
    private String hostname;
    private String port;
    private String username;
    private String password;
    private List<String> databaseList;
    private List<String> tableList;
    private String syncType;
    private Long timeStamp;
    private String savePoints;
    private String bootStrapServers;
    private List<String> topics;
    private String groupId;
}
