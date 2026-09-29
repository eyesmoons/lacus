package com.lacus.config;

import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.Serializable;

/**
 * Flink环境配置类
 */
public class FlinkEnvConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final int CHECKPOINT_INTERVAL = 60000;
    private static final int CHECKPOINT_MIN_PAUSE = 1000;
    private static final int CHECKPOINT_TIMEOUT = 60000;
    private static final int DEFAULT_PARALLELISM = 1;

    public static StreamExecutionEnvironment createExecutionEnvironment() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        configureBasicSettings(env);
        configureCheckpoint(env);
        return env;
    }

    private static void configureBasicSettings(StreamExecutionEnvironment env) {
        env.setParallelism(DEFAULT_PARALLELISM);
        env.getConfig().setAutoWatermarkInterval(0);
    }

    private static void configureCheckpoint(StreamExecutionEnvironment env) {
        env.enableCheckpointing(CHECKPOINT_INTERVAL, CheckpointingMode.EXACTLY_ONCE);
        CheckpointConfig checkpointConfig = env.getCheckpointConfig();
        checkpointConfig.setMinPauseBetweenCheckpoints(CHECKPOINT_MIN_PAUSE);
        checkpointConfig.setCheckpointTimeout(CHECKPOINT_TIMEOUT);
        checkpointConfig.setMaxConcurrentCheckpoints(1);
        checkpointConfig.setExternalizedCheckpointCleanup(
            CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION
        );
    }
}
