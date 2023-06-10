package com.lacus.domain.datasync.job.model;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.Map;

@Data
public class FlinkTaskEngine {
    private String ip;
    private Integer port;
    private String userName;
    private String password;
    private String dbName;
    private Map<String, JSONObject> ColumnMap;
}
