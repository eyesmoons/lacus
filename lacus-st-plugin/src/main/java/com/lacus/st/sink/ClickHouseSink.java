package com.lacus.st.sink;

import com.google.auto.service.AutoService;
import com.lacus.st.abstracts.AbstractStSink;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.annotation.StField;
import com.lacus.st.annotation.StTag;
import com.lacus.st.annotation.StTag.TagDefinition;
import com.lacus.st.interfaces.StComponentInterface;
import lombok.extern.slf4j.Slf4j;

/**
 * ClickHouse Sink组件
 * ClickHouse数据库输出组件
 * 
 * @author lacus
 */
@Slf4j
@StComponent(
        type = StComponent.ComponentType.SINK,
        name = "clickhouse_sink",
        displayName = "ClickHouse数据库输出",
        description = "用于将数据写入ClickHouse，支持精准一次、CDC和多表写入",
        version = "2.0.0",
        author = "lacus"
)
@StTag({
        @TagDefinition(name = "连接配置", displayName = "连接配置", order = 1, description = "ClickHouse连接相关配置"),
        @TagDefinition(name = "性能配置", displayName = "性能配置", order = 2, description = "性能优化相关配置"),
        @TagDefinition(name = "CDC配置", displayName = "CDC配置", order = 3, description = "CDC相关配置"),
        @TagDefinition(name = "表管理配置", displayName = "表管理配置", order = 4, description = "表结构和数据管理配置")
})
@AutoService(StComponentInterface.class)
public class ClickHouseSink extends AbstractStSink {

    // 连接配置
    @StField(
            tag = "连接配置",
            order = 1,
            required = true,
            enName = "host",
            cnName = "主机地址",
            description = "ClickHouse集群地址，格式：host:port，支持多个hosts配置，用逗号分隔",
            placeHolder = "localhost:8123",
            formType = StField.FormType.TEXT
    )
    private String host;

    @StField(
            tag = "连接配置",
            order = 2,
            required = true,
            enName = "database",
            cnName = "数据库名",
            description = "ClickHouse数据库名称",
            placeHolder = "default",
            formType = StField.FormType.TEXT
    )
    private String database;

    @StField(
            tag = "连接配置",
            order = 3,
            required = true,
            enName = "table",
            cnName = "表名",
            description = "目标表名，支持变量：${table_name}、${schema_name}",
            placeHolder = "test_table",
            formType = StField.FormType.TEXT
    )
    private String table;

    @StField(
            tag = "连接配置",
            order = 4,
            required = true,
            enName = "username",
            cnName = "用户名",
            description = "ClickHouse用户账号",
            placeHolder = "default",
            formType = StField.FormType.TEXT
    )
    private String username;

    @StField(
            tag = "连接配置",
            order = 5,
            required = true,
            enName = "password",
            cnName = "密码",
            description = "ClickHouse用户密码",
            placeHolder = "请输入密码",
            formType = StField.FormType.PASSWORD
    )
    private String password;

    // 性能配置
    @StField(
            tag = "性能配置",
            order = 6,
            required = false,
            enName = "bulk_size",
            cnName = "批量写入大小",
            defaultValue = "20000",
            description = "每次通过clickhouse-jdbc写入的行数",
            placeHolder = "20000",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer bulkSize;

    @StField(
            tag = "性能配置",
            order = 7,
            required = false,
            enName = "split_mode",
            cnName = "分片模式",
            defaultValue = "false",
            description = "仅支持Distributed引擎的表，将在seatunnel中拆分分布式表数据",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean splitMode;

    @StField(
            tag = "性能配置",
            order = 8,
            required = false,
            enName = "sharding_key",
            cnName = "分片键",
            description = "使用split_mode时，指定分片算法的字段",
            placeHolder = "user_id",
            formType = StField.FormType.TEXT
    )
    private String shardingKey;

    // CDC配置
    @StField(
            tag = "CDC配置",
            order = 9,
            required = false,
            enName = "primary_key",
            cnName = "主键",
            description = "标记ClickHouse表中的主键列，用于执行INSERT/UPDATE/DELETE",
            placeHolder = "id",
            formType = StField.FormType.TEXT
    )
    private String primaryKey;

    @StField(
            tag = "CDC配置",
            order = 10,
            required = false,
            enName = "support_upsert",
            cnName = "支持更新插入",
            defaultValue = "false",
            description = "支持按查询主键更新插入行",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean supportUpsert;

    @StField(
            tag = "CDC配置",
            order = 11,
            required = false,
            enName = "allow_experimental_lightweight_delete",
            cnName = "允许轻量级删除",
            defaultValue = "false",
            description = "允许基于MergeTree表引擎实验性轻量级删除",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean allowExperimentalLightweightDelete;

    // 表管理配置
    @StField(
            tag = "表管理配置",
            order = 12,
            required = false,
            enName = "schema_save_mode",
            cnName = "表结构保存模式",
            defaultValue = "CREATE_SCHEMA_WHEN_NOT_EXIST",
            description = "针对目标侧已有的表结构选择不同的处理方案",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"RECREATE_SCHEMA", "CREATE_SCHEMA_WHEN_NOT_EXIST", "ERROR_WHEN_SCHEMA_NOT_EXIST", "IGNORE"}
    )
    private String schemaSaveMode;

    @StField(
            tag = "表管理配置",
            order = 13,
            required = false,
            enName = "data_save_mode",
            cnName = "数据保存模式",
            defaultValue = "APPEND_DATA",
            description = "针对目标侧已存在的数据选择不同的处理方案",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"DROP_DATA", "APPEND_DATA", "CUSTOM_PROCESSING", "ERROR_WHEN_DATA_EXISTS"}
    )
    private String dataSaveMode;

    @StField(
            tag = "表管理配置",
            order = 14,
            required = false,
            enName = "custom_sql",
            cnName = "自定义SQL",
            description = "当data_save_mode为CUSTOM_PROCESSING时，执行的自定义SQL",
            placeHolder = "TRUNCATE TABLE test_table",
            formType = StField.FormType.TEXT_AREA
    )
    private String customSql;

    @StField(
            tag = "表管理配置",
            order = 15,
            required = false,
            enName = "save_mode_create_template",
            cnName = "建表模板",
            description = "自动创建ClickHouse表的SQL模板，支持占位符",
            placeHolder = "CREATE TABLE IF NOT EXISTS `${database}`.`${table}` (${rowtype_fields}) ENGINE = MergeTree() ORDER BY (${rowtype_primary_key})",
            formType = StField.FormType.TEXT_AREA
    )
    private String saveModeCreateTemplate;

    // ClickHouse客户端配置
    @StField(
            tag = "客户端配置",
            order = 16,
            required = false,
            enName = "max_rows_to_read",
            cnName = "最大读取行数",
            description = "ClickHouse客户端配置：最大读取行数",
            placeHolder = "100000",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer maxRowsToRead;

    @StField(
            tag = "客户端配置",
            order = 17,
            required = false,
            enName = "read_overflow_mode",
            cnName = "读取溢出模式",
            defaultValue = "throw",
            description = "ClickHouse客户端配置：读取溢出处理模式",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"throw", "break", "any"}
    )
    private String readOverflowMode;

    @Override
    protected boolean doCheckConnection() {
        try {
            // 验证必填字段
            if (host == null || host.trim().isEmpty()) {
                log.error("ClickHouse Sink配置错误：host不能为空");
                return false;
            }

            if (database == null || database.trim().isEmpty()) {
                log.error("ClickHouse Sink配置错误：database不能为空");
                return false;
            }

            if (table == null || table.trim().isEmpty()) {
                log.error("ClickHouse Sink配置错误：table不能为空");
                return false;
            }

            if (username == null || username.trim().isEmpty()) {
                log.error("ClickHouse Sink配置错误：username不能为空");
                return false;
            }

            // 验证host格式
            String[] hosts = host.split(",");
            for (String hostStr : hosts) {
                String trimmedHost = hostStr.trim();
                if (!trimmedHost.contains(":")) {
                    log.error("ClickHouse Sink配置错误：host格式不正确，应为host:port格式：{}", trimmedHost);
                    return false;
                }
            }

            // 验证split_mode和sharding_key的关系
            boolean splitModeEnabled = splitMode != null && splitMode;
            if (splitModeEnabled && (shardingKey == null || shardingKey.trim().isEmpty())) {
                log.warn("ClickHouse Sink警告：启用split_mode时建议配置sharding_key");
            }

            // 验证CDC相关配置
            boolean upsertEnabled = supportUpsert != null && supportUpsert;
            if (upsertEnabled && (primaryKey == null || primaryKey.trim().isEmpty())) {
                log.error("ClickHouse Sink配置错误：启用support_upsert时，primary_key不能为空");
                return false;
            }

            // 验证批量大小
            int bulkSizeValue = bulkSize != null ? bulkSize : 20000;
            if (bulkSizeValue <= 0) {
                log.error("ClickHouse Sink配置错误：bulk_size必须大于0");
                return false;
            }

            log.info("ClickHouse Sink连接检查成功，host: {}, database: {}, table: {}", host, database, table);
            return true;
        } catch (Exception e) {
            log.error("ClickHouse Sink连接检查失败", e);
            return false;
        }
    }

    // Getter and Setter methods
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getBulkSize() {
        return bulkSize != null ? bulkSize : 20000;
    }

    public void setBulkSize(Integer bulkSize) {
        this.bulkSize = bulkSize;
    }

    public Boolean getSplitMode() {
        return splitMode != null ? splitMode : false;
    }

    public void setSplitMode(Boolean splitMode) {
        this.splitMode = splitMode;
    }

    public String getShardingKey() {
        return shardingKey;
    }

    public void setShardingKey(String shardingKey) {
        this.shardingKey = shardingKey;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Boolean getSupportUpsert() {
        return supportUpsert != null ? supportUpsert : false;
    }

    public void setSupportUpsert(Boolean supportUpsert) {
        this.supportUpsert = supportUpsert;
    }

    public Boolean getAllowExperimentalLightweightDelete() {
        return allowExperimentalLightweightDelete != null ? allowExperimentalLightweightDelete : false;
    }

    public void setAllowExperimentalLightweightDelete(Boolean allowExperimentalLightweightDelete) {
        this.allowExperimentalLightweightDelete = allowExperimentalLightweightDelete;
    }

    public String getSchemaSaveMode() {
        return schemaSaveMode != null ? schemaSaveMode : "CREATE_SCHEMA_WHEN_NOT_EXIST";
    }

    public void setSchemaSaveMode(String schemaSaveMode) {
        this.schemaSaveMode = schemaSaveMode;
    }

    public String getDataSaveMode() {
        return dataSaveMode != null ? dataSaveMode : "APPEND_DATA";
    }

    public void setDataSaveMode(String dataSaveMode) {
        this.dataSaveMode = dataSaveMode;
    }

    public String getCustomSql() {
        return customSql;
    }

    public void setCustomSql(String customSql) {
        this.customSql = customSql;
    }

    public String getSaveModeCreateTemplate() {
        return saveModeCreateTemplate;
    }

    public void setSaveModeCreateTemplate(String saveModeCreateTemplate) {
        this.saveModeCreateTemplate = saveModeCreateTemplate;
    }

    public Integer getMaxRowsToRead() {
        return maxRowsToRead;
    }

    public void setMaxRowsToRead(Integer maxRowsToRead) {
        this.maxRowsToRead = maxRowsToRead;
    }

    public String getReadOverflowMode() {
        return readOverflowMode != null ? readOverflowMode : "throw";
    }

    public void setReadOverflowMode(String readOverflowMode) {
        this.readOverflowMode = readOverflowMode;
    }
}