package com.lacus.common.utils.beans;

import lombok.Data;

import java.io.Serializable;

@Data
public class MetaDatasource implements Serializable {
    private String ip;
    private Integer port;
    private String dbName;
    private String user;
    private String password;
}
