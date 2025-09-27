package com.lacus.st.source;

import com.google.auto.service.AutoService;
import com.lacus.st.abstracts.AbstractStSource;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.annotation.StField;
import com.lacus.st.interfaces.StComponentInterface;
import com.lacus.st.annotation.StTag;
import com.lacus.st.annotation.StTag.TagDefinition;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Oracle CDC数据源组件
 * 
 * @author lacus
 */
@Slf4j
@StComponent(
        type = StComponent.ComponentType.SOURCE,
        name = "oracle_cdc_source",
        displayName = "Oracle CDC数据源",
        description = "从Oracle数据库读取变更数据捕获(CDC)流，支持实时数据同步",
        version = "2.0.0",
        author = "lacus"
)
@StTag({
        @TagDefinition(name = "数据源配置", displayName = "数据源配置", order = 1, description = "数据源选择和连接配置"),
        @TagDefinition(name = "查询配置", displayName = "查询配置", order = 2, description = "数据查询相关配置"),
        @TagDefinition(name = "性能配置", displayName = "性能配置", order = 3, description = "性能优化相关配置"),
        @TagDefinition(name = "其他配置", displayName = "其他配置", order = 4, description = "其他扩展配置")
})

@AutoService(StComponentInterface.class)
public class OracleCdcSource extends AbstractStSource {

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
            enName = "schema_list",
            cnName = "模式列表",
            description = "需要监控的Oracle模式列表",
            placeHolder = "SCOTT,HR,OE",
            formType = StField.FormType.TEXT
    )
    private String schemaList;

    @StField(
            tag = "数据源配置",
            order = 3,
            required = true,
            enName = "table_list",
            cnName = "表列表",
            description = "需要监控的表列表，格式：schema.table",
            placeHolder = "SCOTT.EMP,SCOTT.DEPT",
            formType = StField.FormType.TEXT_AREA
    )
    private String tableList;

    // 连接配置
    @StField(
            tag = "连接配置",
            order = 4,
            required = true,
            enName = "hostname",
            cnName = "主机地址",
            description = "Oracle服务器主机地址",
            placeHolder = "localhost",
            formType = StField.FormType.TEXT
    )
    private String hostname;

    @StField(
            tag = "连接配置",
            order = 5,
            required = true,
            enName = "port",
            cnName = "端口号",
            defaultValue = "1521",
            description = "Oracle监听器端口号",
            placeHolder = "1521",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer port;

    @StField(
            tag = "连接配置",
            order = 6,
            required = true,
            enName = "username",
            cnName = "用户名",
            description = "数据库用户名，需要具有LogMiner权限",
            placeHolder = "system",
            formType = StField.FormType.TEXT
    )
    private String username;

    @StField(
            tag = "连接配置",
            order = 7,
            required = true,
            enName = "password",
            cnName = "密码",
            placeHolder = "请输入数据库密码",
            formType = StField.FormType.PASSWORD
    )
    private String password;

    @StField(
            tag = "连接配置",
            order = 8,
            required = true,
            enName = "database_name",
            cnName = "数据库名",
            description = "Oracle数据库名称或服务名",
            placeHolder = "ORCL",
            formType = StField.FormType.TEXT
    )
    private String databaseName;

    // CDC配置
    @StField(
            tag = "CDC配置",
            order = 9,
            required = false,
            enName = "server_time_zone",
            cnName = "服务器时区",
            defaultValue = "UTC",
            description = "数据库服务器中的会话时区",
            placeHolder = "UTC",
            formType = StField.FormType.TEXT
    )
    private String serverTimeZone;

    @StField(
            tag = "CDC配置",
            order = 10,
            required = false,
            enName = "connect_timeout",
            cnName = "连接超时",
            defaultValue = "30s",
            description = "连接器在尝试连接到Oracle数据库服务器后等待响应的最长时间",
            placeHolder = "30s",
            formType = StField.FormType.TEXT
    )
    private String connectTimeout;

    @StField(
            tag = "CDC配置",
            order = 11,
            required = false,
            enName = "connection_pool_size",
            cnName = "连接池大小",
            defaultValue = "20",
            description = "连接池的最大连接数",
            placeHolder = "20",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer connectionPoolSize;

    // LogMiner配置
    @StField(
            tag = "LogMiner配置",
            order = 12,
            required = false,
            enName = "log_mining_strategy",
            cnName = "日志挖掘策略",
            defaultValue = "online_catalog",
            description = "LogMiner策略：online_catalog或catalog_in_redo",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"online_catalog", "catalog_in_redo"}
    )
    private String logMiningStrategy;

    @StField(
            tag = "LogMiner配置",
            order = 13,
            required = false,
            enName = "log_mining_batch_size_min",
            cnName = "最小批处理大小",
            defaultValue = "1000",
            description = "LogMiner批处理的最小大小",
            placeHolder = "1000",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer logMiningBatchSizeMin;

    @StField(
            tag = "LogMiner配置",
            order = 14,
            required = false,
            enName = "log_mining_batch_size_max",
            cnName = "最大批处理大小",
            defaultValue = "20000",
            description = "LogMiner批处理的最大大小",
            placeHolder = "20000",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer logMiningBatchSizeMax;

    @StField(
            tag = "LogMiner配置",
            order = 15,
            required = false,
            enName = "log_mining_sleep_time_min_ms",
            cnName = "最小休眠时间",
            defaultValue = "1000",
            description = "LogMiner最小休眠时间(毫秒)",
            placeHolder = "1000",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer logMiningSleepTimeMinMs;

    @StField(
            tag = "LogMiner配置",
            order = 16,
            required = false,
            enName = "log_mining_sleep_time_max_ms",
            cnName = "最大休眠时间",
            defaultValue = "3000",
            description = "LogMiner最大休眠时间(毫秒)",
            placeHolder = "3000",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer logMiningSleepTimeMaxMs;

    // 快照配置
    @StField(
            tag = "快照配置",
            order = 17,
            required = false,
            enName = "snapshot_mode",
            cnName = "快照模式",
            defaultValue = "initial",
            description = "快照模式：initial(初始快照), never(从不), schema_only(仅模式)",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"initial", "never", "schema_only"}
    )
    private String snapshotMode;

    @StField(
            tag = "快照配置",
            order = 18,
            required = false,
            enName = "snapshot_split_size",
            cnName = "快照分片大小",
            defaultValue = "8096",
            description = "表快照的分片大小",
            placeHolder = "8096",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer snapshotSplitSize;

    @StField(
            tag = "快照配置",
            order = 19,
            required = false,
            enName = "snapshot_fetch_size",
            cnName = "快照获取大小",
            defaultValue = "1024",
            description = "快照读取每次fetch的行数",
            placeHolder = "1024",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer snapshotFetchSize;

    // 启动配置
    @StField(
            tag = "启动配置",
            order = 20,
            required = false,
            enName = "scan_startup_mode",
            cnName = "启动模式",
            defaultValue = "initial",
            description = "启动模式：initial(全量+增量), latest(最新), specific-offset(指定位置)",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"initial", "latest", "specific-offset"}
    )
    private String scanStartupMode;

    @StField(
            tag = "启动配置",
            order = 21,
            required = false,
            enName = "scan_startup_specific_offset_file",
            cnName = "起始日志文件",
            description = "当启动模式为specific-offset时，指定开始读取的归档日志文件",
            placeHolder = "/path/to/archive/log/file",
            formType = StField.FormType.TEXT
    )
    private String scanStartupSpecificOffsetFile;

    @StField(
            tag = "启动配置",
            order = 22,
            required = false,
            enName = "scan_startup_specific_offset_scn",
            cnName = "起始SCN",
            description = "当启动模式为specific-offset时，指定开始读取的SCN号",
            placeHolder = "1000000",
            formType = StField.FormType.NUMBER
    )
    private Long scanStartupSpecificOffsetScn;

    // 性能配置
    @StField(
            tag = "性能配置",
            order = 23,
            required = false,
            enName = "heartbeat_interval",
            cnName = "心跳间隔",
            defaultValue = "30s",
            description = "连接器向源数据库发送心跳的时间间隔",
            placeHolder = "30s",
            formType = StField.FormType.TEXT
    )
    private String heartbeatInterval;

    @StField(
            tag = "性能配置",
            order = 24,
            required = false,
            enName = "debezium_properties",
            cnName = "Debezium属性",
            description = "额外的Debezium属性配置，格式：key1=value1;key2=value2",
            placeHolder = "decimal.handling.mode=string",
            formType = StField.FormType.TEXT_AREA
    )
    private String debeziumProperties;

    private Connection connection;

    @Override
    protected boolean doCheckConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection.isValid(30);
            }

            // 构建连接URL
            String url = String.format("jdbc:oracle:thin:@%s:%d:%s", hostname, port, databaseName);
            
            // 创建连接进行测试
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection testConnection = DriverManager.getConnection(url, username, password);
            
            // 验证是否开启了归档日志
            boolean archivelogEnabled = testConnection.createStatement()
                    .executeQuery("SELECT LOG_MODE FROM V$DATABASE")
                    .next();
            
            testConnection.close();
            
            if (!archivelogEnabled) {
                log.warn("Oracle归档日志未开启，CDC功能可能无法正常工作");
            }
            
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            log.error("Oracle CDC连接检查失败", e);
            return false;
        }
    }
}