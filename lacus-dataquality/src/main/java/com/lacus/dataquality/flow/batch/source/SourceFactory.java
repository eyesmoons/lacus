package com.lacus.dataquality.flow.batch.source;

import com.lacus.dataquality.config.SourceConfig;
import com.lacus.dataquality.context.SparkRuntimeEnvironment;
import com.lacus.dataquality.enums.ReaderType;
import com.lacus.dataquality.exception.DataQualityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Source 工厂类
 * 根据配置类型创建对应的 Source 实例
 */
public class SourceFactory {

    private static final Logger logger = LoggerFactory.getLogger(SourceFactory.class);

    private static final SourceFactory INSTANCE = new SourceFactory();

    private SourceFactory() {
    }

    public static SourceFactory getInstance() {
        return INSTANCE;
    }

    /**
     * 根据配置列表创建并初始化所有 Source
     */
    public List<Source> getSources(SparkRuntimeEnvironment env, List<SourceConfig> configs) {
        List<Source> sources = new ArrayList<>();
        if (configs == null || configs.isEmpty()) {
            return sources;
        }
        for (SourceConfig config : configs) {
            Source source = createSource(config);
            if (source != null) {
                String error = source.validateConfig();
                if (error != null) {
                    throw new DataQualityException("Source config validation failed: " + error);
                }
                source.prepare(env);
                sources.add(source);
            }
        }
        return sources;
    }

    private Source createSource(SourceConfig config) {
        ReaderType type = ReaderType.getType(config.getType());
        if (type == null) {
            throw new DataQualityException("Unknown source type: " + config.getType());
        }
        switch (type) {
            case JDBC:
                return new JdbcSource(config.getConfig());
            case HIVE:
                return new HiveSource(config.getConfig());
            default:
                throw new DataQualityException("Unsupported source type: " + type);
        }
    }
}
