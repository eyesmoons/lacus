package com.lacus.dataquality.config;

import java.util.Map;

/**
 * Source 数据源配置（对应 JSON 中的 source 列表）
 */
public class SourceConfig {

    /**
     * 数据源类型：JDBC / HIVE
     */
    private String type;

    /**
     * 配置参数，如 database, table, url, user, password, output_table 等
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
