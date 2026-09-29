package com.lacus.dataquality.config;

import java.util.Map;

/**
 * Spark 环境配置
 */
public class EnvConfig {

    /**
     * 任务类型：batch / stream
     */
    private String type = "batch";

    /**
     * Spark 配置项，如 spark.executor.instances
     */
    private Map<String, String> config;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }
}
