package com.lacus.dataquality.config;

import java.util.List;

/**
 * 数据质量任务主配置
 * 包含 env / source / transform / sink，以 JSON 格式传递给 Spark Application
 * JSON 结构顺序：source -> transform -> sink
 */
public class DataQualityConfiguration {

    /**
     * 任务名称
     */
    private String name;

    /**
     * Spark 运行环境配置
     */
    private EnvConfig env;

    /**
     * 数据源读取配置列表（对应 JSON key: source）
     */
    private List<SourceConfig> source;

    /**
     * 数据转换配置列表（对应 JSON key: transform）
     */
    private List<TransformConfig> transform;

    /**
     * 数据写入配置列表（对应 JSON key: sink）
     */
    private List<SinkConfig> sink;

    public DataQualityConfiguration() {
    }

    public DataQualityConfiguration(String name, EnvConfig env,
                                     List<SourceConfig> source,
                                     List<TransformConfig> transform,
                                     List<SinkConfig> sink) {
        this.name = name;
        this.env = env;
        this.source = source;
        this.transform = transform;
        this.sink = sink;
    }

    /**
     * 简单校验配置有效性
     */
    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("DataQualityConfiguration.name must not be empty");
        }
        if (env == null) {
            throw new IllegalArgumentException("DataQualityConfiguration.env must not be null");
        }
        if (source == null || source.isEmpty()) {
            throw new IllegalArgumentException("DataQualityConfiguration.source must not be empty");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EnvConfig getEnv() {
        return env;
    }

    public void setEnv(EnvConfig env) {
        this.env = env;
    }

    public List<SourceConfig> getSource() {
        return source;
    }

    public void setSource(List<SourceConfig> source) {
        this.source = source;
    }

    public List<TransformConfig> getTransform() {
        return transform;
    }

    public void setTransform(List<TransformConfig> transform) {
        this.transform = transform;
    }

    public List<SinkConfig> getSink() {
        return sink;
    }

    public void setSink(List<SinkConfig> sink) {
        this.sink = sink;
    }
}
