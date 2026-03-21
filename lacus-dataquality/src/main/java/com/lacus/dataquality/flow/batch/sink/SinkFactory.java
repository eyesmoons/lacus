package com.lacus.dataquality.flow.batch.sink;

import com.lacus.dataquality.config.SinkConfig;
import com.lacus.dataquality.context.SparkRuntimeEnvironment;
import com.lacus.dataquality.enums.WriterType;
import com.lacus.dataquality.exception.DataQualityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Sink 工厂类
 */
public class SinkFactory {

    private static final Logger logger = LoggerFactory.getLogger(SinkFactory.class);

    private static final SinkFactory INSTANCE = new SinkFactory();

    private SinkFactory() {
    }

    public static SinkFactory getInstance() {
        return INSTANCE;
    }

    /**
     * 根据配置列表创建并初始化所有 Sink
     */
    public List<Sink> getSinks(SparkRuntimeEnvironment env, List<SinkConfig> configs) {
        List<Sink> sinks = new ArrayList<>();
        if (configs == null || configs.isEmpty()) {
            return sinks;
        }
        for (SinkConfig config : configs) {
            Sink sink = createSink(config);
            if (sink != null) {
                String error = sink.validateConfig();
                if (error != null) {
                    throw new DataQualityException("Sink config validation failed: " + error);
                }
                sink.prepare(env);
                sinks.add(sink);
            }
        }
        return sinks;
    }

    private Sink createSink(SinkConfig config) {
        WriterType type = WriterType.getType(config.getType());
        if (type == null) {
            throw new DataQualityException("Unknown sink type: " + config.getType());
        }
        switch (type) {
            case JDBC:
                return new JdbcSink(config.getConfig());
            case HDFS_FILE:
            case LOCAL_FILE:
                return new FileSink(config.getConfig(), type);
            case DQ_RESULT:
                return new DqResultSink(config.getConfig());
            default:
                throw new DataQualityException("Unsupported sink type: " + type);
        }
    }
}
