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
 * StarRocks数据源组件
 * 
 * @author lacus
 */
@Slf4j
@StComponent(
        type = StComponent.ComponentType.SOURCE,
        name = "starrocks_source",
        displayName = "StarRocks数据源",
        description = "从StarRocks数据库读取数据，支持高性能实时分析",
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
public class StarRocksSource extends AbstractStSource {

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
            cnName = "数据库名",
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
            cnName = "表名",
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
            enName = "nodeUrls",
            cnName = "节点地址",
            description = "StarRocks Frontend节点地址列表，格式：ip:http_port",
            placeHolder = "127.0.0.1:8030,127.0.0.1:8031",
            formType = StField.FormType.TEXT
    )
    private String nodeUrls;

    @StField(
            tag = "连接配置",
            order = 5,
            required = true,
            enName = "username",
            cnName = "用户名",
            description = "StarRocks数据库用户名",
            placeHolder = "root",
            formType = StField.FormType.TEXT
    )
    private String username;

    @StField(
            tag = "连接配置",
            order = 6,
            required = true,
            enName = "password",
            cnName = "密码",
            placeHolder = "请输入数据库密码",
            formType = StField.FormType.PASSWORD
    )
    private String password;

    // 查询配置
    @StField(
            tag = "查询配置",
            order = 7,
            required = false,
            enName = "scan_filter",
            cnName = "扫描过滤条件",
            description = "数据扫描过滤条件，SQL WHERE子句",
            placeHolder = "age > 18 AND status = 'active'",
            formType = StField.FormType.TEXT_AREA
    )
    private String scanFilter;

    @StField(
            tag = "查询配置",
            order = 8,
            required = false,
            enName = "scan_columns",
            cnName = "扫描列",
            description = "需要扫描的列名列表，用逗号分隔，为空则扫描所有列",
            placeHolder = "id,name,age,created_time",
            formType = StField.FormType.TEXT
    )
    private String scanColumns;

    @StField(
            tag = "查询配置",
            order = 9,
            required = false,
            enName = "scan_be_max_parallel",
            cnName = "BE最大并行度",
            defaultValue = "1",
            description = "单个BE节点的最大并行扫描任务数",
            placeHolder = "1",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer scanBeMaxParallel;

    // 性能配置
    @StField(
            tag = "性能配置",
            order = 10,
            required = false,
            enName = "scan_request_timeout_ms",
            cnName = "扫描请求超时",
            defaultValue = "30000",
            description = "扫描请求超时时间(毫秒)",
            placeHolder = "30000",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer scanRequestTimeoutMs;

    @StField(
            tag = "性能配置",
            order = 11,
            required = false,
            enName = "scan_keep_alive_min",
            cnName = "扫描保活时间",
            defaultValue = "10",
            description = "扫描任务保活时间(分钟)",
            placeHolder = "10",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer scanKeepAliveMin;

    @StField(
            tag = "性能配置",
            order = 12,
            required = false,
            enName = "scan_batch_rows",
            cnName = "批次行数",
            defaultValue = "1024",
            description = "每批次扫描的行数",
            placeHolder = "1024",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer scanBatchRows;

    @StField(
            tag = "性能配置",
            order = 13,
            required = false,
            enName = "scan_mem_limit",
            cnName = "扫描内存限制",
            defaultValue = "1073741824",
            description = "单次扫描的内存限制(字节)",
            placeHolder = "1073741824",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Long scanMemLimit;

    @StField(
            tag = "性能配置",
            order = 14,
            required = false,
            enName = "max_retries",
            cnName = "最大重试次数",
            defaultValue = "3",
            description = "连接或查询失败时的最大重试次数",
            placeHolder = "3",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer maxRetries;

    // JDBC配置
    @StField(
            tag = "JDBC配置",
            order = 15,
            required = false,
            enName = "jdbc_url",
            cnName = "JDBC连接URL",
            description = "StarRocks的JDBC连接URL，用于获取元数据信息",
            placeHolder = "jdbc:mysql://127.0.0.1:9030/test",
            formType = StField.FormType.TEXT
    )
    private String jdbcUrl;

    // 高级配置
    @StField(
            tag = "高级配置",
            order = 16,
            required = false,
            enName = "scan_url",
            cnName = "扫描URL模式",
            description = "自定义扫描URL的请求模式",
            placeHolder = "/api/{api_version}/warehouses/{warehouse_id}/tablets",
            formType = StField.FormType.TEXT
    )
    private String scanUrl;

    @StField(
            tag = "高级配置",
            order = 17,
            required = false,
            enName = "use_tablet_scan",
            cnName = "使用Tablet扫描",
            defaultValue = "true",
            description = "是否使用Tablet级别的扫描，提高并行度",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"true", "false"}
    )
    private Boolean useTabletScan;

    @StField(
            tag = "高级配置",
            order = 18,
            required = false,
            enName = "scan_params",
            cnName = "扫描参数",
            description = "额外的扫描参数配置，格式：key1=value1;key2=value2",
            placeHolder = "enable_vectorized_scan=true;tablet_size=1048576",
            formType = StField.FormType.TEXT_AREA
    )
    private String scanParams;

    // 安全配置
    @StField(
            tag = "安全配置",
            order = 19,
            required = false,
            enName = "enable_https",
            cnName = "启用HTTPS",
            defaultValue = "false",
            description = "是否启用HTTPS连接",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"true", "false"}
    )
    private Boolean enableHttps;

    @StField(
            tag = "安全配置",
            order = 20,
            required = false,
            enName = "headers",
            cnName = "HTTP请求头",
            description = "自定义HTTP请求头，格式：key1=value1;key2=value2",
            placeHolder = "Authorization=Bearer token;Content-Type=application/json",
            formType = StField.FormType.TEXT_AREA
    )
    private String headers;

    private Connection connection;

    @Override
    protected boolean doCheckConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection.isValid(30);
            }

            // 使用JDBC连接测试StarRocks连接
            String url = jdbcUrl;
            if (url == null || url.trim().isEmpty()) {
                // 从nodeUrls构建JDBC URL
                String[] nodes = nodeUrls.split(",");
                if (nodes.length > 0) {
                    String[] hostPort = nodes[0].split(":");
                    String host = hostPort[0];
                    String queryPort = hostPort.length > 1 ? 
                        String.valueOf(Integer.parseInt(hostPort[1]) + 1000) : "9030";  // 默认query_port
                    url = String.format("jdbc:mysql://%s:%s/%s", host, queryPort, database != null ? database : "");
                }
            }

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection testConnection = DriverManager.getConnection(url, username, password);
            boolean isValid = testConnection.isValid(30);
            testConnection.close();
            
            return isValid;
        } catch (SQLException | ClassNotFoundException e) {
            log.error("StarRocks连接检查失败", e);
            return false;
        }
    }
}