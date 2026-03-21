package com.lacus.dataquality.flow.batch.sink;

import com.lacus.dataquality.context.SparkRuntimeEnvironment;
import com.lacus.dataquality.utils.ConfigUtils;
import com.lacus.dataquality.utils.ParserUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * JDBC 数据写入器
 */
public class JdbcSink implements Sink {

    private static final Logger logger = LoggerFactory.getLogger(JdbcSink.class);

    private final Map<String, Object> config;

    private static final List<String> REQUIRED_KEYS = Arrays.asList(
            ConfigUtils.URL, ConfigUtils.TABLE, ConfigUtils.USER, ConfigUtils.PASSWORD
    );

    public JdbcSink(Map<String, Object> config) {
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
                return "JdbcWriter missing required config: " + key;
            }
        }
        return null;
    }

    @Override
    public void prepare(SparkRuntimeEnvironment env) {
        // 设置默认写入模式
        if (!ConfigUtils.has(config, ConfigUtils.SAVE_MODE)) {
            config.put(ConfigUtils.SAVE_MODE, "append");
        }
    }

    @Override
    public void write(Dataset<Row> data, SparkRuntimeEnvironment env) {
        // 如果配置了 sql，先通过 sql 查询得到目标数据
        if (ConfigUtils.has(config, ConfigUtils.SQL)) {
            String sql = ConfigUtils.getString(config, ConfigUtils.SQL);
            data = env.sparkSession().sql(sql);
        }

        String url = ConfigUtils.getString(config, ConfigUtils.URL);
        String database = ConfigUtils.getString(config, ConfigUtils.DATABASE);
        String table = ConfigUtils.getString(config, ConfigUtils.TABLE);
        String user = ConfigUtils.getString(config, ConfigUtils.USER);
        String password = ParserUtils.decode(ConfigUtils.getString(config, ConfigUtils.PASSWORD));
        String driver = ConfigUtils.getString(config, ConfigUtils.DRIVER);
        String saveModeStr = ConfigUtils.getString(config, ConfigUtils.SAVE_MODE);

        String dbtable = (database != null && !database.isEmpty())
                ? database + "." + table
                : table;

        SaveMode saveMode = resolveSaveMode(saveModeStr);
        logger.info("JdbcWriter writing to {}, saveMode={}", dbtable, saveMode);

        org.apache.spark.sql.DataFrameWriter<Row> writer = data.write()
                .format("jdbc")
                .option("url", url)
                .option("dbtable", dbtable)
                .option("user", user)
                .option("password", password)
                .mode(saveMode);

        if (driver != null && !driver.isEmpty()) {
            writer = writer.option("driver", driver);
        }

        writer.save();
    }

    private SaveMode resolveSaveMode(String mode) {
        if (mode == null) {
            return SaveMode.Append;
        }
        switch (mode.toLowerCase()) {
            case "overwrite":
                return SaveMode.Overwrite;
            case "ignore":
                return SaveMode.Ignore;
            case "errorifexists":
                return SaveMode.ErrorIfExists;
            default:
                return SaveMode.Append;
        }
    }
}
