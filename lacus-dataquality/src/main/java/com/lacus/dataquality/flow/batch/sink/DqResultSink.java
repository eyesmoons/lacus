package com.lacus.dataquality.flow.batch.sink;

import com.lacus.dataquality.context.SparkRuntimeEnvironment;
import com.lacus.dataquality.utils.ConfigUtils;
import com.lacus.dataquality.utils.ParserUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 数据质量检测结果专用写入器
 * <p>
 * 将 Transformer 计算出的 actual_value 写入 dq_check_result 表，
 * 并根据 check_method / operator / expected_value 计算 formula_result 和 pass_flag。
 * <p>
 * 必须配置项：
 *   url, user, password, log_id, rule_id, rule_name, check_method,
 *   operator, expected_type, expected_value(FIXED时), template_code,
 *   datasource_id, db_name, table_name, field_names, check_sql
 */
public class DqResultSink implements Sink {

    private static final Logger logger = LoggerFactory.getLogger(DqResultSink.class);

    // 专用配置 key
    public static final String LOG_ID = "log_id";
    public static final String RULE_ID = "rule_id";
    public static final String RULE_NAME = "rule_name";
    public static final String CHECK_METHOD = "check_method";
    public static final String OPERATOR = "operator";
    public static final String EXPECTED_TYPE = "expected_type";
    public static final String EXPECTED_VALUE = "expected_value";
    public static final String TEMPLATE_CODE = "template_code";
    public static final String DATASOURCE_ID = "datasource_id";
    public static final String DB_NAME = "db_name";
    public static final String TABLE_NAME = "table_name";
    public static final String FIELD_NAMES = "field_names";
    public static final String CHECK_SQL = "check_sql";
    // 结果表 JDBC 连接
    public static final String RESULT_URL = "result_url";
    public static final String RESULT_USER = "result_user";
    public static final String RESULT_PASSWORD = "result_password";
    public static final String RESULT_DRIVER = "result_driver";

    private final Map<String, Object> config;

    public DqResultSink(Map<String, Object> config) {
        this.config = config;
    }

    @Override
    public Map<String, Object> getConfig() {
        return config;
    }

    @Override
    public String validateConfig() {
        for (String key : new String[]{LOG_ID, RULE_ID, CHECK_METHOD, OPERATOR, RESULT_URL, RESULT_USER, RESULT_PASSWORD}) {
            if (!ConfigUtils.has(config, key)) {
                return "DqResultWriter missing required config: " + key;
            }
        }
        return null;
    }

    @Override
    public void prepare(SparkRuntimeEnvironment env) {
        // no-op
    }

    @Override
    public void write(Dataset<Row> data, SparkRuntimeEnvironment env) {
        // Step 1: 从检测结果 Dataset 获取 actual_value（取第一行）
        Row firstRow = data.first();
        BigDecimal actualValue = null;
        try {
            Object rawVal = firstRow.getAs("actual_value");
            if (rawVal != null) {
                actualValue = new BigDecimal(rawVal.toString());
            }
        } catch (Exception e) {
            logger.warn("Failed to parse actual_value from result dataset, use 0 as fallback", e);
            actualValue = BigDecimal.ZERO;
        }

        // Step 2: 获取 expected_value
        BigDecimal expectedValue = BigDecimal.ZERO;
        String expectedType = ConfigUtils.getString(config, EXPECTED_TYPE);
        if ("FIXED".equals(expectedType) && ConfigUtils.has(config, EXPECTED_VALUE)) {
            try {
                expectedValue = new BigDecimal(ConfigUtils.getString(config, EXPECTED_VALUE));
            } catch (Exception e) {
                logger.warn("Failed to parse expected_value", e);
            }
        }
        // 非固定值类型（统计平均等）暂时使用 0，后续可扩展历史统计逻辑
        logger.info("actual_value={}, expected_value={}, expectedType={}", actualValue, expectedValue, expectedType);

        // Step 3: 计算 formula_result
        BigDecimal formulaResult = computeFormulaResult(
                ConfigUtils.getString(config, CHECK_METHOD), actualValue, expectedValue);

        // Step 4: 判断 pass_flag
        int passFlag = evaluatePassFlag(
                ConfigUtils.getString(config, OPERATOR), formulaResult);

        logger.info("formula_result={}, operator={}, pass_flag={}", formulaResult,
                ConfigUtils.getString(config, OPERATOR), passFlag);

        // Step 5: 构建结果 DataFrame 并写入 dq_check_result
        String insertSql = buildInsertSql(actualValue, expectedValue, formulaResult, passFlag);
        logger.info("DqResultWriter inserting result, logId={}, ruleId={}",
                ConfigUtils.getString(config, LOG_ID), ConfigUtils.getString(config, RULE_ID));

        // 使用 JDBC 直接执行 INSERT（Spark JDBC Writer 需要 DataFrame，这里构建单行 DataFrame 写入）
        writeSingleRowViaJdbc(env, actualValue, expectedValue, formulaResult, passFlag);
    }

    private void writeSingleRowViaJdbc(SparkRuntimeEnvironment env,
                                       BigDecimal actualValue, BigDecimal expectedValue,
                                       BigDecimal formulaResult, int passFlag) {
        String logId = ConfigUtils.getString(config, LOG_ID);
        String ruleId = ConfigUtils.getString(config, RULE_ID);
        String ruleName = ConfigUtils.getString(config, RULE_NAME);
        String templateCode = ConfigUtils.getString(config, TEMPLATE_CODE);
        String datasourceId = ConfigUtils.getString(config, DATASOURCE_ID);
        String dbName = ConfigUtils.getString(config, DB_NAME);
        String tableName = ConfigUtils.getString(config, TABLE_NAME);
        String fieldNames = ConfigUtils.getString(config, FIELD_NAMES);
        String checkSql = ConfigUtils.getString(config, CHECK_SQL);
        String checkMethod = ConfigUtils.getString(config, CHECK_METHOD);
        String operator = ConfigUtils.getString(config, OPERATOR);
        String expectedType = ConfigUtils.getString(config, EXPECTED_TYPE);

        // 使用 Spark SQL 构建单行结果 DataFrame 写入 JDBC
        String escapedCheckSql = checkSql != null ? checkSql.replace("'", "\\'") : "";
        String escapedRuleName = ruleName != null ? ruleName.replace("'", "\\'") : "";

        String sql = String.format(
            "SELECT " +
            "CAST('%s' AS BIGINT) AS log_id, " +
            "CAST('%s' AS BIGINT) AS rule_id, " +
            "'%s' AS rule_name, " +
            "'%s' AS template_code, " +
            "CAST('%s' AS BIGINT) AS datasource_id, " +
            "'%s' AS db_name, " +
            "'%s' AS table_name, " +
            "'%s' AS field_names, " +
            "'%s' AS check_sql, " +
            "CAST(%s AS DECIMAL(20,4)) AS actual_value, " +
            "CAST(%s AS DECIMAL(20,4)) AS expected_value, " +
            "'%s' AS expected_type, " +
            "'%s' AS check_method, " +
            "'%s' AS operator, " +
            "CAST(%s AS DECIMAL(20,4)) AS formula_result, " +
            "CAST(%d AS INT) AS pass_flag, " +
            "NOW() AS create_time",
            logId, ruleId, escapedRuleName, templateCode,
            datasourceId, dbName, tableName, fieldNames, escapedCheckSql,
            actualValue, expectedValue, expectedType,
            checkMethod, operator, formulaResult, passFlag
        );

        Dataset<Row> resultDs = env.sparkSession().sql(sql);

        String url = ConfigUtils.getString(config, RESULT_URL);
        String user = ConfigUtils.getString(config, RESULT_USER);
        String password = ParserUtils.decode(ConfigUtils.getString(config, RESULT_PASSWORD));
        String driver = ConfigUtils.getString(config, RESULT_DRIVER);

        org.apache.spark.sql.DataFrameWriter<Row> writer = resultDs.write()
                .format("jdbc")
                .option("url", url)
                .option("dbtable", "dq_check_result")
                .option("user", user)
                .option("password", password)
                .mode(SaveMode.Append);

        if (driver != null && !driver.isEmpty()) {
            writer = writer.option("driver", driver);
        }
        writer.save();
        logger.info("DqResultWriter wrote check result to dq_check_result, logId={}", logId);
    }

    /**
     * 根据 checkMethod 计算公式结果
     * expected_minus_actual  → expected - actual
     * actual_minus_expected  → actual - expected
     * actual_div_expected    → actual / expected
     * diff_div_expected      → (expected - actual) / expected
     */
    private BigDecimal computeFormulaResult(String checkMethod, BigDecimal actual, BigDecimal expected) {
        if (actual == null) actual = BigDecimal.ZERO;
        if (expected == null) expected = BigDecimal.ZERO;
        if (checkMethod == null) return actual;
        switch (checkMethod) {
            case "expected_minus_actual":
                return expected.subtract(actual);
            case "actual_minus_expected":
                return actual.subtract(expected);
            case "actual_div_expected":
                if (expected.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
                return actual.divide(expected, 4, java.math.RoundingMode.HALF_UP);
            case "diff_div_expected":
                if (expected.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
                return expected.subtract(actual).divide(expected, 4, java.math.RoundingMode.HALF_UP);
            default:
                return actual;
        }
    }

    /**
     * 根据操作符和公式结果判断是否通过
     */
    private int evaluatePassFlag(String operator, BigDecimal formulaResult) {
        if (operator == null || formulaResult == null) return 0;
        int cmp = formulaResult.compareTo(BigDecimal.ZERO);
        switch (operator) {
            case "=":  return cmp == 0 ? 1 : 0;
            case "!=": return cmp != 0 ? 1 : 0;
            case ">":  return cmp > 0 ? 1 : 0;
            case ">=": return cmp >= 0 ? 1 : 0;
            case "<":  return cmp < 0 ? 1 : 0;
            case "<=": return cmp <= 0 ? 1 : 0;
            default:   return 0;
        }
    }

    private String buildInsertSql(BigDecimal actualValue, BigDecimal expectedValue,
                                   BigDecimal formulaResult, int passFlag) {
        return String.format("INSERT INTO dq_check_result(log_id,rule_id,...) VALUES(%s,%s,...)",
                ConfigUtils.getString(config, LOG_ID), ConfigUtils.getString(config, RULE_ID));
    }
}
