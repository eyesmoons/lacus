package com.lacus.dataquality.flow.batch.source;

import com.lacus.dataquality.context.SparkRuntimeEnvironment;
import com.lacus.dataquality.utils.ConfigUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Hive 数据读取器
 * 通过 SparkSQL 执行 Hive 查询读取数据（需启用 Hive Support）
 */
public class HiveSource implements Source {

    private static final Logger logger = LoggerFactory.getLogger(HiveSource.class);

    private final Map<String, Object> config;

    private static final List<String> REQUIRED_KEYS = Arrays.asList(
            ConfigUtils.SQL, ConfigUtils.OUTPUT_TABLE
    );

    public HiveSource(Map<String, Object> config) {
        this.config = config;
    }

    @Override
    public Map<String, Object> getConfig() {
        return config;
    }

    @Override
    public String validateConfig() {
        for (String key : REQUIRED_KEYS) {
            if (!ConfigUtils.has(config, key)) {
                return "HiveReader missing required config: " + key;
            }
        }
        return null;
    }

    @Override
    public Dataset<Row> read(SparkRuntimeEnvironment env) {
        String sql = ConfigUtils.getString(config, ConfigUtils.SQL);
        logger.info("HiveReader executing SQL: {}", sql);
        return env.sparkSession().sql(sql);
    }
}
