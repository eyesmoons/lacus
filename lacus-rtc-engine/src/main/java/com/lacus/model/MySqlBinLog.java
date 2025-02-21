package com.lacus.model;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.io.Serializable;

/**
 * @created by shengyu on 2023/9/7 16:26
 */
@Data
public class MySqlBinLog implements Serializable {
    private static final long serialVersionUID = 1L;
    private String db;
    private String tableName;
    private JSONObject before;
    private JSONObject after;
    private String op;
}
