package com.lacus.st.sink;

import com.google.auto.service.AutoService;
import com.lacus.st.abstracts.AbstractStSink;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.annotation.StField;
import com.lacus.st.interfaces.StComponentInterface;
import com.lacus.st.annotation.StTag;
import com.lacus.st.annotation.StTag.TagDefinition;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * JDBC Sink组件
 * 通用JDBC数据库输出组件，支持多种数据库
 * 
 * @author lacus
 */
@Slf4j
@StComponent(
        type = StComponent.ComponentType.SINK,
        name = "jdbc_sink",
        displayName = "JDBC数据库输出",
        description = "通过JDBC写入数据，支持批处理模式和流模式，支持并发写入，支持精确一次语义",
        version = "2.0.0",
        author = "lacus"
)
@StTag({
        @TagDefinition(name = "连接配置", displayName = "连接配置", order = 1, description = "数据库连接相关配置"),
        @TagDefinition(name = "数据配置", displayName = "数据配置", order = 2, description = "数据操作相关配置"),
        @TagDefinition(name = "性能配置", displayName = "性能配置", order = 3, description = "性能优化相关配置"),
        @TagDefinition(name = "事务配置", displayName = "事务配置", order = 4, description = "事务处理相关配置")
})

@AutoService(StComponentInterface.class)
public class JdbcSink extends AbstractStSink {

    // 连接配置
    @StField(
            tag = "连接配置",
            order = 1,
            required = true,
            enName = "url",
            cnName = "JDBC连接URL",
            description = "JDBC连接的URL，如：jdbc:mysql://localhost:3306/test",
            placeHolder = "jdbc:mysql://localhost:3306/test",
            formType = StField.FormType.TEXT
    )
    private String url;

    @StField(
            tag = "连接配置",
            order = 2,
            required = true,
            enName = "driver",
            cnName = "驱动类名",
            description = "用于连接远程数据源的JDBC类名",
            placeHolder = "com.mysql.cj.jdbc.Driver",
            formType = StField.FormType.TEXT
    )
    private String driver;

    @StField(
            tag = "连接配置",
            order = 3,
            required = false,
            enName = "user",
            cnName = "用户名",
            description = "数据库用户名",
            placeHolder = "root",
            formType = StField.FormType.TEXT
    )
    private String user;

    @StField(
            tag = "连接配置",
            order = 4,
            required = false,
            enName = "password",
            cnName = "密码",
            description = "数据库密码",
            placeHolder = "请输入密码",
            formType = StField.FormType.PASSWORD
    )
    private String password;

    // 数据配置
    @StField(
            tag = "数据配置",
            order = 5,
            required = false,
            enName = "query",
            cnName = "SQL查询语句",
            description = "使用SQL语句将上游输入数据写入数据库，如：INSERT INTO table VALUES(?,?)",
            placeHolder = "INSERT INTO table(name, age) VALUES(?, ?)",
            formType = StField.FormType.TEXT_AREA
    )
    private String query;

    @StField(
            tag = "数据配置",
            order = 6,
            required = false,
            enName = "database",
            cnName = "数据库名",
            description = "目标数据库名称，与table配合自动生成SQL",
            placeHolder = "test_db",
            formType = StField.FormType.TEXT
    )
    private String database;

    @StField(
            tag = "数据配置",
            order = 7,
            required = false,
            enName = "table",
            cnName = "表名",
            description = "目标表名称，与database配合自动生成SQL",
            placeHolder = "test_table",
            formType = StField.FormType.TEXT
    )
    private String table;

    @StField(
            tag = "数据配置",
            order = 8,
            required = false,
            enName = "primary_keys",
            cnName = "主键字段",
            description = "主键字段列表，用逗号分隔，用于INSERT/UPDATE/DELETE操作",
            placeHolder = "id,name",
            formType = StField.FormType.TEXT
    )
    private String primaryKeys;

    // 性能配置
    @StField(
            tag = "性能配置",
            order = 9,
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
            order = 10,
            required = false,
            enName = "connection_check_timeout_sec",
            cnName = "连接检查超时时间(秒)",
            defaultValue = "30",
            description = "验证数据库连接所使用的操作完成的等待时间（秒）",
            placeHolder = "30",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer connectionCheckTimeoutSec;

    @StField(
            tag = "性能配置",
            order = 11,
            required = false,
            enName = "max_retries",
            cnName = "最大重试次数",
            defaultValue = "0",
            description = "提交失败的重试次数",
            placeHolder = "0",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer maxRetries;

    // 高级配置
    @StField(
            tag = "高级配置",
            order = 12,
            required = false,
            enName = "generate_sink_sql",
            cnName = "自动生成SQL",
            defaultValue = "false",
            description = "根据要写入的数据库表结构生成SQL语句",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean generateSinkSql;

    @StField(
            tag = "高级配置",
            order = 13,
            required = false,
            enName = "enable_upsert",
            cnName = "启用UPSERT",
            defaultValue = "true",
            description = "通过主键启用更新插入，如果任务只有INSERT，设置为false可以加快速度",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean enableUpsert;

    @StField(
            tag = "高级配置",
            order = 14,
            required = false,
            enName = "auto_commit",
            cnName = "自动提交",
            defaultValue = "true",
            description = "是否启用自动事务提交",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean autoCommit;

    @StField(
            tag = "高级配置",
            order = 15,
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
            tag = "高级配置",
            order = 16,
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

    @Override
    protected boolean doCheckConnection() {
        try {
            // 验证必填字段
            if (url == null || url.trim().isEmpty()) {
                log.error("JDBC Sink配置错误：url不能为空");
                return false;
            }

            if (driver == null || driver.trim().isEmpty()) {
                log.error("JDBC Sink配置错误：driver不能为空");
                return false;
            }

            // 验证SQL查询或database+table配置
            boolean hasQuery = query != null && !query.trim().isEmpty();
            boolean hasDbTable = (database != null && !database.trim().isEmpty()) && 
                                (table != null && !table.trim().isEmpty());
            
            if (!hasQuery && !hasDbTable) {
                log.error("JDBC Sink配置错误：必须配置query查询语句或者database+table组合");
                return false;
            }

            // 验证驱动类是否存在
            try {
                Class.forName(driver.trim());
            } catch (ClassNotFoundException e) {
                log.error("JDBC Sink配置错误：找不到驱动类 {}", driver, e);
                return false;
            }

            // 测试数据库连接
            try (Connection connection = DriverManager.getConnection(url.trim(), user, password)) {
                if (connection != null && !connection.isClosed()) {
                    log.info("JDBC Sink数据库连接测试成功");
                    return true;
                } else {
                    log.error("JDBC Sink数据库连接失败：连接为空或已关闭");
                    return false;
                }
            } catch (SQLException e) {
                log.error("JDBC Sink数据库连接测试失败", e);
                return false;
            }

        } catch (Exception e) {
            log.error("JDBC Sink连接检查失败", e);
            return false;
        }
    }

    /**
     * 获取主键字段列表
     */
    public List<String> getPrimaryKeysList() {
        if (primaryKeys == null || primaryKeys.trim().isEmpty()) {
            return null;
        }
        String[] keys = primaryKeys.split(",");
        return java.util.Arrays.stream(keys)
                .map(String::trim)
                .filter(key -> !key.isEmpty())
                .collect(java.util.stream.Collectors.toList());
    }

    // Getter and Setter methods
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
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

    public String getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(String primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public Integer getBatchSize() {
        return batchSize != null ? batchSize : 1000;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public Integer getConnectionCheckTimeoutSec() {
        return connectionCheckTimeoutSec != null ? connectionCheckTimeoutSec : 30;
    }

    public void setConnectionCheckTimeoutSec(Integer connectionCheckTimeoutSec) {
        this.connectionCheckTimeoutSec = connectionCheckTimeoutSec;
    }

    public Integer getMaxRetries() {
        return maxRetries != null ? maxRetries : 0;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public Boolean getGenerateSinkSql() {
        return generateSinkSql != null ? generateSinkSql : false;
    }

    public void setGenerateSinkSql(Boolean generateSinkSql) {
        this.generateSinkSql = generateSinkSql;
    }

    public Boolean getEnableUpsert() {
        return enableUpsert != null ? enableUpsert : true;
    }

    public void setEnableUpsert(Boolean enableUpsert) {
        this.enableUpsert = enableUpsert;
    }

    public Boolean getAutoCommit() {
        return autoCommit != null ? autoCommit : true;
    }

    public void setAutoCommit(Boolean autoCommit) {
        this.autoCommit = autoCommit;
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
}