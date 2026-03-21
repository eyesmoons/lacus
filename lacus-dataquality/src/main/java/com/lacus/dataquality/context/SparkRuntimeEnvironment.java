package com.lacus.dataquality.context;

import com.lacus.dataquality.execution.SparkBatchExecution;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Spark 运行时环境
 * 负责创建并管理 SparkSession
 */
public class SparkRuntimeEnvironment {

    private static final Logger logger = LoggerFactory.getLogger(SparkRuntimeEnvironment.class);

    private static final String TYPE_KEY = "type";
    private static final String BATCH = "batch";

    private final SparkSession sparkSession;
    private final String type;

    public SparkRuntimeEnvironment(Map<String, String> envConfigMap, boolean hiveSupport) {
        this.type = envConfigMap != null ? envConfigMap.getOrDefault(TYPE_KEY, BATCH) : BATCH;

        SparkConf conf = new SparkConf();
        conf.set("spark.sql.crossJoin.enabled", "true");

        if (envConfigMap != null) {
            envConfigMap.forEach((k, v) -> {
                if (!TYPE_KEY.equals(k) && v != null) {
                    conf.set(k, v);
                }
            });
        }

        SparkSession.Builder builder = SparkSession.builder().config(conf);
        this.sparkSession = hiveSupport
                ? builder.enableHiveSupport().getOrCreate()
                : builder.getOrCreate();

        logger.info("SparkRuntimeEnvironment initialized, type={}, hiveSupport={}", type, hiveSupport);
    }

    public SparkSession sparkSession() {
        return sparkSession;
    }

    public boolean isBatch() {
        return BATCH.equalsIgnoreCase(type);
    }

    public SparkBatchExecution getBatchExecution() {
        return new SparkBatchExecution(this);
    }

    public void stop() {
        if (sparkSession != null) {
            sparkSession.stop();
        }
    }
}
