package com.lacus.dataquality.config;

import java.util.Map;

/**
 * Transform 转换配置（对应 JSON 中的 transform 列表）
 */
public class TransformConfig {

    /**
     * 转换类型：SQL
     */
    private String type;

    /**
     * 配置参数，如 sql, input_table, output_table, tmp_table 等
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
