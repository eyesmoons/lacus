package com.lacus.dataquality.execution;

import com.lacus.dataquality.context.SparkRuntimeEnvironment;
import com.lacus.dataquality.exception.ConfigRuntimeException;
import com.lacus.dataquality.flow.batch.source.Source;
import com.lacus.dataquality.flow.batch.transform.Transform;
import com.lacus.dataquality.flow.batch.sink.Sink;
import com.lacus.dataquality.utils.ConfigUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Spark 批处理执行器
 * 协调 source -> transform -> sink 的数据流转
 */
public class SparkBatchExecution {

    private static final Logger logger = LoggerFactory.getLogger(SparkBatchExecution.class);

    private final SparkRuntimeEnvironment environment;

    public SparkBatchExecution(SparkRuntimeEnvironment environment) {
        this.environment = environment;
    }

    /**
     * 执行批处理流程：source -> transform -> sink
     */
    public void execute(List<Source> sources,
                        List<Transform> transforms,
                        List<Sink> sinks) {

        if (sources.isEmpty()) {
            logger.warn("No sources configured, skipping execution.");
            return;
        }

        // Step 1: 注册所有 Source 为临时视图（只读一次，避免重复 JDBC 请求产生重复列引用）
        for (Source source : sources) {
            registerSourceTempView(source);
        }

        // Step 2: 从已注册的临时视图读取，而非再次调用 source.read()
        String firstOutputTable = ConfigUtils.getString(sources.get(0).getConfig(), ConfigUtils.OUTPUT_TABLE);
        Dataset<Row> ds = environment.sparkSession().read().table(firstOutputTable);

        // Step 3: 依次执行 Transform
        for (Transform transform : transforms) {
            ds = executeTransform(transform, ds);
        }

        // Step 4: 依次执行 Sink
        for (Sink sink : sinks) {
            executeSink(sink, ds);
        }
    }

    private void registerSourceTempView(Source source) {
        Map<String, Object> conf = source.getConfig();
        if (ConfigUtils.has(conf, ConfigUtils.OUTPUT_TABLE)) {
            String tableName = ConfigUtils.getString(conf, ConfigUtils.OUTPUT_TABLE);
            Dataset<Row> ds = source.read(environment);
            ds.createOrReplaceTempView(tableName);
            logger.info("Registered source temp view: {}", tableName);
        } else {
            throw new ConfigRuntimeException(
                    "Source [" + source.getClass().getSimpleName() + "] must set output_table config");
        }
    }

    private Dataset<Row> executeTransform(Transform transform, Dataset<Row> current) {
        Map<String, Object> conf = transform.getConfig();

        Dataset<Row> input = current;

        // 如果配置了 input_table，从临时视图合并读取
        if (ConfigUtils.has(conf, ConfigUtils.INPUT_TABLE)) {
            String[] tables = ConfigUtils.getString(conf, ConfigUtils.INPUT_TABLE).split(",");
            Dataset<Row> merged = null;
            for (String tbl : tables) {
                Dataset<Row> tblDs = environment.sparkSession().read().table(tbl.trim());
                merged = (merged == null) ? tblDs : merged.union(tblDs);
            }
            input = merged != null ? merged : current;
        }

        // 如果配置了 tmp_table，先注册中间视图
        if (ConfigUtils.has(conf, ConfigUtils.TMP_TABLE)) {
            String tmpTable = ConfigUtils.getString(conf, ConfigUtils.TMP_TABLE);
            if (input != null) {
                input.createOrReplaceTempView(tmpTable);
                logger.info("Registered tmp view: {}", tmpTable);
            }
        }

        Dataset<Row> result = transform.transform(input, environment);

        // 如果配置了 output_table，注册结果视图
        if (ConfigUtils.has(conf, ConfigUtils.OUTPUT_TABLE)) {
            String outputTable = ConfigUtils.getString(conf, ConfigUtils.OUTPUT_TABLE);
            result.createOrReplaceTempView(outputTable);
            logger.info("Registered transform output view: {}", outputTable);
        }

        return result;
    }

    private void executeSink(Sink sink, Dataset<Row> current) {
        Map<String, Object> conf = sink.getConfig();
        Dataset<Row> input = current;

        // 如果配置了 input_table，从临时视图读取
        if (ConfigUtils.has(conf, ConfigUtils.INPUT_TABLE)) {
            String inputTable = ConfigUtils.getString(conf, ConfigUtils.INPUT_TABLE);
            input = environment.sparkSession().read().table(inputTable);
        }

        sink.write(input, environment);
    }
}
