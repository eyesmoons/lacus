package com.lacus.st.sink;

import com.google.auto.service.AutoService;
import com.lacus.st.abstracts.AbstractStSink;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.annotation.StField;
import com.lacus.st.annotation.StTag;
import com.lacus.st.annotation.StTag.TagDefinition;
import com.lacus.st.interfaces.StComponentInterface;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * DB2 Sink组件
 * DB2数据库输出组件
 * 
 * @author lacus
 */
@Slf4j
@StComponent(
        type = StComponent.ComponentType.SINK,
        name = "db2_sink",
        displayName = "DB2数据库输出",
        description = "通过JDBC写入DB2数据库，支持批处理模式和流模式，支持精确一次语义",
        version = "2.0.0",
        author = "lacus"
)
@StTag({
        @TagDefinition(name = "连接配置", displayName = "连接配置", order = 1, description = "数据库连接相关配置"),
        @TagDefinition(name = "数据配置", displayName = "数据配置", order = 2, description = "数据操作相关配置"),
        @TagDefinition(name = "性能配置", displayName = "性能配置", order = 3, description = "性能优化相关配置"),
        @TagDefinition(name = "DB2配置", displayName = "DB2配置", order = 4, description = "DB2特有配置"),
        @TagDefinition(name = "事务配置", displayName = "事务配置", order = 5, description = "事务处理相关配置")
})
@AutoService(StComponentInterface.class)
public class Db2Sink extends AbstractStSink {

    // 连接配置
    @StField(
            tag = "连接配置",
            order = 1,
            required = true,
            enName = "url",
            cnName = "JDBC连接URL",
            description = "DB2数据库连接URL",
            placeHolder = "jdbc:db2://localhost:50000/testdb",
            formType = StField.FormType.TEXT
    )
    private String url;

    @StField(
            tag = "连接配置",
            order = 2,
            required = true,
            enName = "driver",
            cnName = "驱动类名",
            defaultValue = "com.ibm.db2.jcc.DB2Driver",
            description = "DB2 JDBC驱动类名",
            placeHolder = "com.ibm.db2.jcc.DB2Driver",
            formType = StField.FormType.TEXT
    )
    private String driver;

    @StField(
            tag = "连接配置",
            order = 3,
            required = true,
            enName = "user",
            cnName = "用户名",
            description = "DB2数据库用户名",
            placeHolder = "db2inst1",
            formType = StField.FormType.TEXT
    )
    private String user;

    @StField(
            tag = "连接配置",
            order = 4,
            required = true,
            enName = "password",
            cnName = "密码",
            description = "DB2数据库密码",
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
            description = "使用SQL语句将上游输入数据写入DB2",
            placeHolder = "INSERT INTO test_table(name, age) VALUES(?, ?)",
            formType = StField.FormType.TEXT_AREA
    )
    private String query;

    @StField(
            tag = "数据配置",
            order = 6,
            required = false,
            enName = "database",
            cnName = "数据库名",
            description = "目标DB2数据库名称",
            placeHolder = "TESTDB",
            formType = StField.FormType.TEXT
    )
    private String database;

    @StField(
            tag = "数据配置",
            order = 7,
            required = false,
            enName = "table",
            cnName = "表名",
            description = "目标DB2表名称，格式：schema.table",
            placeHolder = "DB2INST1.TEST_TABLE",
            formType = StField.FormType.TEXT
    )
    private String table;

    @StField(
            tag = "数据配置",
            order = 8,
            required = false,
            enName = "primary_keys",
            cnName = "主键字段",
            description = "主键字段列表，用逗号分隔",
            placeHolder = "ID,NAME",
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

    // DB2特有配置
    @StField(
            tag = "DB2配置",
            order = 11,
            required = false,
            enName = "generate_sink_sql",
            cnName = "自动生成SQL",
            defaultValue = "false",
            description = "根据要写入的DB2表结构生成SQL语句",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean generateSinkSql;

    @StField(
            tag = "DB2配置",
            order = 12,
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
            tag = "DB2配置",
            order = 13,
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
            tag = "DB2配置",
            order = 14,
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
            order = 15,
            required = false,
            enName = "is_exactly_once",
            cnName = "精确一次语义",
            defaultValue = "false",
            description = "是否启用精确一次语义，使用XA事务保证",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean isExactlyOnce;

    @StField(
            tag = "事务配置",
            order = 16,
            required = false,
            enName = "xa_data_source_class_name",
            cnName = "XA数据源类名",
            defaultValue = "com.ibm.db2.jcc.DB2XADataSource",
            description = "DB2 XA数据源类名，用于精确一次语义",
            placeHolder = "com.ibm.db2.jcc.DB2XADataSource",
            formType = StField.FormType.TEXT
    )
    private String xaDataSourceClassName;

    @Override
    protected boolean doCheckConnection() {
        try {
            // 验证必填字段
            if (url == null || url.trim().isEmpty()) {
                log.error("DB2 Sink配置错误：url不能为空");
                return false;
            }

            if (user == null || user.trim().isEmpty()) {
                log.error("DB2 Sink配置错误：user不能为空");
                return false;
            }

            // 验证驱动类
            String driverClass = driver != null ? driver.trim() : "com.ibm.db2.jcc.DB2Driver";
            try {
                Class.forName(driverClass);
            } catch (ClassNotFoundException e) {
                log.error("DB2 Sink配置错误：找不到DB2驱动类 {}", driverClass, e);
                return false;
            }

            // 验证SQL查询或database+table配置
            boolean hasQuery = query != null && !query.trim().isEmpty();
            boolean hasDbTable = (database != null && !database.trim().isEmpty()) && 
                                (table != null && !table.trim().isEmpty());
            
            if (!hasQuery && !hasDbTable) {
                log.error("DB2 Sink配置错误：必须配置query查询语句或者database+table组合");
                return false;
            }

            // 验证URL格式
            if (!url.toLowerCase().contains("db2")) {
                log.warn("DB2 Sink警告：URL中未包含db2关键字，请确认URL格式正确");
            }

            // 测试DB2连接
            try (Connection connection = DriverManager.getConnection(url.trim(), user.trim(), password)) {
                if (connection != null && !connection.isClosed()) {
                    log.info("DB2 Sink数据库连接测试成功");
                    return true;
                } else {
                    log.error("DB2 Sink数据库连接失败：连接为空或已关闭");
                    return false;
                }
            } catch (SQLException e) {
                log.error("DB2 Sink数据库连接测试失败", e);
                return false;
            }

        } catch (Exception e) {
            log.error("DB2 Sink连接检查失败", e);
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
        return driver != null ? driver : "com.ibm.db2.jcc.DB2Driver";
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

    public Boolean getGenerateSinkSql() {
        return generateSinkSql != null ? generateSinkSql : false;
    }

    public void setGenerateSinkSql(Boolean generateSinkSql) {
        this.generateSinkSql = generateSinkSql;
    }

    public String getFieldIde() {
        return fieldIde != null ? fieldIde : "UPPERCASE";
    }

    public void setFieldIde(String fieldIde) {
        this.fieldIde = fieldIde;
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

    public Boolean getIsExactlyOnce() {
        return isExactlyOnce != null ? isExactlyOnce : false;
    }

    public void setIsExactlyOnce(Boolean isExactlyOnce) {
        this.isExactlyOnce = isExactlyOnce;
    }

    public String getXaDataSourceClassName() {
        return xaDataSourceClassName != null ? xaDataSourceClassName : "com.ibm.db2.jcc.DB2XADataSource";
    }

    public void setXaDataSourceClassName(String xaDataSourceClassName) {
        this.xaDataSourceClassName = xaDataSourceClassName;
    }
}