package com.lacus.domain.dataquality;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lacus.common.core.page.PageDTO;
import com.lacus.common.exception.CustomException;
import com.lacus.utils.CommonPropertyUtils;
import com.lacus.dao.dataquality.entity.DqRuleEntity;
import com.lacus.dao.dataquality.entity.DqRuleTemplateEntity;
import com.lacus.dao.dataquality.entity.DqRuleVO;
import com.lacus.dao.dataquality.mapper.DqRuleMapper;
import com.lacus.dao.metadata.entity.MetaDatasourceEntity;
import com.lacus.datasource.api.DataSourcePlugin;
import com.lacus.datasource.manager.DataSourcePluginManager;
import com.lacus.datasource.model.ConnectionParam;
import com.lacus.domain.dataquality.command.AddDqRuleCommand;
import com.lacus.domain.dataquality.command.RuleCheckParams;
import com.lacus.domain.dataquality.command.UpdateDqRuleCommand;
import com.lacus.domain.dataquality.query.DqRuleQuery;
import com.lacus.service.dataquality.IDqRuleService;
import com.lacus.service.dataquality.IDqRuleTemplateService;
import com.lacus.service.dataquality.IDqCheckResultService;
import com.lacus.service.dataquality.IDqExecutionLogService;
import com.lacus.service.dataquality.IDqStatisticsValueService;
import com.lacus.service.metadata.IMetaDataSourceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据质量规则业务逻辑
 */
@Slf4j
@Service
public class DqRuleBusiness {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Autowired
    private IDqRuleService dqRuleService;

    @Autowired
    private IDqRuleTemplateService dqRuleTemplateService;

    @Autowired
    private DqRuleMapper dqRuleMapper;

    @Autowired
    private IMetaDataSourceService metaDataSourceService;

    @Autowired
    private DataSourcePluginManager dataSourcePluginManager;

    @Autowired
    private IDqCheckResultService dqCheckResultService;

    @Autowired
    private IDqExecutionLogService dqExecutionLogService;

    @Autowired
    private IDqStatisticsValueService dqStatisticsValueService;

    /**
     * 分页查询规则列表（关联模板和数据源名称）
     */
    public PageDTO pageList(DqRuleQuery query) {
        Page<DqRuleVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<DqRuleVO> result = (Page<DqRuleVO>) dqRuleMapper.selectPageWithJoin(
                page, query.getRuleName(), query.getTemplateId(), query.getEnabled());
        return new PageDTO(result.getRecords(), result.getTotal());
    }

    /**
     * 新增规则
     */
    public DqRuleVO addRule(AddDqRuleCommand command) {
        if (dqRuleService.isRuleNameDuplicated(null, command.getRuleName())) {
            throw new CustomException("规则名称[" + command.getRuleName() + "]已存在");
        }
        // 校验模板存在
        DqRuleTemplateEntity template = dqRuleTemplateService.getById(command.getTemplateId());
        if (ObjectUtils.isEmpty(template)) {
            throw new CustomException("规则模板[" + command.getTemplateId() + "]不存在");
        }
        DqRuleEntity entity = new DqRuleEntity();
        entity.setRuleName(command.getRuleName());
        entity.setTemplateId(command.getTemplateId());
        entity.setDescription(command.getDescription());
        entity.setEnabled(command.getEnabled() != null ? command.getEnabled() : 1);
        // 数据源/库/表/字段作为独立列存储
        entity.setDatasourceId(command.getDatasourceId());
        entity.setDbName(command.getDbName());
        entity.setTableName(command.getTableName());
        if (command.getFieldNames() != null && !command.getFieldNames().isEmpty()) {
            entity.setFieldNames(command.getFieldNames());
        }
        // rule_config 只存步骤四的校验规则
        entity.setRuleConfig(buildRuleConfig(command.getRuleCheckParams()));
        // spark 任务参数
        entity.setSparkParams(command.getSparkParams());
        entity.insert();
        return enrichVO(entity, template, null);
    }

    /**
     * 修改规则（patch 语义：只更新非 null 字段）
     */
    public void updateRule(UpdateDqRuleCommand command) {
        DqRuleEntity entity = dqRuleService.getById(command.getId());
        if (ObjectUtils.isEmpty(entity)) {
            throw new CustomException("规则[" + command.getId() + "]不存在");
        }
        if (command.getRuleName() != null && !command.getRuleName().isEmpty()) {
            if (dqRuleService.isRuleNameDuplicated(command.getId(), command.getRuleName())) {
                throw new CustomException("规则名称[" + command.getRuleName() + "]已存在");
            }
            entity.setRuleName(command.getRuleName());
        }
        if (command.getTemplateId() != null) {
            DqRuleTemplateEntity tpl = dqRuleTemplateService.getById(command.getTemplateId());
            if (ObjectUtils.isEmpty(tpl)) {
                throw new CustomException("规则模板[" + command.getTemplateId() + "]不存在");
            }
            entity.setTemplateId(command.getTemplateId());
        }
        if (command.getDatasourceId() != null) {
            entity.setDatasourceId(command.getDatasourceId());
        }
        if (command.getDbName() != null) {
            entity.setDbName(command.getDbName());
        }
        if (command.getTableName() != null) {
            entity.setTableName(command.getTableName());
        }
        if (command.getFieldNames() != null) {
            entity.setFieldNames(command.getFieldNames().isEmpty() ? null : command.getFieldNames());
        }
        if (command.getRuleCheckParams() != null) {
            entity.setRuleConfig(buildRuleConfig(command.getRuleCheckParams()));
        }
        if (command.getSparkParams() != null) {
            entity.setSparkParams(command.getSparkParams());
        }
        if (command.getDescription() != null) {
            entity.setDescription(command.getDescription());
        }
        if (command.getEnabled() != null) {
            entity.setEnabled(command.getEnabled());
        }
        dqRuleService.updateById(entity);
    }

    /**
     * 删除规则（级联清理关联检测结果、统计值快照、执行记录）
     */
    public void deleteRule(Long id) {
        DqRuleEntity entity = dqRuleService.getById(id);
        if (ObjectUtils.isEmpty(entity)) {
            throw new CustomException("规则[" + id + "]不存在");
        }
        // 1. 先删子表数据，避免脏数据
        dqCheckResultService.removeByRuleId(id);
        dqStatisticsValueService.removeByRuleId(id);
        dqExecutionLogService.removeByRuleId(id);
        // 2. 再删规则本身
        dqRuleService.removeById(id);
        log.info("规则[{}]及其关联的检测结果、统计值、执行记录已全部删除", id);
    }

    /**
     * 获取规则详情（关联模板和数据源名称）
     */
    public DqRuleVO detail(Long id) {
        DqRuleVO vo = dqRuleMapper.selectVOById(id);
        if (ObjectUtils.isEmpty(vo)) {
            throw new CustomException("规则[" + id + "]不存在");
        }
        return vo;
    }

    // ==================== 私有方法 ====================

    /**
     * 将步骤四的校验参数序列化为 JSON 存入 rule_config。
     * 只保存校验规则字段，不包含数据源/库/表/字段信息（它们存在独立列）。
     */
    private String buildRuleConfig(RuleCheckParams params) {
        if (params == null) return "{}";
        try {
            Map<String, Object> config = new HashMap<>();
            // 通用字段
            if (params.getCheckMethod()   != null) config.put("checkMethod",   params.getCheckMethod());
            if (params.getOperator()      != null) config.put("operator",      params.getOperator());
            if (params.getExpectedType()  != null) config.put("expectedType",  params.getExpectedType());
            if (params.getExpectedValue() != null) config.put("expectedValue", params.getExpectedValue());
            // 及时性模板专属字段
            if (params.getField2()     != null) config.put("field2",     params.getField2());
            if (params.getTimeUnit()   != null) config.put("timeUnit",   params.getTimeUnit());
            if (params.getThreshold()  != null) config.put("threshold",  params.getThreshold());
            if (params.getRefTable()   != null) config.put("refTable",   params.getRefTable());
            if (params.getJoinField()  != null) config.put("joinField",  params.getJoinField());
            // 有效性模板专属字段
            if (params.getRegexPattern() != null) config.put("regexPattern", params.getRegexPattern());
            if (params.getLengthOp()     != null) config.put("lengthOp",     params.getLengthOp());
            if (params.getLength()       != null) config.put("length",       params.getLength());
            // 稳定性模板专属字段
            if (params.getStatMethod()   != null) config.put("statMethod",   params.getStatMethod());
            return MAPPER.writeValueAsString(config);
        } catch (Exception e) {
            log.error("Failed to build rule config", e);
            throw new CustomException("校验规则配置生成失败: " + e.getMessage());
        }
    }

    /**
     * 组装展示用 VO（补充 templateName/datasourceName 等关联字段）
     */
    private DqRuleVO enrichVO(DqRuleEntity entity, DqRuleTemplateEntity template, String datasourceName) {
        DqRuleVO vo = new DqRuleVO(entity);
        if (template != null) {
            vo.setTemplateName(template.getTemplateName());
            vo.setTemplateCode(template.getTemplateCode());
            vo.setTemplateIcon(template.getTemplateIcon());
            vo.setTemplateColor(template.getTemplateColor());
        }
        if (datasourceName != null) {
            vo.setDatasourceName(datasourceName);
        }
        return vo;
    }

    /** 复用 Spring 主库连接信息作为 DQ 结果写入库（无需单独配置） */
    @Value("${spring.datasource.druid.master.url}")
    private String resultJdbcUrl;

    @Value("${spring.datasource.druid.master.username}")
    private String resultJdbcUser;

    @Value("${spring.datasource.druid.master.password}")
    private String resultJdbcPassword;

    @Value("${spring.datasource.driverClassName:com.mysql.cj.jdbc.Driver}")
    private String resultJdbcDriver;

    // ==================== Spark 任务配置构建 ====================

    /** 错误数据行 HDFS 根路径 key（在 common.properties 中配置） */
    private static final String DQ_ERROR_HDFS_PATH_KEY = "dataquality.error.data.hdfs.path";

    /** 检测结果写入表（明细结果） */
    private static final String DQ_EXECUTE_RESULT_TABLE   = "dq_check_result";
    /** 统计值写入表 */
    private static final String DQ_STATISTICS_VALUE_TABLE = "dq_statistics_value";

    /**
     * 在提交 Spark 任务时动态构建完整的 DataQualityConfiguration JSON。
     *
     * <p>结构对齐 DataQuality 参考格式：
     * <pre>
     * {
     *   "name": "DQ-xxx",
     *   "env": { "type": "batch" },
     *   "source": [{ "type": "JDBC", "config": { 真实 JDBC 连接参数 } }],
     *   "transform": [
     *     { "type": "sql", "config": { "index": 1, "output_table": "{templateCode}_items", "sql": "SELECT * ..." } },
     *     { "type": "sql", "config": { "index": 2, "output_table": "{templateCode}_count", "sql": "SELECT COUNT(*) AS statistics_value ..." } }
     *   ],
     *   "sink": [
     *     { "type": "JDBC", "config": { 写入执行结果表 } },
     *     { "type": "JDBC", "config": { 写入统计值表 } },
     *     { "type": "hdfs_file", "config": { 写入 HDFS 错误数据行 } }
     *   ]
     * }
     * </pre>
     */
    public String buildSparkConfig(DqRuleEntity rule, Long logId, Long ruleId) {
        try {
            // 解析 rule_config（只含校验规则字段）
            RuleCheckParams params = (rule.getRuleConfig() != null && !rule.getRuleConfig().isEmpty())
                    ? MAPPER.readValue(rule.getRuleConfig(), RuleCheckParams.class)
                    : new RuleCheckParams();

            // 查询规则模板
            DqRuleTemplateEntity template = dqRuleTemplateService.getById(rule.getTemplateId());
            String templateCode = (template != null && template.getTemplateCode() != null)
                    ? template.getTemplateCode().toLowerCase() : "dq";

            Map<String, Object> dqConfig = new LinkedHashMap<>();
            dqConfig.put("name", "DQ-" + rule.getRuleName());

            // === env ===
            Map<String, Object> env = new HashMap<>();
            env.put("type", "batch");
            env.put("config", null);
            dqConfig.put("env", env);

            // === source ===
            // output_table 名称：{dbName}_{tableName}，供后续 transform SQL 引用
            String outputTable = sanitizeTableName(rule.getDbName()) + "_" + sanitizeTableName(rule.getTableName());
            Map<String, Object> sourceConfig = buildReaderConfig(rule.getDatasourceId(), rule.getDbName(), rule.getTableName(), outputTable);
            Map<String, Object> source = new HashMap<>();
            source.put("type", sourceConfig.remove("__type__"));  // 从 config 取出 type
            source.put("config", sourceConfig);
            List<Map<String, Object>> sources = new ArrayList<>();
            sources.add(source);
            dqConfig.put("source", sources);

            // === transform ===
            // 第一段：SELECT * 过滤问题数据行，output_table = {templateCode}_items
            String itemsTable = templateCode + "_items";
            String itemsSql = buildItemsSql(template, outputTable, rule.getFieldNames(), params);
            // 第二段：聚合统计 SQL，output_table = {templateCode}_count
            // 来自模板的 check_sql_pattern，替换占位符后执行
            String countTable = templateCode + "_count";
            String countSql = buildCountSql(template, outputTable, rule.getFieldNames(), params, itemsTable);

            Map<String, Object> transform1Config = new HashMap<>();
            transform1Config.put("index", 1);
            transform1Config.put("output_table", itemsTable);
            transform1Config.put("sql", itemsSql);
            Map<String, Object> transform1 = new HashMap<>();
            transform1.put("type", "sql");
            transform1.put("config", transform1Config);

            Map<String, Object> transform2Config = new HashMap<>();
            transform2Config.put("index", 2);
            transform2Config.put("output_table", countTable);
            transform2Config.put("sql", countSql);
            Map<String, Object> transform2 = new HashMap<>();
            transform2.put("type", "sql");
            transform2.put("config", transform2Config);

            List<Map<String, Object>> transforms = new ArrayList<>();
            transforms.add(transform1);
            transforms.add(transform2);
            dqConfig.put("transform", transforms);

            // === sink ===
            String resultUrl      = resultJdbcUrl;
            String resultUser     = resultJdbcUser;
            String resultPassword = resultJdbcPassword;
            String resultDriver   = resultJdbcDriver;
            String hdfsRootPath   = CommonPropertyUtils.getString(DQ_ERROR_HDFS_PATH_KEY);
            String errorDataPath  = (hdfsRootPath != null ? hdfsRootPath : "hdfs:///dq_error_data")
                    + "/" + ruleId + "_" + logId;

            // 提取 MySQL URL 中的数据库名
            String resultDatabase = extractDatabaseFromUrl(resultUrl);

            // Sink 1：写执行结果（logId/ruleId/统计值/通过标志等）
            String executeResultSql = buildExecuteResultSql(ruleId, logId, rule.getRuleName(),
                    templateCode, countTable, params);
            Map<String, Object> sink1Config = new HashMap<>();
            sink1Config.put("database", resultDatabase);
            sink1Config.put("url", resultUrl);
            sink1Config.put("user", resultUser);
            sink1Config.put("password", resultPassword);
            sink1Config.put("driver", resultDriver);
            sink1Config.put("table", DQ_EXECUTE_RESULT_TABLE);
            sink1Config.put("sql", executeResultSql);
            Map<String, Object> sink1 = new HashMap<>();
            sink1.put("type", "JDBC");
            sink1.put("config", sink1Config);

            // Sink 2：写统计值快照
            String statisticsValueSql = buildStatisticsValueSql(ruleId, logId, templateCode, countTable);
            Map<String, Object> sink2Config = new HashMap<>();
            sink2Config.put("database", resultDatabase);
            sink2Config.put("url", resultUrl);
            sink2Config.put("user", resultUser);
            sink2Config.put("password", resultPassword);
            sink2Config.put("driver", resultDriver);
            sink2Config.put("table", DQ_STATISTICS_VALUE_TABLE);
            sink2Config.put("sql", statisticsValueSql);
            Map<String, Object> sink2 = new HashMap<>();
            sink2.put("type", "JDBC");
            sink2.put("config", sink2Config);

            // Sink 3：错误数据行写入 HDFS
            Map<String, Object> sink3Config = new HashMap<>();
            sink3Config.put("path", errorDataPath);
            sink3Config.put("format", "csv");
            sink3Config.put("save_mode", "overwrite");
            sink3Config.put("input_table", itemsTable);
            Map<String, Object> sink3 = new HashMap<>();
            sink3.put("type", "hdfs_file");
            sink3.put("config", sink3Config);

            List<Map<String, Object>> sinks = new ArrayList<>();
            sinks.add(sink1);
            sinks.add(sink2);
            sinks.add(sink3);
            dqConfig.put("sink", sinks);

            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(dqConfig);
        } catch (Exception e) {
            log.error("Failed to build spark config, ruleId={}, logId={}", ruleId, logId, e);
            throw new CustomException("Spark 配置生成失败: " + e.getMessage());
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 计算错误数据行 HDFS 输出路径（供外部持久化使用）
     */
    public String buildErrorDataPath(Long ruleId, Long logId) {
        String hdfsRootPath = CommonPropertyUtils.getString(DQ_ERROR_HDFS_PATH_KEY);
        return (hdfsRootPath != null ? hdfsRootPath : "hdfs:///dq_error_data")
                + "/" + ruleId + "_" + logId;
    }

    /**
     * 通过 datasourceId 获取数据源的真实 JDBC 连接信息，构建 reader config。
     * 返回 Map 中包含 __type__ 键（reader type，如 JDBC/HIVE），其余为 JDBC 连接字段。
     */
    private Map<String, Object> buildReaderConfig(Long datasourceId, String dbName, String tableName, String outputTable) {
        Map<String, Object> config = new HashMap<>();
        if (datasourceId == null) {
            // 兜底：无数据源时返回占位信息
            config.put("__type__", "JDBC");
            config.put("database", dbName);
            config.put("table", tableName);
            config.put("output_table", outputTable);
            config.put("url", "");
            config.put("user", "");
            config.put("password", "");
            config.put("driver", "");
            return config;
        }
        try {
            MetaDatasourceEntity datasource = metaDataSourceService.getById(datasourceId);
            if (datasource == null) {
                throw new CustomException("数据源[" + datasourceId + "]不存在");
            }
            // 通过插件管理器获取对应插件
            String dsType = datasource.getType() != null ? datasource.getType().toUpperCase() : "JDBC";
            DataSourcePlugin plugin = dataSourcePluginManager.getProcessor(dsType);

            // 解析连接参数
            ConnectionParam connParam = MAPPER.readValue(datasource.getConnectionParams(), ConnectionParam.class);

            String jdbcUrl    = (plugin != null) ? plugin.getJdbcUrl(connParam) : "";
            String driverName = (plugin != null) ? plugin.getDriverName() : "";
            String username   = connParam.getUsername() != null ? connParam.getUsername() : "";
            String password   = connParam.getPassword() != null ? connParam.getPassword() : "";

            // reader type：HIVE 保持原样，其余关系型数据库统一为 JDBC
            String readerType = "HIVE".equalsIgnoreCase(dsType) ? "HIVE" : "JDBC";
            config.put("__type__", readerType);
            config.put("database", dbName);
            config.put("table", tableName);
            config.put("output_table", outputTable);
            config.put("url", jdbcUrl);
            config.put("user", username);
            config.put("password", password);
            config.put("driver", driverName);
        } catch (Exception e) {
            log.error("Failed to build reader config for datasourceId={}", datasourceId, e);
            // 出错时返回基础结构，不中断整体配置构建
            config.put("__type__", "JDBC");
            config.put("database", dbName);
            config.put("table", tableName);
            config.put("output_table", outputTable);
            config.put("url", "");
            config.put("user", "");
            config.put("password", "");
            config.put("driver", "");
        }
        return config;
    }

    /**
     * 构建明细行过滤 SQL（SELECT * WHERE 条件），结果存入 {templateCode}_items。
     * 优先使用模板的 items_sql_pattern 占位符替换；若无则退回内置 switch-case。
     * <p>
     * 占位符说明：
     * {outputTable} - reader 的 output_table 名称（{db}_{table}）
     * {field}       - 主检测字段
     * {field2}      - 对比字段（一致性/时间比较模板）
     * {timeUnit}    - 时间单位秒数（1=秒/60=分/3600=时/86400=天），Spark SQL 用 UNIX_TIMESTAMP 差值除以该值
     * {threshold}   - 时间差阈值
     * {refTable}    - 参考表名（两表时间比较）
     * {joinField}   - 关联字段（两表时间比较）
     * {regexPattern}- 正则表达式
     * {lengthOp}    - 长度操作符
     * {length}      - 长度阈值
     * {statMethod}  - 统计方式（AVG/MAX/MIN/SUM/COUNT）
     */
    private String buildItemsSql(DqRuleTemplateEntity template, String outputTable,
                                  String fieldNamesStr, RuleCheckParams params) {
        List<String> fields = parseFields(fieldNamesStr);
        String firstField = fields.isEmpty() ? "*" : fields.get(0);
        String templateCode = template != null ? template.getTemplateCode() : "";

        // 优先使用模板配置的 items_sql_pattern 做占位符替换
        if (template != null && template.getItemsSqlPattern() != null && !template.getItemsSqlPattern().isEmpty()) {
            String pattern = template.getItemsSqlPattern();
            pattern = pattern.replace("{outputTable}", outputTable);
            pattern = pattern.replace("{field}", firstField);
            pattern = pattern.replace("{fields}", String.join(", ", fields));
            if (params != null) {
                if (notBlank(params.getField2()))       pattern = pattern.replace("{field2}",       params.getField2());
                if (notBlank(params.getTimeUnit()))     pattern = pattern.replace("{timeUnit}",     params.getTimeUnit());
                if (params.getThreshold() != null)      pattern = pattern.replace("{threshold}",    String.valueOf(params.getThreshold()));
                if (notBlank(params.getRefTable())) {
                    // 参考表带库名前缀（如 db.table）
                    String refDbN = notBlank(params.getRefDbName()) ? params.getRefDbName() + "." : "";
                    pattern = pattern.replace("{refTable}", refDbN + params.getRefTable());
                }
                if (notBlank(params.getJoinField()))    pattern = pattern.replace("{joinField}",    params.getJoinField());
                if (notBlank(params.getRegexPattern())) pattern = pattern.replace("{regexPattern}", params.getRegexPattern());
                if (notBlank(params.getLengthOp()))     pattern = pattern.replace("{lengthOp}",     params.getLengthOp());
                if (params.getLength() != null)         pattern = pattern.replace("{length}",       String.valueOf(params.getLength()));
                if (notBlank(params.getStatMethod()))   pattern = pattern.replace("{statMethod}",   params.getStatMethod());
            }
            // 检测是否还有未替换的占位符（说明用户漏填必要参数）
            if (pattern.contains("{") && pattern.contains("}")) {
                log.warn("items_sql_pattern 存在未替换的占位符，可能是用户未填写必要参数，SQL: {}", pattern);
            }
            return pattern;
        }

        // 兜底：内置逻辑，覆盖 12 种模板
        if (params == null) params = new RuleCheckParams();
        switch (templateCode != null ? templateCode : "") {

            // ===== 完整性 =====
            case "NULL_CHECK":
                return String.format("SELECT * FROM %s WHERE %s IS NULL", outputTable, firstField);

            case "EMPTY_STRING_CHECK":
                return String.format("SELECT * FROM %s WHERE %s IS NULL OR TRIM(%s) = ''",
                        outputTable, firstField, firstField);

            // ===== 唯一性 =====
            case "UNIQUENESS_CHECK":
                // 用窗口函数代替 JOIN，彻底避免 Spark SQL 中 JOIN 两侧列名冲突
                return String.format(
                    "SELECT * FROM (SELECT *, COUNT(*) OVER (PARTITION BY %s) AS _cnt FROM %s) _tmp WHERE _cnt > 1",
                    firstField, outputTable);

            case "DISTINCT_COUNT_CHECK":
                // 无明细行 SQL，统计值直接来自 check_sql_pattern
                return String.format("SELECT * FROM %s WHERE 1=0", outputTable);

            case "DUPLICATE_COUNT_CHECK":
                // 用窗口函数代替 JOIN，彻底避免 Spark SQL 中 JOIN 两侧列名冲突
                return String.format(
                    "SELECT * FROM (SELECT *, COUNT(*) OVER (PARTITION BY %s) AS _cnt FROM %s) _tmp WHERE _cnt > 1",
                    firstField, outputTable);

            // ===== 及时性 =====
            case "SINGLE_TABLE_TIME_CHECK": {
                String field2          = params.getField2()    != null ? params.getField2()    : firstField;
                double thresh          = params.getThreshold() != null ? params.getThreshold() : 0;
                // timeUnit 存秒数字符串：1=秒, 60=分, 3600=时, 86400=天
                String timeUnitSec     = params.getTimeUnit()  != null ? params.getTimeUnit()  : "3600";
                return String.format(
                    "SELECT * FROM %s WHERE (UNIX_TIMESTAMP(%s) - UNIX_TIMESTAMP(%s)) / %s > %s",
                    outputTable, field2, firstField, timeUnitSec, thresh);
            }
            case "CROSS_TABLE_TIME_CHECK": {
                String field2          = params.getField2()    != null ? params.getField2()    : firstField;
                double thresh          = params.getThreshold() != null ? params.getThreshold() : 0;
                String timeUnitSec     = params.getTimeUnit()  != null ? params.getTimeUnit()  : "3600";
                String refDbName       = params.getRefDbName() != null ? params.getRefDbName() : "";
                String refTableName    = params.getRefTable()  != null ? params.getRefTable()  : outputTable;
                // 带库名前缀，格式：dbName.tableName
                String fullRefTable    = notBlank(refDbName) ? refDbName + "." + refTableName : refTableName;
                String joinField       = params.getJoinField() != null ? params.getJoinField() : "id";
                return String.format(
                    "SELECT * FROM (SELECT a.*, (UNIX_TIMESTAMP(b.%s) - UNIX_TIMESTAMP(a.%s)) / %s AS _diff" +
                    " FROM %s a JOIN %s b ON a.%s = b.%s) _tmp WHERE _diff > %s",
                    field2, firstField, timeUnitSec, outputTable, fullRefTable, joinField, joinField, thresh);
            }

            // ===== 有效性 =====
            case "REGEX_CHECK": {
                String pattern = params.getRegexPattern() != null ? params.getRegexPattern() : "";
                return String.format("SELECT * FROM %s WHERE %s NOT RLIKE '%s'", outputTable, firstField, pattern);
            }
            case "LENGTH_CHECK": {
                String op  = params.getLengthOp() != null ? params.getLengthOp() : "!=";
                int    len = params.getLength()   != null ? params.getLength()   : 0;
                return String.format("SELECT * FROM %s WHERE LENGTH(%s) %s %d", outputTable, firstField, op, len);
            }

            // ===== 一致性 =====
            case "CONSISTENCY_CHECK": {
                String field2 = params.getField2() != null ? params.getField2() : firstField;
                return String.format(
                    "SELECT * FROM %s WHERE %s != %s OR (%s IS NULL AND %s IS NOT NULL) OR (%s IS NOT NULL AND %s IS NULL)",
                    outputTable, firstField, field2, firstField, field2, firstField, field2);
            }

            // ===== 稳定性 =====
            case "STAT_CHECK":
            case "FLUCTUATION_CHECK":
                // 统计类模板无明细行（直接输出聚合值）
                return String.format("SELECT * FROM %s WHERE 1=0", outputTable);

            default:
                return String.format("SELECT * FROM %s WHERE 1=0", outputTable);
        }
    }

    /**
     * 构建聚合统计 SQL（第二段 transformer），output_table = {templateCode}_count。
     * 来自模板的 check_sql_pattern，支持占位符替换：
     * - 对于计数类模板：SELECT COUNT(*) AS statistics_value FROM {templateCode}_items
     * - 对于统计类模板（STAT_CHECK/FLUCTUATION_CHECK/DISTINCT_COUNT_CHECK）：直接在 outputTable 上聚合
     */
    private String buildCountSql(DqRuleTemplateEntity template, String outputTable,
                                  String fieldNamesStr, RuleCheckParams params, String itemsTable) {
        List<String> fields = parseFields(fieldNamesStr);
        String firstField = fields.isEmpty() ? "*" : fields.get(0);

        // 使用模板的 check_sql_pattern 做占位符替换
        String pattern = (template != null && template.getCheckSqlPattern() != null)
                ? template.getCheckSqlPattern()
                : "SELECT COUNT(*) AS statistics_value FROM " + itemsTable;

        // 通用占位符替换
        pattern = pattern.replace("{outputTable}", outputTable);
        pattern = pattern.replace("{field}", firstField);
        pattern = pattern.replace("{fields}", String.join(", ", fields));
        pattern = pattern.replace("{templateCode}_items", itemsTable);

        // 模板专属占位符替换
        if (params != null) {
            if (notBlank(params.getStatMethod())) pattern = pattern.replace("{statMethod}", params.getStatMethod());
        }
        if (pattern.contains("{") && pattern.contains("}")) {
            log.warn("check_sql_pattern 存在未替换的占位符，可能是用户未填写必要参数，SQL: {}", pattern);
        }
        return pattern;
    }

    /**
     * 构建写入执行结果表的 SELECT SQL（从 countTable 读出统计值，附带元信息字段）。
     */
    private String buildExecuteResultSql(Long ruleId, Long logId, String ruleName,
                                          String templateCode, String countTable,
                                          RuleCheckParams params) {
        String now = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        String expectedValue = (params != null && params.getExpectedValue() != null)
                ? String.valueOf(params.getExpectedValue()) : "0";
        String operator    = (params != null && params.getOperator()    != null) ? params.getOperator()    : "";
        String checkMethod = (params != null && params.getCheckMethod() != null) ? params.getCheckMethod() : "";
        String expectedType = (params != null && params.getExpectedType() != null) ? params.getExpectedType() : "";

        // 列名严格对齐 dq_check_result 表结构（已去除 datasource_id/db_name/table_name/field_names）
        return String.format(
            "SELECT %d AS log_id, %d AS rule_id, '%s' AS rule_name, '%s' AS template_code," +
            " %s.statistics_value AS actual_value," +
            " %s AS expected_value, '%s' AS expected_type, '%s' AS check_method, '%s' AS operator," +
            " CAST(NULL AS BOOLEAN) AS pass_flag, '%s' AS create_time FROM %s",
            logId, ruleId, ruleName, templateCode,
            countTable,
            expectedValue, expectedType, checkMethod, operator,
            now, countTable);
    }

    /**
     * 构建写入统计值快照表的 SELECT SQL。
     */
    private String buildStatisticsValueSql(Long ruleId, Long logId, String templateCode, String countTable) {
        String now = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        return String.format(
            "SELECT %d AS rule_id, '%s' AS template_code, %d AS log_id," +
            " '%s_count.statistics_value' AS statistics_name," +
            " %s.statistics_value AS statistics_value, '%s' AS create_time FROM %s",
            ruleId, templateCode, logId,
            templateCode, countTable, now, countTable);
    }

    /**
     * 判断字符串非 null 且非空白（用于占位符替换前的有效性检查）
     */
    private boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    /**
     * 将字段字符串（逗号分隔）解析为列表
     */
    private List<String> parseFields(String fieldNamesStr) {
        List<String> fields = new ArrayList<>();
        if (fieldNamesStr != null && !fieldNamesStr.isEmpty()) {
            for (String f : fieldNamesStr.split(",")) {
                String trimmed = f.trim();
                if (!trimmed.isEmpty()) fields.add(trimmed);
            }
        }
        return fields;
    }

    /**
     * 将库名/表名中的特殊字符替换为下划线，用于拼接 output_table 名称
     */
    private String sanitizeTableName(String name) {
        if (name == null || name.isEmpty()) return "unknown";
        return name.replaceAll("[^a-zA-Z0-9_]", "_");
    }

    /**
     * 从 JDBC URL 中提取数据库名称（取最后一个 '/' 后、'?' 前的部分）
     */
    private String extractDatabaseFromUrl(String jdbcUrl) {
        if (jdbcUrl == null || jdbcUrl.isEmpty()) return "";
        try {
            // jdbc:mysql://host:port/dbName?params
            int slashIdx = jdbcUrl.lastIndexOf('/');
            if (slashIdx < 0) return "";
            String afterSlash = jdbcUrl.substring(slashIdx + 1);
            int qIdx = afterSlash.indexOf('?');
            return qIdx > 0 ? afterSlash.substring(0, qIdx) : afterSlash;
        } catch (Exception e) {
            return "";
        }
    }
}
