package com.lacus.dataquality.flow.batch.sink;

import com.lacus.dataquality.context.SparkRuntimeEnvironment;
import com.lacus.dataquality.enums.WriterType;
import com.lacus.dataquality.utils.ConfigUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 文件写入器（HDFS / 本地）
 */
public class FileSink implements Sink {

    private static final Logger logger = LoggerFactory.getLogger(FileSink.class);

    private final Map<String, Object> config;
    private final WriterType writerType;

    public FileSink(Map<String, Object> config, WriterType writerType) {
        this.config = config;
        this.writerType = writerType;
    }

    @Override
    public Map<String, Object> getConfig() {
        return config;
    }

    @Override
    public String validateConfig() {
        if (!ConfigUtils.has(config, ConfigUtils.PATH)) {
            return "FileWriter missing required config: path";
        }
        if (!ConfigUtils.has(config, ConfigUtils.FORMAT)) {
            return "FileWriter missing required config: format";
        }
        return null;
    }

    @Override
    public void write(Dataset<Row> data, SparkRuntimeEnvironment env) {
        String path = ConfigUtils.getString(config, ConfigUtils.PATH);
        String format = ConfigUtils.getString(config, ConfigUtils.FORMAT);
        String saveModeStr = ConfigUtils.getString(config, ConfigUtils.SAVE_MODE);
        SaveMode saveMode = "overwrite".equalsIgnoreCase(saveModeStr) ? SaveMode.Overwrite : SaveMode.Append;

        // input_table 的读取由 executeSink 统一处理后传入，此处直接使用 data
        logger.info("FileWriter writing to path={}, format={}, saveMode={}", path, format, saveMode);
        data.write().format(format).mode(saveMode).save(path);
    }
}
