package com.lacus.st.sink;

import com.alibaba.fastjson2.JSONObject;
import com.google.auto.service.AutoService;
import com.lacus.st.abstracts.AbstractStSink;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.annotation.StField;
import com.lacus.st.annotation.StTag;
import com.lacus.st.annotation.StTag.TagDefinition;
import com.lacus.st.interfaces.StComponentInterface;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * MySQL Sink 组件
 * 属性对齐 SeaTunnel 2.3.12 Mysql(Jdbc) Sink Options。
 * 文档：https://seatunnel.apache.org/zh-CN/docs/2.3.12/connector-v2/sink/Mysql
 *
 * @author lacus
 */
@Data
@Slf4j
@StComponent(
        type = StComponent.ComponentType.SINK,
        name = "mysql_sink",
        displayName = "MySQL数据库输出",
        description = "通过 JDBC 写入 MySQL，支持批处理/流模式、精确一次(XA)",
        version = "2.0.0",
        author = "lacus",
        connectorKey = "Jdbc"
)
@StTag({
        @TagDefinition(name = "数据源配置", displayName = "数据源配置", order = 1, description = "数据源配置"),
        @TagDefinition(name = "数据配置", displayName = "数据配置", order = 2, description = "query/database/table/primary_keys"),
        @TagDefinition(name = "性能配置", displayName = "性能配置", order = 3, description = "batch_size/connection_check_timeout_sec/max_retries"),
        @TagDefinition(name = "表结构配置", displayName = "表结构配置", order = 4, description = "schema_save_mode/data_save_mode/custom_sql/field_ide"),
        @TagDefinition(name = "事务配置", displayName = "事务配置", order = 5, description = "is_exactly_once/xa/max_commit_attempts/transaction_timeout_sec/auto_commit"),
        @TagDefinition(name = "其他", displayName = "其他", order = 6, description = "generate_sink_sql/enable_upsert/properties")
})
@AutoService(StComponentInterface.class)
public class MysqlSink extends AbstractStSink {

    @StField(tag = "数据源配置", order = 1, required = true, enName = "datasourceId", cnName = "数据源",
            placeHolder = "请选择数据源", formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.URL, dictUrl = "/metadata/datasource/list")
    private String datasourceId;

    @StField(tag = "数据源配置", order = 2, required = true, enName = "database", cnName = "数据库",
            placeHolder = "请选择数据库", formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.URL, dictUrl = "/metadata/db/list/{datasourceId}")
    private String database;

    @StField(tag = "数据源配置", order = 3, required = true, enName = "table", cnName = "数据表",
            placeHolder = "请选择数据表", formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.URL, dictUrl = "/metadata/table/listTable")
    private String table;

    @StField(tag = "数据配置", order = 1, required = false, enName = "query", cnName = "SQL 写入语句",
            placeHolder = "INSERT INTO t(name, age) VALUES(?, ?)", formType = StField.FormType.TEXT_AREA)
    private String query;

    @StField(tag = "数据配置", order = 2, required = false, enName = "primary_keys", cnName = "主键字段",
            placeHolder = "id,name", formType = StField.FormType.TEXT)
    private String primaryKeys;

    @StField(tag = "性能配置", order = 1, required = false, enName = "batch_size", cnName = "批处理大小",
            defaultValue = "1000", placeHolder = "1000", formType = StField.FormType.POSITIVE_NUMBER)
    private Integer batchSize;

    @StField(tag = "性能配置", order = 2, required = false, enName = "connection_check_timeout_sec", cnName = "连接检查超时(秒)",
            defaultValue = "30", placeHolder = "30", formType = StField.FormType.POSITIVE_NUMBER)
    private Integer connectionCheckTimeoutSec;

    @StField(tag = "性能配置", order = 3, required = false, enName = "max_retries", cnName = "最大重试次数",
            defaultValue = "0", placeHolder = "0", formType = StField.FormType.POSITIVE_NUMBER)
    private Integer maxRetries;

    @StField(tag = "表结构配置", order = 1, required = false, enName = "schema_save_mode", cnName = "表结构保存模式",
            defaultValue = "CREATE_SCHEMA_WHEN_NOT_EXIST",
            formType = StField.FormType.SINGLE_SELECT, dictType = StField.DictType.ENUM,
            dictEnum = {"RECREATE_SCHEMA", "CREATE_SCHEMA_WHEN_NOT_EXIST", "ERROR_WHEN_SCHEMA_NOT_EXIST", "IGNORE"})
    private String schemaSaveMode;

    @StField(tag = "表结构配置", order = 2, required = false, enName = "data_save_mode", cnName = "数据保存模式",
            defaultValue = "APPEND_DATA",
            formType = StField.FormType.SINGLE_SELECT, dictType = StField.DictType.ENUM,
            dictEnum = {"DROP_DATA", "APPEND_DATA", "CUSTOM_PROCESSING", "ERROR_WHEN_DATA_EXISTS"})
    private String dataSaveMode;

    @StField(tag = "表结构配置", order = 3, required = false, enName = "custom_sql", cnName = "自定义 SQL",
            description = "data_save_mode=CUSTOM_PROCESSING 时填写，任务前执行",
            placeHolder = "TRUNCATE TABLE t", formType = StField.FormType.TEXT_AREA)
    private String customSql;

    @StField(tag = "表结构配置", order = 4, required = false, enName = "field_ide", cnName = "字段名大小写",
            formType = StField.FormType.SINGLE_SELECT, dictType = StField.DictType.ENUM,
            dictEnum = {"ORIGINAL", "UPPERCASE", "LOWERCASE"})
    private String fieldIde;

    @StField(tag = "事务配置", order = 1, required = false, enName = "is_exactly_once", cnName = "精确一次",
            defaultValue = "false", formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM, dictEnum = {"false", "true"})
    private Boolean isExactlyOnce;

    @StField(tag = "事务配置", order = 2, required = false, enName = "xa_data_source_class_name", cnName = "XA 数据源类名",
            defaultValue = "com.mysql.cj.jdbc.MysqlXADataSource",
            placeHolder = "com.mysql.cj.jdbc.MysqlXADataSource", formType = StField.FormType.TEXT)
    private String xaDataSourceClassName;

    @StField(tag = "事务配置", order = 3, required = false, enName = "max_commit_attempts", cnName = "提交重试次数",
            defaultValue = "3", placeHolder = "3", formType = StField.FormType.POSITIVE_NUMBER)
    private Integer maxCommitAttempts;

    @StField(tag = "事务配置", order = 4, required = false, enName = "transaction_timeout_sec", cnName = "事务超时(秒)",
            defaultValue = "-1", placeHolder = "-1", formType = StField.FormType.NUMBER)
    private Integer transactionTimeoutSec;

    @StField(tag = "事务配置", order = 5, required = false, enName = "auto_commit", cnName = "自动提交",
            defaultValue = "true", formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM, dictEnum = {"false", "true"})
    private Boolean autoCommit;

    @StField(tag = "其他", order = 1, required = false, enName = "generate_sink_sql", cnName = "自动生成 SQL",
            defaultValue = "true", formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM, dictEnum = {"false", "true"})
    private Boolean generateSinkSql;

    @StField(tag = "其他", order = 2, required = false, enName = "enable_upsert", cnName = "启用 UPSERT",
            defaultValue = "true", formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM, dictEnum = {"false", "true"})
    private Boolean enableUpsert;

    @StField(tag = "其他", order = 3, required = false, enName = "properties", cnName = "附加连接参数",
            description = "如 rewriteBatchedStatements=true",
            placeHolder = "rewriteBatchedStatements=true", formType = StField.FormType.TEXT)
    private String properties;

    @Override
    protected boolean doCheckConnection() {
        return true;
    }

    @Override
    public JSONObject buildTaskConfig(JSONObject connectionConfig, Long datasourceId) {
        JSONObject config = new JSONObject();
        putIfNotEmpty(config, "url", connectionConfig.getString("url"));
        config.put("driver", "com.mysql.cj.jdbc.Driver");
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
        putIfNotEmpty(config, "properties", connectionConfig.getString("properties"));
        return config;
    }
}
