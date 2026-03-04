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
 * DB2 Sink组件
 * DB2数据库输出组件
 *
 * @author lacus
 */
@Setter
@Slf4j
@StComponent(
        type = StComponent.ComponentType.SINK,
        name = "db2_sink",
        displayName = "DB2数据库输出",
        description = "通过JDBC写入DB2数据库，支持批处理模式和流模式，支持精确一次语义",
        version = "2.0.0",
        author = "lacus",
        connectorKey = "Jdbc"
)
@StTag({
        @TagDefinition(name = "数据源配置", displayName = "数据源配置", order = 1, description = "数据源配置"),
        @TagDefinition(name = "查询配置", displayName = "查询配置", order = 2, description = "查询配置"),
        @TagDefinition(name = "性能配置", displayName = "性能配置", order = 3, description = "性能优化相关配置"),
        @TagDefinition(name = "表结构配置", displayName = "表结构配置", order = 4, description = "表结构配置"),
        @TagDefinition(name = "事务配置", displayName = "事务配置", order = 5, description = "事务处理相关配置"),
        @TagDefinition(name = "其他配置", displayName = "其他配置", order = 6, description = "其他配置")
})
@AutoService(StComponentInterface.class)
public class Db2Sink extends AbstractStSink {

    // 数据源配置
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

    // 数据配置
    @StField(
            tag = "查询配置",
            order = 1,
            required = false,
            enName = "query",
            cnName = "SQL查询语句",
            description = "使用SQL语句将上游输入数据写入DB2",
            placeHolder = "INSERT INTO test_table(name, age) VALUES(?, ?)",
            formType = StField.FormType.TEXT_AREA
    )
    private String query;

    @StField(
            tag = "查询配置",
            order = 2,
            required = false,
            enName = "primary_keys",
            cnName = "主键字段",
            description = "主键字段列表，用逗号分隔",
            placeHolder = "ID,NAME",
            formType = StField.FormType.TEXT
    )
    private String primaryKeys;

    @StField(
            tag = "查询配置",
            order = 2,
            required = false,
            enName = "generate_sink_sql",
            cnName = "自动生成SQL",
            defaultValue = "true",
            description = "根据要写入的DB2表结构生成SQL语句",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"false", "true"}
    )
    private Boolean generateSinkSql;

    // 性能配置
    @StField(
            tag = "性能配置",
            order = 1,
            required = false,
            enName = "batch_size",
            cnName = "批处理大小",
            defaultValue = "1000",
            description = "批量写入时的记录数量",
            placeHolder = "1000",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer batchSize;

    @StField(
            tag = "性能配置",
            order = 2,
            required = false,
            enName = "max_retries",
            cnName = "提交失败的重试次数",
            defaultValue = "0",
            description = "提交失败的重试次数 (执行批处理)",
            placeHolder = "提交失败的重试次数",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer max_retries;

    @StField(
            tag = "性能配置",
            order = 3,
            required = false,
            enName = "connection_check_timeout_sec",
            cnName = "连接检查超时时间(秒)",
            defaultValue = "30",
            description = "验证数据库连接所使用的操作完成的等待时间（秒）",
            placeHolder = "30",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer connectionCheckTimeoutSec;

    // 表结构配置
    @StField(
            tag = "表结构配置",
            order = 1,
            required = false,
            enName = "field_ide",
            cnName = "字段名大小写",
            defaultValue = "UPPERCASE",
            description = "DB2字段名转换规则，DB2通常使用大写",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"ORIGINAL", "UPPERCASE", "LOWERCASE"}
    )
    private String fieldIde;

    @StField(
            tag = "表结构配置",
            order = 2,
            required = false,
            enName = "schema_save_mode",
            cnName = "表结构保存模式",
            defaultValue = "CREATE_SCHEMA_WHEN_NOT_EXIST",
            description = "在启动同步任务之前，针对目标侧已有的表结构选择不同的处理方案",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"RECREATE_SCHEMA", "CREATE_SCHEMA_WHEN_NOT_EXIST", "ERROR_WHEN_SCHEMA_NOT_EXIST", "IGNORE"}
    )
    private String schemaSaveMode;

    @StField(
            tag = "表结构配置",
            order = 3,
            required = false,
            enName = "data_save_mode",
            cnName = "数据保存模式",
            defaultValue = "APPEND_DATA",
            description = "在启动同步任务之前，针对目标侧已存在的数据选择不同的处理方案",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"DROP_DATA", "APPEND_DATA", "CUSTOM_PROCESSING", "ERROR_WHEN_DATA_EXISTS"}
    )
    private String dataSaveMode;

    // 事务配置
    @StField(
            tag = "事务配置",
            order = 1,
            required = false,
            enName = "auto_commit",
            cnName = "启用自动事务提交",
            defaultValue = "true",
            description = "默认情况下启用自动事务提交",
            placeHolder = "启用自动事务提交",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"false", "true"}
    )
    private Integer auto_commit;

    @StField(
            tag = "事务配置",
            order = 2,
            required = false,
            enName = "max_commit_attempts",
            cnName = "事务提交失败的重试次数",
            defaultValue = "3",
            description = "事务提交失败的重试次数",
            placeHolder = "事务提交失败的重试次数",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer max_commit_attempts;

    @StField(
            tag = "事务配置",
            order = 3,
            required = false,
            enName = "transaction_timeout_sec",
            cnName = "事务打开后的超时",
            defaultValue = "-1",
            description = "事务打开后的超时，默认值为-1（永不超时）. 请注意，设置超时可能会影响精确一次语义",
            placeHolder = "事务打开后的超时，默认值为-1（永不超时）",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer transaction_timeout_sec;

    @StField(
            tag = "事务配置",
            order = 4,
            required = false,
            enName = "is_exactly_once",
            cnName = "精确一次语义",
            defaultValue = "false",
            description = "是否启用精确一次语义，使用XA事务保证",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"false", "true"}
    )
    private Boolean isExactlyOnce;

    @StField(
            tag = "其他配置",
            order = 1,
            required = false,
            enName = "xa_data_source_class_name",
            cnName = "XA数据源类名",
            defaultValue = "com.ibm.db2.jcc.DB2XADataSource",
            description = "DB2 XA数据源类名，用于精确一次语义",
            placeHolder = "com.ibm.db2.jcc.DB2XADataSource",
            formType = StField.FormType.TEXT
    )
    private String xaDataSourceClassName;

    @StField(
            tag = "其他配置",
            order = 2,
            required = false,
            enName = "properties",
            cnName = "附加连接配置参数",
            description = "附加连接配置参数，当属性和URL具有相同的参数时，优先级由驱动程序的特定实现决定. 例如，在MySQL中，属性优先于URL.",
            placeHolder = "附加连接配置参数",
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
        config.put("driver", "com.ibm.db2.jdbc.app.DB2Driver");
        putIfNotEmpty(config, "user", connectionConfig.getString("username"));
        putIfNotEmpty(config, "password", connectionConfig.getString("password"));
        putIfNotEmpty(config, "query", connectionConfig.getString("query"));
        putIfNotEmpty(config, "database", connectionConfig.getString("database"));
        putIfNotEmpty(config, "table", connectionConfig.getString("table"));
        putIfNotEmpty(config, "primary_keys", connectionConfig.getString("primary_keys"));
        putIfNotEmpty(config, "connection_check_timeout_sec", connectionConfig.getString("connection_check_timeout_sec"));
        putIfNotEmpty(config, "max_retries", connectionConfig.getString("max_retries"));
        putIfNotEmpty(config, "batch_size", connectionConfig.getString("batch_size"));
        putIfNotEmpty(config, "is_exactly_once", connectionConfig.getBoolean("is_exactly_once"));
        putIfNotEmpty(config, "generate_sink_sql", connectionConfig.getBoolean("generate_sink_sql"));
        putIfNotEmpty(config, "xa_data_source_class_name", connectionConfig.getString("xa_data_source_class_name"));
        putIfNotEmpty(config, "max_commit_attempts", connectionConfig.getString("max_commit_attempts"));
        putIfNotEmpty(config, "transaction_timeout_sec", connectionConfig.getString("transaction_timeout_sec"));
        putIfNotEmpty(config, "auto_commit", connectionConfig.getBoolean("auto_commit"));
        putIfNotEmpty(config, "properties", connectionConfig.getString("properties"));
        return config;
    }
}
