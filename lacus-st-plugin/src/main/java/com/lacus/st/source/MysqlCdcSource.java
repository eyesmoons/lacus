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
 * MySQL CDC数据源组件
 * 
 * @author lacus
 */
@Slf4j
@StComponent(
        type = StComponent.ComponentType.SOURCE,
        name = "mysql_cdc_source",
        displayName = "MySQL CDC数据源",
        description = "从MySQL数据库读取变更数据捕获(CDC)流，支持实时数据同步",
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
public class MysqlCdcSource extends AbstractStSource {

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
            enName = "database_list",
            cnName = "数据库列表",
            description = "需要监控的数据库列表，支持多个数据库",
            placeHolder = "db1,db2,db3",
            formType = StField.FormType.TEXT
    )
    private String databaseList;

    @StField(
            tag = "数据源配置",
            order = 3,
            required = true,
            enName = "table_list",
            cnName = "表列表",
            description = "需要监控的表列表，格式：database.table",
            placeHolder = "db1.table1,db1.table2",
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
            description = "MySQL服务器主机地址",
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
            defaultValue = "3306",
            description = "MySQL服务器端口号",
            placeHolder = "3306",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer port;

    @StField(
            tag = "连接配置",
            order = 6,
            required = true,
            enName = "username",
            cnName = "用户名",
            description = "数据库用户名，需要具有REPLICATION SLAVE和REPLICATION CLIENT权限",
            placeHolder = "root",
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

    // CDC配置
    @StField(
            tag = "CDC配置",
            order = 1,
            required = true,
            enName = "server_id",
            cnName = "服务器ID",
            description = "CDC连接器的唯一服务器ID，不能与现有MySQL集群中的任何服务器ID重复",
            placeHolder = "5400",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer serverId;

    @StField(
            tag = "CDC配置",
            order = 2,
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
            order = 3,
            required = false,
            enName = "connect_timeout",
            cnName = "连接超时",
            defaultValue = "30s",
            description = "连接器在尝试连接到MySQL数据库服务器后等待响应的最长时间",
            placeHolder = "30s",
            formType = StField.FormType.TEXT
    )
    private String connectTimeout;

    @StField(
            tag = "CDC配置",
            order = 4,
            required = false,
            enName = "connect_max_retries",
            cnName = "最大重试次数",
            defaultValue = "3",
            description = "连接失败或发生错误时的最大重试次数",
            placeHolder = "3",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer connectMaxRetries;

    // 快照配置
    @StField(
            tag = "快照配置",
            order = 5,
            required = false,
            enName = "snapshot_mode",
            cnName = "快照模式",
            defaultValue = "initial",
            description = "快照模式：initial(初始快照), when_needed(需要时), never(从不), schema_only(仅模式)",
            placeHolder = "initial",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"initial", "when_needed", "never", "schema_only"}
    )
    private String snapshotMode;

    @StField(
            tag = "快照配置",
            order = 6,
            required = false,
            enName = "snapshot_split_size",
            cnName = "快照分片大小",
            defaultValue = "8096",
            description = "表快照的分片大小，影响并行度",
            placeHolder = "8096",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer snapshotSplitSize;

    @StField(
            tag = "快照配置",
            order = 7,
            required = false,
            enName = "snapshot_fetch_size",
            cnName = "快照获取大小",
            defaultValue = "1024",
            description = "快照读取每次fetch的行数",
            placeHolder = "1024",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer snapshotFetchSize;

    // Binlog配置
    @StField(
            tag = "Binlog配置",
            order = 8,
            required = false,
            enName = "binlog_filename",
            cnName = "Binlog文件名",
            description = "开始读取的binlog文件名，为空则从当前位置开始",
            placeHolder = "mysql-bin.000001",
            formType = StField.FormType.TEXT
    )
    private String binlogFilename;

    @StField(
            tag = "Binlog配置",
            order = 9,
            required = false,
            enName = "binlog_position",
            cnName = "Binlog位置",
            description = "开始读取的binlog位置",
            placeHolder = "4",
            formType = StField.FormType.NUMBER
    )
    private Long binlogPosition;

    @StField(
            tag = "Binlog配置",
            order = 10,
            required = false,
            enName = "scan_startup_mode",
            cnName = "启动模式",
            defaultValue = "initial",
            description = "启动模式：initial(全量+增量), earliest(最早), latest(最新), specific-offset(指定位置), timestamp(时间戳)",
            placeHolder = "initial",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"initial", "earliest", "latest", "specific-offset", "timestamp"}
    )
    private String scanStartupMode;

    @StField(
            tag = "Binlog配置",
            order = 11,
            required = false,
            enName = "scan_startup_timestamp_millis",
            cnName = "启动时间戳",
            description = "当启动模式为timestamp时，指定开始读取的时间戳(毫秒)",
            placeHolder = "1640995200000",
            formType = StField.FormType.NUMBER
    )
    private Long scanStartupTimestampMillis;

    // 性能配置
    @StField(
            tag = "性能配置",
            order = 12,
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
            order = 13,
            required = false,
            enName = "debezium_properties",
            cnName = "Debezium属性",
            description = "额外的Debezium属性配置，格式：key1=value1;key2=value2",
            placeHolder = "decimal.handling.mode=string",
            formType = StField.FormType.TEXT_AREA
    )
    private String debeziumProperties;

    // 过滤配置
    @StField(
            tag = "过滤配置",
            order = 14,
            required = false,
            enName = "table_excludes",
            cnName = "排除表",
            description = "需要排除的表，格式：database.table，支持正则表达式",
            placeHolder = "test.temp_.*",
            formType = StField.FormType.TEXT_AREA
    )
    private String tableExcludes;

    @StField(
            tag = "过滤配置",
            order = 15,
            required = false,
            enName = "column_excludes",
            cnName = "排除列",
            description = "需要排除的列，格式：database.table.column",
            placeHolder = "test.users.password",
            formType = StField.FormType.TEXT_AREA
    )
    private String columnExcludes;

    private Connection connection;

    @Override
    protected boolean doCheckConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection.isValid(30);
            }

            // 构建连接URL
            String url = String.format("jdbc:mysql://%s:%d/", hostname, port);
            
            // 创建连接进行测试
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection testConnection = DriverManager.getConnection(url, username, password);
            
            // 验证是否开启了binlog
            boolean binlogEnabled = testConnection.createStatement()
                    .executeQuery("SHOW VARIABLES LIKE 'log_bin'")
                    .next();
            
            testConnection.close();
            
            if (!binlogEnabled) {
                log.warn("MySQL binlog未开启，CDC功能可能无法正常工作");
            }
            
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            log.error("MySQL CDC连接检查失败", e);
            return false;
        }
    }
}