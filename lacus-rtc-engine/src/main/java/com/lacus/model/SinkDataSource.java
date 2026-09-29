package com.lacus.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @created by shengyu on 2023/9/6 10:01
 */
@Data
public class SinkDataSource implements Serializable {
    private static final long serialVersionUID = -3668452129478214973L;
    private String dataSourceName;
    private String ip;
    private Integer port;
    private String userName;
    private String password;
    private String dbName;

    // 额外参数
    private String hostPort;
}