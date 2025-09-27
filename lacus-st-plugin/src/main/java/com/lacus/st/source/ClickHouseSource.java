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
 * ClickHouse数据源组件
 * 
 * @author lacus
 */
@Slf4j
@StComponent(
        type = StComponent.ComponentType.SOURCE,
        name = "clickhouse_source",
        displayName = "ClickHouse数据源",
        description = "从ClickHouse数据库读取数据，支持高性能OLAP查询",
        version = "2.0.0",
        author = "lacus"
)
@StTag({
        @TagDefinition(name = "数据源配置", displayName = "数据源配置", order = 1, description = "数据源选择和连接配置"),
        @TagDefinition(name = "连接配置", displayName = "连接配置", order = 2, description = "数据库连接相关配置"),
        @TagDefinition(name = "查询配置", displayName = "查询配置", order = 3, description = "数据查询相关配置"),
        @TagDefinition(name = "性能配置", displayName = "性能配置", order = 4, description = "性能优化相关配置"),
        @TagDefinition(name = "ClickHouse配置", displayName = "ClickHouse配置", order = 5, description = "ClickHouse特有配置")
})

@AutoService(StComponentInterface.class)
public class ClickHouseSource extends AbstractStSource {

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

    // 连接配置
    @StField(
            tag = "连接配置",
            order = 4,
            required = true,
            enName = "host",
            cnName = "主机地址",
            description = "ClickHouse服务器主机地址",
            placeHolder = "localhost",
            formType = StField.FormType.TEXT
    )
    private String host;

    @StField(
            tag = "连接配置",
            order = 5,
            required = true,
            enName = "port",
            cnName = "端口号",
            defaultValue = "8123",
            description = "ClickHouse HTTP端口号",
            placeHolder = "8123",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer port;

    @StField(
            tag = "连接配置",
            order = 6,
            required = true,
            enName = "username",
            cnName = "用户名",
            defaultValue = "default",
            description = "ClickHouse数据库用户名",
            placeHolder = "default",
            formType = StField.FormType.TEXT
    )
    private String username;

    @StField(
            tag = "连接配置",
            order = 7,
            required = false,
            enName = "password",
            cnName = "密码",
            placeHolder = "请输入数据库密码",
            formType = StField.FormType.PASSWORD
    )
    private String password;

    // 查询配置
    @StField(
            tag = "查询配置",
            order = 8,
            required = false,
            enName = "sql",
            cnName = "查询SQL",
            description = "自定义SQL查询语句",
            placeHolder = "SELECT * FROM table_name WHERE condition",
            formType = StField.FormType.TEXT_AREA
    )
    private String sql;

    @StField(
            tag = "查询配置",
            order = 9,
            required = false,
            enName = "where_condition",
            cnName = "WHERE条件",
            description = "数据过滤条件，自动添加到查询中",
            placeHolder = "id > 1000 AND status = 'active'",
            formType = StField.FormType.TEXT_AREA
    )
    private String whereCondition;

    @StField(
            tag = "查询配置",
            order = 10,
            required = false,
            enName = "split_key",
            cnName = "分片键",
            description = "用于数据分片的列名，提高并行读取性能",
            placeHolder = "id",
            formType = StField.FormType.TEXT
    )
    private String splitKey;

    @StField(
            tag = "查询配置",
            order = 11,
            required = false,
            enName = "split_lower_bound",
            cnName = "分片下界",
            description = "分片键的最小值",
            placeHolder = "1",
            formType = StField.FormType.NUMBER
    )
    private Long splitLowerBound;

    @StField(
            tag = "查询配置",
            order = 12,
            required = false,
            enName = "split_upper_bound",
            cnName = "分片上界",
            description = "分片键的最大值",
            placeHolder = "100000",
            formType = StField.FormType.NUMBER
    )
    private Long splitUpperBound;

    @StField(
            tag = "查询配置",
            order = 13,
            required = false,
            enName = "num_splits",
            cnName = "分片数量",
            defaultValue = "1",
            description = "并行读取的分片数量",
            placeHolder = "4",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer numSplits;

    // 性能配置
    @StField(
            tag = "性能配置",
            order = 14,
            required = false,
            enName = "bulk_size",
            cnName = "批处理大小",
            defaultValue = "20000",
            description = "批量读取的记录数",
            placeHolder = "20000",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer bulkSize;

    @StField(
            tag = "性能配置",
            order = 15,
            required = false,
            enName = "connect_timeout_ms",
            cnName = "连接超时时间",
            defaultValue = "60000",
            description = "连接超时时间(毫秒)",
            placeHolder = "60000",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer connectTimeoutMs;

    @StField(
            tag = "性能配置",
            order = 16,
            required = false,
            enName = "socket_timeout_ms",
            cnName = "套接字超时时间",
            defaultValue = "60000",
            description = "套接字操作超时时间(毫秒)",
            placeHolder = "60000",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer socketTimeoutMs;

    // ClickHouse特殊配置
    @StField(
            tag = "ClickHouse配置",
            order = 17,
            required = false,
            enName = "clickhouse_config",
            cnName = "ClickHouse配置",
            description = "额外的ClickHouse配置参数，格式：key1=value1;key2=value2",
            placeHolder = "max_execution_time=3600;max_memory_usage=10000000000",
            formType = StField.FormType.TEXT_AREA
    )
    private String clickhouseConfig;

    @StField(
            tag = "ClickHouse配置",
            order = 18,
            required = false,
            enName = "cluster_name",
            cnName = "集群名称",
            description = "ClickHouse集群名称，用于分布式查询",
            placeHolder = "default_cluster",
            formType = StField.FormType.TEXT
    )
    private String clusterName;

    @StField(
            tag = "ClickHouse配置",
            order = 19,
            required = false,
            enName = "use_ssl",
            cnName = "使用SSL",
            defaultValue = "false",
            description = "是否使用SSL连接",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"true", "false"}
    )
    private Boolean useSsl;

    @StField(
            tag = "ClickHouse配置",
            order = 20,
            required = false,
            enName = "compress",
            cnName = "启用压缩",
            defaultValue = "true",
            description = "是否启用数据传输压缩",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"true", "false"}
    )
    private Boolean compress;

    @StField(
            tag = "ClickHouse配置",
            order = 21,
            required = false,
            enName = "database_charset",
            cnName = "数据库字符集",
            defaultValue = "UTF-8",
            description = "数据库字符编码",
            placeHolder = "UTF-8",
            formType = StField.FormType.TEXT
    )
    private String databaseCharset;

    private Connection connection;

    @Override
    protected boolean doCheckConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection.isValid(30);
            }

            // 构建ClickHouse JDBC URL
            String protocol = useSsl != null && useSsl ? "https" : "http";
            String url = String.format("jdbc:clickhouse://%s:%d/%s", host, port, database);
            
            // 添加连接参数
            StringBuilder urlBuilder = new StringBuilder(url);
            urlBuilder.append("?socket_timeout=").append(socketTimeoutMs != null ? socketTimeoutMs : 60000);
            urlBuilder.append("&connect_timeout=").append(connectTimeoutMs != null ? connectTimeoutMs : 60000);
            
            if (useSsl != null && useSsl) {
                urlBuilder.append("&ssl=true");
            }
            
            if (compress != null && compress) {
                urlBuilder.append("&compress=1");
            }

            // 解析额外配置
            if (clickhouseConfig != null && !clickhouseConfig.trim().isEmpty()) {
                String[] configs = clickhouseConfig.split(";");
                for (String config : configs) {
                    String[] keyValue = config.split("=", 2);
                    if (keyValue.length == 2) {
                        urlBuilder.append("&").append(keyValue[0].trim()).append("=").append(keyValue[1].trim());
                    }
                }
            }

            Class.forName("ru.yandex.clickhouse.ClickHouseDriver");
            Connection testConnection = DriverManager.getConnection(urlBuilder.toString(), username, password);
            boolean isValid = testConnection.isValid(30);
            testConnection.close();
            
            return isValid;
        } catch (SQLException | ClassNotFoundException e) {
            log.error("ClickHouse连接检查失败", e);
            return false;
        }
    }
}