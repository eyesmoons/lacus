package com.lacus.job.model;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class FlinkTaskEngine implements Serializable {
    private static final long serialVersionUID = 5979498851672199679L;
    private String ip;
    private Integer port;
    private String userName;
    private String password;
    private String dbName;
    private Map<String, JSONObject> ColumnMap;
}
