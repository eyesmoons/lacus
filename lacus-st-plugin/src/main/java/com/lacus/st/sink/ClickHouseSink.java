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
import org.apache.commons.lang3.ObjectUtils;

/**
 * ClickHouse Sink组件
 * ClickHouse数据库输出组件
 *
 * @author lacus
 */
@Setter
@Slf4j
@StComponent(
        type = StComponent.ComponentType.SINK,
        name = "clickhouse_sink",
        displayName = "ClickHouse数据库输出",
        description = "用于将数据写入ClickHouse，支持精准一次、CDC和多表写入",
        version = "2.0.0",
        author = "lacus",
        connectorKey = "ClickHouse"
)
@StTag({
        @TagDefinition(name = "数据源配置", displayName = "数据源配置", order = 1, description = "ClickHouse数据源相关配置"),
        @TagDefinition(name = "性能配置", displayName = "性能配置", order = 2, description = "性能优化相关配置"),
        @TagDefinition(name = "数据配置", displayName = "数据配置", order = 3, description = "数据配置"),
        @TagDefinition(name = "表管理配置", displayName = "表管理配置", order = 4, description = "表结构和数据管理配置")
})
@AutoService(StComponentInterface.class)
public class ClickHouseSink extends AbstractStSink {

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

    // 性能配置
    @StField(
            tag = "性能配置",
            order = 1,
            required = false,
            enName = "bulk_size",
            cnName = "批量写入大小",
            defaultValue = "20000",
            description = "每次通过clickhouse-jdbc写入的行数",
            placeHolder = "20000",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer bulk_size;

    @StField(
            tag = "性能配置",
            order = 2,
            required = false,
            enName = "split_mode",
            cnName = "分片模式",
            defaultValue = "false",
            description = "仅支持Distributed引擎的表，将在seatunnel中拆分分布式表数据",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"false", "true"}
    )
    private Boolean split_mode;

    @StField(
            tag = "性能配置",
            order = 3,
            required = false,
            enName = "sharding_key",
            cnName = "分片键",
            description = "使用split_mode时，指定分片算法的字段",
            placeHolder = "user_id",
            formType = StField.FormType.TEXT
    )
    private String sharding_key;

    // CDC配置
    @StField(
            tag = "数据配置",
            order = 1,
            required = false,
            enName = "primary_key",
            cnName = "主键",
            description = "标记ClickHouse表中的主键列，用于执行INSERT/UPDATE/DELETE",
            placeHolder = "id",
            formType = StField.FormType.TEXT
    )
    private String primary_key;

    @StField(
            tag = "数据配置",
            order = 2,
            required = false,
            enName = "support_upsert",
            cnName = "支持更新插入",
            defaultValue = "false",
            description = "支持按查询主键更新插入行",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean support_upsert;

    @StField(
            tag = "数据配置",
            order = 3,
            required = false,
            enName = "allow_experimental_lightweight_delete",
            cnName = "允许轻量级删除",
            defaultValue = "false",
            description = "允许基于MergeTree表引擎实验性轻量级删除",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean allow_experimental_lightweight_delete;

    // 表管理配置
    @StField(
            tag = "表管理配置",
            order = 1,
            required = false,
            enName = "schema_save_mode",
            cnName = "表结构保存模式",
            defaultValue = "CREATE_SCHEMA_WHEN_NOT_EXIST",
            description = "针对目标侧已有的表结构选择不同的处理方案",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"RECREATE_SCHEMA", "CREATE_SCHEMA_WHEN_NOT_EXIST", "ERROR_WHEN_SCHEMA_NOT_EXIST", "IGNORE"}
    )
    private String schema_save_mode;

    @StField(
            tag = "表管理配置",
            order = 2,
            required = false,
            enName = "data_save_mode",
            cnName = "数据保存模式",
            defaultValue = "APPEND_DATA",
            description = "针对目标侧已存在的数据选择不同的处理方案",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"DROP_DATA", "APPEND_DATA", "CUSTOM_PROCESSING", "ERROR_WHEN_DATA_EXISTS"}
    )
    private String data_save_mode;

    @StField(
            tag = "表管理配置",
            order = 3,
            required = false,
            enName = "custom_sql",
            cnName = "自定义SQL",
            description = "当data_save_mode为CUSTOM_PROCESSING时，执行的自定义SQL",
            placeHolder = "TRUNCATE TABLE test_table",
            formType = StField.FormType.TEXT_AREA
    )
    private String custom_sql;

    @StField(
            tag = "表管理配置",
            order = 4,
            required = false,
            enName = "save_mode_create_template",
            cnName = "建表模板",
            description = "自动创建ClickHouse表的SQL模板，支持占位符",
            placeHolder = "CREATE TABLE IF NOT EXISTS `${database}`.`${table}` (${rowtype_fields}) ENGINE = MergeTree() ORDER BY (${rowtype_primary_key})",
            formType = StField.FormType.TEXT_AREA
    )
    private String save_mode_create_template;

    // ClickHouse客户端配置
    @StField(
            tag = "客户端配置",
            order = 1,
            required = false,
            enName = "clickhouse_config",
            cnName = "clickHouse客户端配置",
            description = "clickHouse客户端配置",
            formType = StField.FormType.TEXT_AREA
    )
    private Integer clickhouse_config;

    @Override
    protected boolean doCheckConnection() {
        return true;
    }

    @Override
    public JSONObject buildTaskConfig(JSONObject connectionConfig, Long datasourceId) {
        JSONObject config = new JSONObject();
        String host = connectionConfig.getString("host");
        Integer port = connectionConfig.getInteger("port");
        if (ObjectUtils.isNotEmpty(host) && ObjectUtils.isNotEmpty(port)) {
            config.put("host", host + ":" + port);
        }
        putIfNotEmpty(config, "user", connectionConfig.getString("username"));
        putIfNotEmpty(config, "password", connectionConfig.getString("password"));
        putIfNotEmpty(config, "database", connectionConfig.getString("database"));
        JSONObject outputModel = connectionConfig.getJSONObject("outputModel");
        putIfNotEmpty(config, "database", outputModel.getString("tableName"));
        putIfNotEmpty(config, "clickhouse.config", connectionConfig.getString("clickhouse_config"));
        putIfNotEmpty(config, "bulk_size", connectionConfig.getString("bulk_size"));
        putIfNotEmpty(config, "split_mode", connectionConfig.getString("split_mode"));
        putIfNotEmpty(config, "sharding_key", connectionConfig.getString("sharding_key"));
        putIfNotEmpty(config, "primary_key", connectionConfig.getString("primary_key"));
        putIfNotEmpty(config, "support_upsert", connectionConfig.getBoolean("support_upsert"));
        putIfNotEmpty(config, "allow_experimental_lightweight_delete", connectionConfig.getBoolean("allow_experimental_lightweight_delete"));
        putIfNotEmpty(config, "schema_save_mode", connectionConfig.getString("schema_save_mode"));
        putIfNotEmpty(config, "data_save_mode", connectionConfig.getString("data_save_mode"));
        putIfNotEmpty(config, "custom_sql", connectionConfig.getString("custom_sql"));
        putIfNotEmpty(config, "save_mode_create_template", connectionConfig.getString("save_mode_create_template"));
        return config;
    }
}
