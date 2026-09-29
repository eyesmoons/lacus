package com.lacus.dataquality.flow.batch.source;

import com.lacus.dataquality.context.SparkRuntimeEnvironment;
import com.lacus.dataquality.utils.ConfigUtils;
import com.lacus.dataquality.utils.ParserUtils;
import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC 数据读取器
 * 通过 Spark JDBC 接口从关系型数据库读取数据
 */
public class JdbcSource implements Source {

    private static final Logger logger = LoggerFactory.getLogger(JdbcSource.class);

    private final Map<String, Object> config;

    private static final List<String> REQUIRED_KEYS = Arrays.asList(
            ConfigUtils.URL, ConfigUtils.TABLE, ConfigUtils.USER,
            ConfigUtils.PASSWORD, ConfigUtils.OUTPUT_TABLE
    );

    public JdbcSource(Map<String, Object> config) {
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
                return "JdbcReader missing required config: " + key;
            }
        }
        return null;
    }

    @Override
    public Dataset<Row> read(SparkRuntimeEnvironment env) {
        String url = ConfigUtils.getString(config, ConfigUtils.URL);
        String database = ConfigUtils.getString(config, ConfigUtils.DATABASE);
        String table = ConfigUtils.getString(config, ConfigUtils.TABLE);
        String user = ConfigUtils.getString(config, ConfigUtils.USER);
        String password = ParserUtils.decode(ConfigUtils.getString(config, ConfigUtils.PASSWORD));
        String driver = ConfigUtils.getString(config, ConfigUtils.DRIVER);

        String dbtable = (database != null && !database.isEmpty())
                ? database + "." + table
                : table;

        logger.info("JdbcReader reading from {}", dbtable);

        DataFrameReader reader = env.sparkSession().read()
                .format("jdbc")
                .option("url", url)
                .option("dbtable", dbtable)
                .option("user", user)
                .option("password", password);

        if (driver != null && !driver.isEmpty()) {
            reader = reader.option("driver", driver);
        }

        // 处理额外的 jdbc.xxx 配置
        Map<String, String> extraOptions = new HashMap<>();
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            if (entry.getKey().startsWith("jdbc.") && entry.getValue() != null) {
                extraOptions.put(entry.getKey().substring(5), String.valueOf(entry.getValue()));
            }
        }
        if (!extraOptions.isEmpty()) {
            reader = reader.options(extraOptions);
        }

        return reader.load();
    }
}
