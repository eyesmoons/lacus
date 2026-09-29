package com.lacus.dataquality;

import com.lacus.dataquality.config.DataQualityConfiguration;
import com.lacus.dataquality.config.EnvConfig;
import com.lacus.dataquality.context.DataQualityContext;
import com.lacus.dataquality.context.SparkRuntimeEnvironment;
import com.lacus.dataquality.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据质量引擎入口
 * 通过 spark-submit 调用，接收 JSON 格式的 DataQualityConfiguration 作为第一个参数
 *
 * 示例：
 *   spark-submit \
 *     --master yarn \
 *     --deploy-mode cluster \
 *     --class com.lacus.dataquality.DataQualityEngine \
 *     lacus-dataquality-jar-with-dependencies.jar \
 *     '{"name":"xxx","env":{"type":"batch"},"source":[...],"transform":[...],"sink":[...]}'
 */
public class DataQualityEngine {

    private static final Logger logger = LoggerFactory.getLogger(DataQualityEngine.class);

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            logger.error("DataQualityEngine requires a JSON config as the first argument");
            System.exit(1);
        }

        String jsonConfig = args[0];
        logger.info("Received DataQualityConfiguration JSON, length={}", jsonConfig.length());

        // Step 1: 反序列化配置
        DataQualityConfiguration configuration = JsonUtils.fromJson(jsonConfig, DataQualityConfiguration.class);
        if (configuration == null) {
            logger.error("Failed to parse DataQualityConfiguration from JSON");
            System.exit(1);
        }

        // Step 2: 校验配置
        configuration.validate();

        // Step 3: 判断是否需要 Hive 支持
        boolean hiveSupport = configuration.getSource() != null
                && configuration.getSource().stream()
                .anyMatch(s -> "HIVE".equalsIgnoreCase(s.getType()));

        // Step 4: 构建 Spark 环境配置
        EnvConfig envConfig = configuration.getEnv();
        Map<String, String> envMap = envConfig.getConfig() != null
                ? new HashMap<>(envConfig.getConfig())
                : new HashMap<>();

        // 注入任务名称到 Spark app name
        if (!envMap.containsKey("spark.app.name")) {
            envMap.put("spark.app.name", "DQ-" + configuration.getName());
        }

        // Step 5: 创建 Spark 运行时环境
        SparkRuntimeEnvironment sparkEnv = new SparkRuntimeEnvironment(envMap, hiveSupport);

        try {
            // Step 6: 创建执行上下文并执行
            DataQualityContext context = new DataQualityContext(sparkEnv, configuration);
            context.execute();
        } finally {
            sparkEnv.stop();
        }
    }
}
