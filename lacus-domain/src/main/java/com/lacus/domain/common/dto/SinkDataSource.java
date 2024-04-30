package com.lacus.domain.common.dto;

import lombok.Data;

/**
 * @created by shengyu on 2023/9/6 10:01
 */
@Data
public class SinkDataSource {
    private String dataSourceType;
    private String dataSourceName;
    private String ip;
    private Integer port;
    private String userName;
    private String password;
    private String dbName;
    private String hostPort;
}