package com.lacus.job.model;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

@Data
public class MySqlBinLog {
    private String db;
    private String tableName;
    private JSONObject before;
    private JSONObject after;
    private String op;
}