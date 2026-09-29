package com.lacus.st.sink;

import com.alibaba.fastjson2.JSONObject;
import com.google.auto.service.AutoService;
import com.lacus.st.abstracts.AbstractStSink;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.annotation.StField;
import com.lacus.st.annotation.StTag;
import com.lacus.st.annotation.StTag.TagDefinition;
import com.lacus.st.interfaces.StComponentInterface;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * JDBC Sink 组件
 * 通用 JDBC 数据库输出，属性对齐 SeaTunnel 2.3.12 Jdbc Sink Options。
 * 文档：https://seatunnel.apache.org/zh-CN/docs/2.3.12/connector-v2/sink/Jdbc
 *
 * @author lacus
 */
@Setter
@Slf4j
@StComponent(
        type = StComponent.ComponentType.SINK,
        name = "jdbc_sink",
        displayName = "JDBC数据库输出",
        description = "通过JDBC写入数据，支持批处理模式和流模式，支持并发写入，支持精确一次语义(XA)",
        version = "2.0.0",
        author = "lacus",
        connectorKey = "Jdbc"
)
@StTag({
        @TagDefinition(name = "数据源配置", displayName = "数据源配置", order = 1, description = "数据源配置"),
        @TagDefinition(name = "数据配置", displayName = "数据配置", order = 2, description = "数据操作相关配置"),
        @TagDefinition(name = "性能配置", displayName = "性能配置", order = 3, description = "性能优化相关配置"),
        @TagDefinition(name = "表结构配置", displayName = "表结构配置", order = 4, description = "表结构保存与字段规则"),
        @TagDefinition(name = "事务配置", displayName = "事务配置", order = 5, description = "事务处理相关配置"),
        @TagDefinition(name = "其他配置", displayName = "其他配置", order = 6, description = "方言、兼容模式、扩展参数")
})

@AutoService(StComponentInterface.class)
public class JdbcSink extends AbstractStSink {

    // ---------- 数据源配置 ----------
    @StField(
            tag = "数据源配置",
            order = 1,
            required = true,
            enName = "datasourceId",
            cnName = "数据源",
            placeHolder = "请选择数据源",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.URL,
            dictUrl = "/metadata/datasource/list"
    )
    private String datasourceId;

    @StField(
            tag = "数据源配置",
            order = 2,
            required = true,
            enName = "database",
            cnName = "数据库",
            placeHolder = "请选择数据库",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.URL,
            dictUrl = "/metadata/db/list/{datasourceId}"
    )
    private String database;

    @StField(
            tag = "数据源配置",
            order = 3,
            required = true,
            enName = "table",
            cnName = "数据表",
            placeHolder = "请选择数据表",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.URL,
            dictUrl = "/metadata/table/listTable"
    )
    private String table;

    @StField(
            tag = "数据源配置",
            order = 4,
            required = true,
            enName = "driver",
            cnName = "JDBC驱动类名",
            description = "连接数据库的 JDBC 驱动类名，如 MySQL: com.mysql.cj.jdbc.Driver",
            placeHolder = "com.mysql.cj.jdbc.Driver",
            formType = StField.FormType.TEXT
    )
    private String driver;

    // ---------- 数据配置 (Options: query, database, table, primary_keys) ----------
    @StField(
            tag = "数据配置",
            order = 1,
            required = false,
            enName = "query",
            cnName = "SQL写入语句",
            description = "使用 SQL 将上游数据写入数据库，如 INSERT ... 与 database/table 二选一",
            placeHolder = "INSERT INTO table(name, age) VALUES(?, ?)",
            formType = StField.FormType.TEXT_AREA
    )
    private String query;

    @StField(
            tag = "数据配置",
            order = 2,
            required = false,
            enName = "primary_keys",
            cnName = "主键字段",
            description = "主键字段列表，逗号分隔，用于辅助生成 insert/delete/update SQL",
            placeHolder = "id,name",
            formType = StField.FormType.TEXT
    )
    private String primaryKeys;

    @StField(
            tag = "数据配置",
            order = 3,
            required = false,
            enName = "generate_sink_sql",
            cnName = "自动生成SQL",
            defaultValue = "true",
            description = "根据要写入的表结构生成 SQL",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"false", "true"}
    )
    private Boolean generateSinkSql;

    @StField(
            tag = "数据配置",
            order = 4,
            required = false,
            enName = "enable_upsert",
            cnName = "启用UPSERT",
            defaultValue = "true",
            description = "通过主键更新插入；若无主键重复可设为 false 加快导入",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"false", "true"}
    )
    private Boolean enableUpsert;

    // ---------- 性能配置 (batch_size, connection_check_timeout_sec, max_retries) ----------
    @StField(
            tag = "性能配置",
            order = 1,
            required = false,
            enName = "batch_size",
            cnName = "批处理大小",
            defaultValue = "1000",
            description = "批量写入时缓冲记录数，达到后或到达 checkpoint.interval 时刷新",
            placeHolder = "1000",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer batchSize;

    @StField(
            tag = "性能配置",
            order = 2,
            required = false,
            enName = "connection_check_timeout_sec",
            cnName = "连接检查超时(秒)",
            defaultValue = "30",
            description = "验证连接有效性时等待数据库操作完成的时间(秒)",
            placeHolder = "30",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer connectionCheckTimeoutSec;

    @StField(
            tag = "性能配置",
            order = 3,
            required = false,
            enName = "max_retries",
            cnName = "最大重试次数",
            defaultValue = "0",
            description = "executeBatch 提交失败的最大重试次数",
            placeHolder = "0",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer maxRetries;

    @StField(
            tag = "性能配置",
            order = 4,
            required = false,
            enName = "use_copy_statement",
            cnName = "使用COPY语句",
            defaultValue = "false",
            description = "使用 COPY table FROM STDIN 导入，仅支持具备 getCopyAPI() 的驱动(如 PostgreSQL)",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"false", "true"}
    )
    private Boolean useCopyStatement;

    // ---------- 表结构配置 (schema_save_mode, data_save_mode, custom_sql, field_ide) ----------
    @StField(
            tag = "表结构配置",
            order = 1,
            required = false,
            enName = "schema_save_mode",
            cnName = "表结构保存模式",
            defaultValue = "CREATE_SCHEMA_WHEN_NOT_EXIST",
            description = "同步前对目标表结构的处理：不存在则创建/存在则重建/报错/忽略",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"RECREATE_SCHEMA", "CREATE_SCHEMA_WHEN_NOT_EXIST", "ERROR_WHEN_SCHEMA_NOT_EXIST", "IGNORE"}
    )
    private String schemaSaveMode;

    @StField(
            tag = "表结构配置",
            order = 2,
            required = false,
            enName = "data_save_mode",
            cnName = "数据保存模式",
            defaultValue = "APPEND_DATA",
            description = "同步前对目标数据的处理：删数据/追加/自定义SQL/有数据则报错",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"DROP_DATA", "APPEND_DATA", "CUSTOM_PROCESSING", "ERROR_WHEN_DATA_EXISTS"}
    )
    private String dataSaveMode;

    @StField(
            tag = "表结构配置",
            order = 3,
            required = false,
            enName = "custom_sql",
            cnName = "自定义SQL",
            description = "data_save_mode 为 CUSTOM_PROCESSING 时填写，任务前执行",
            placeHolder = "TRUNCATE TABLE xxx",
            formType = StField.FormType.TEXT_AREA
    )
    private String customSql;

    @StField(
            tag = "表结构配置",
            order = 4,
            required = false,
            enName = "field_ide",
            cnName = "字段名大小写",
            description = "ORIGINAL不转换/UPPERCASE转大写/LOWERCASE转小写",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"ORIGINAL", "UPPERCASE", "LOWERCASE"}
    )
    private String fieldIde;

    // ---------- 事务配置 (auto_commit, is_exactly_once, xa_data_source_class_name, max_commit_attempts, transaction_timeout_sec) ----------
    @StField(
            tag = "事务配置",
            order = 1,
            required = false,
            enName = "auto_commit",
            cnName = "自动提交",
            defaultValue = "true",
            description = "默认启用自动事务提交",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"false", "true"}
    )
    private Boolean autoCommit;

    @StField(
            tag = "事务配置",
            order = 2,
            required = false,
            enName = "is_exactly_once",
            cnName = "精确一次语义",
            defaultValue = "false",
            description = "使用 XA 事务保证精确一次，需数据库支持并设置 xa_data_source_class_name",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"false", "true"}
    )
    private Boolean isExactlyOnce;

    @StField(
            tag = "事务配置",
            order = 3,
            required = false,
            enName = "xa_data_source_class_name",
            cnName = "XA数据源类名",
            description = "如 MySQL: com.mysql.cj.jdbc.MysqlXADataSource",
            placeHolder = "com.mysql.cj.jdbc.MysqlXADataSource",
            formType = StField.FormType.TEXT
    )
    private String xaDataSourceClassName;

    @StField(
            tag = "事务配置",
            order = 4,
            required = false,
            enName = "max_commit_attempts",
            cnName = "提交重试次数",
            defaultValue = "3",
            description = "事务提交失败的最大重试次数",
            placeHolder = "3",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer maxCommitAttempts;

    @StField(
            tag = "事务配置",
            order = 5,
            required = false,
            enName = "transaction_timeout_sec",
            cnName = "事务超时(秒)",
            defaultValue = "-1",
            description = "事务开启后的超时秒数，-1 表示永不超时，可能影响精确一次语义",
            placeHolder = "-1",
            formType = StField.FormType.NUMBER
    )
    private Integer transactionTimeoutSec;

    // ---------- 其他配置 (compatible_mode, dialect, properties) ----------
    @StField(
            tag = "其他配置",
            order = 1,
            required = false,
            enName = "compatible_mode",
            cnName = "兼容模式",
            description = "如 OceanBase 填 mysql/oracle，StarRocks 填 starrocks，Postgres 9.5 及以下填 postgresLow",
            placeHolder = "mysql",
            formType = StField.FormType.TEXT
    )
    private String compatibleMode;

    @StField(
            tag = "其他配置",
            order = 2,
            required = false,
            enName = "dialect",
            cnName = "SQL方言",
            description = "指定方言，优先级高于 url。如 starrocks、mysql、Oracle、Postgres、SqlServer 等",
            placeHolder = "mysql",
            formType = StField.FormType.TEXT
    )
    private String dialect;

    @StField(
            tag = "其他配置",
            order = 3,
            required = false,
            enName = "properties",
            cnName = "附加连接参数",
            description = "附加连接配置，与 URL 同参数时优先级由驱动实现决定(如 MySQL 中属性优先)",
            placeHolder = "rewriteBatchedStatements=true",
            formType = StField.FormType.TEXT
    )
    private String properties;

    @Override
    protected boolean doCheckConnection() {
        return true;
    }

    @Override
    public JSONObject buildTaskConfig(JSONObject connectionConfig, Long datasourceId) {
        JSONObject config = new JSONObject();
        putIfNotEmpty(config, "url", connectionConfig.getString("url"));
        putIfNotEmpty(config, "driver", connectionConfig.getString("driver"));
        putIfNotEmpty(config, "user", connectionConfig.getString("username"));
        putIfNotEmpty(config, "password", connectionConfig.getString("password"));
        putIfNotEmpty(config, "query", connectionConfig.getString("query"));
        putIfNotEmpty(config, "database", connectionConfig.getString("database"));
        putIfNotEmpty(config, "table", connectionConfig.getString("table"));
        putIfNotEmpty(config, "primary_keys", connectionConfig.getString("primary_keys"));
        putIfNotEmpty(config, "connection_check_timeout_sec", connectionConfig.getInteger("connection_check_timeout_sec"));
        putIfNotEmpty(config, "max_retries", connectionConfig.getInteger("max_retries"));
        putIfNotEmpty(config, "batch_size", connectionConfig.getInteger("batch_size"));
        putIfNotEmpty(config, "is_exactly_once", connectionConfig.getBoolean("is_exactly_once"));
        putIfNotEmpty(config, "generate_sink_sql", connectionConfig.getBoolean("generate_sink_sql"));
        putIfNotEmpty(config, "xa_data_source_class_name", connectionConfig.getString("xa_data_source_class_name"));
        putIfNotEmpty(config, "max_commit_attempts", connectionConfig.getInteger("max_commit_attempts"));
        putIfNotEmpty(config, "transaction_timeout_sec", connectionConfig.getInteger("transaction_timeout_sec"));
        putIfNotEmpty(config, "auto_commit", connectionConfig.getBoolean("auto_commit"));
        putIfNotEmpty(config, "field_ide", connectionConfig.getString("field_ide"));
        putIfNotEmpty(config, "schema_save_mode", connectionConfig.getString("schema_save_mode"));
        putIfNotEmpty(config, "data_save_mode", connectionConfig.getString("data_save_mode"));
        putIfNotEmpty(config, "custom_sql", connectionConfig.getString("custom_sql"));
        putIfNotEmpty(config, "enable_upsert", connectionConfig.getBoolean("enable_upsert"));
        putIfNotEmpty(config, "use_copy_statement", connectionConfig.getBoolean("use_copy_statement"));
        putIfNotEmpty(config, "compatible_mode", connectionConfig.getString("compatible_mode"));
        putIfNotEmpty(config, "dialect", connectionConfig.getString("dialect"));
        putIfNotEmpty(config, "properties", connectionConfig.getString("properties"));
        return config;
    }
}
