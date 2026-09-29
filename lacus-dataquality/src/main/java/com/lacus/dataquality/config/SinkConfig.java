package com.lacus.dataquality.config;

import java.util.Map;

/**
 * Sink 写入配置（对应 JSON 中的 sink 列表）
 */
public class SinkConfig {

    /**
     * 写入类型：JDBC / HDFS_FILE / LOCAL_FILE
     */
    private String type;

    /**
     * 配置参数，如 database, table, url, user, password, save_mode, path, format 等
     */
    private Map<String, Object> config;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }
}
