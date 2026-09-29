package com.lacus.dataquality.flow.batch.transform;

import com.lacus.dataquality.context.SparkRuntimeEnvironment;
import com.lacus.dataquality.utils.ConfigUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * SQL 数据转换器
 * 通过 SparkSQL 对临时视图执行 SQL 变换
 */
public class SqlTransform implements Transform {

    private static final Logger logger = LoggerFactory.getLogger(SqlTransform.class);

    private final Map<String, Object> config;

    public SqlTransform(Map<String, Object> config) {
        this.config = config;
    }

    @Override
    public Map<String, Object> getConfig() {
        return config;
    }

    @Override
    public String validateConfig() {
        if (!ConfigUtils.has(config, ConfigUtils.SQL)) {
            return "SqlTransformer missing required config: sql";
        }
        return null;
    }

    @Override
    public Dataset<Row> transform(Dataset<Row> data, SparkRuntimeEnvironment env) {
        String sql = ConfigUtils.getString(config, ConfigUtils.SQL);
        logger.info("SqlTransformer executing SQL: {}", sql);
        return env.sparkSession().sql(sql);
    }
}
