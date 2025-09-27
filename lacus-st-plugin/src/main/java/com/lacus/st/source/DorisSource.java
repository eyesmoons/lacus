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
 * Doris数据源组件
 *
 * @author lacus
 */
@Slf4j
@StComponent(
        type = StComponent.ComponentType.SOURCE,
        name = "doris_source",
        displayName = "Doris数据源",
        description = "从Apache Doris数据库读取数据，支持高性能分析查询",
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
public class DorisSource extends AbstractStSource {

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
            enName = "fenodes",
            cnName = "Frontend节点",
            description = "Doris Frontend节点地址，格式：ip:port",
            placeHolder = "127.0.0.1:8030",
            formType = StField.FormType.TEXT
    )
    private String fenodes;

    @StField(
            tag = "连接配置",
            order = 5,
            required = true,
            enName = "username",
            cnName = "用户名",
            description = "Doris数据库用户名",
            placeHolder = "root",
            formType = StField.FormType.TEXT
    )
    private String username;

    @StField(
            tag = "连接配置",
            order = 6,
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
            order = 7,
            required = false,
            enName = "doris_read_field",
            cnName = "读取字段",
            description = "需要读取的字段列表，用逗号分隔，为空则读取所有字段",
            placeHolder = "id,name,age",
            formType = StField.FormType.TEXT
    )
    private String dorisReadField;

    @StField(
            tag = "查询配置",
            order = 8,
            required = false,
            enName = "doris_filter_query",
            cnName = "过滤条件",
            description = "数据过滤条件，SQL WHERE子句",
            placeHolder = "age > 18 AND status = 'active'",
            formType = StField.FormType.TEXT_AREA
    )
    private String dorisFilterQuery;

    @StField(
            tag = "查询配置",
            order = 9,
            required = false,
            enName = "doris_tablet_size",
            cnName = "Tablet大小",
            defaultValue = "1",
            description = "单个Tablet对应的Partition数量",
            placeHolder = "1",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer dorisTabletSize;

    // 性能配置
    @StField(
            tag = "性能配置",
            order = 10,
            required = false,
            enName = "doris_request_connect_timeout_ms",
            cnName = "连接超时时间",
            defaultValue = "30000",
            description = "连接超时时间(毫秒)",
            placeHolder = "30000",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer dorisRequestConnectTimeoutMs;

    @StField(
            tag = "性能配置",
            order = 11,
            required = false,
            enName = "doris_request_read_timeout_ms",
            cnName = "读取超时时间",
            defaultValue = "30000",
            description = "读取超时时间(毫秒)",
            placeHolder = "30000",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer dorisRequestReadTimeoutMs;

    @StField(
            tag = "性能配置",
            order = 12,
            required = false,
            enName = "doris_request_query_timeout_s",
            cnName = "查询超时时间",
            defaultValue = "3600",
            description = "查询超时时间(秒)",
            placeHolder = "3600",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer dorisRequestQueryTimeoutS;

    @StField(
            tag = "性能配置",
            order = 13,
            required = false,
            enName = "doris_request_retries",
            cnName = "重试次数",
            defaultValue = "3",
            description = "请求失败时的重试次数",
            placeHolder = "3",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer dorisRequestRetries;

    @StField(
            tag = "性能配置",
            order = 14,
            required = false,
            enName = "doris_batch_size",
            cnName = "批处理大小",
            defaultValue = "1024",
            description = "每批次读取的记录数",
            placeHolder = "1024",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer dorisBatchSize;

    @StField(
            tag = "性能配置",
            order = 15,
            required = false,
            enName = "doris_exec_mem_limit",
            cnName = "执行内存限制",
            defaultValue = "2147483648",
            description = "单个查询的内存限制(字节)",
            placeHolder = "2147483648",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Long dorisExecMemLimit;

    @StField(
            tag = "性能配置",
            order = 16,
            required = false,
            enName = "doris_deserialize_arrow_async",
            cnName = "异步反序列化",
            defaultValue = "false",
            description = "是否异步反序列化Arrow数据",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"true", "false"}
    )
    private Boolean dorisDeserializeArrowAsync;

    @StField(
            tag = "性能配置",
            order = 17,
            required = false,
            enName = "doris_deserialize_queue_size",
            cnName = "反序列化队列大小",
            defaultValue = "64",
            description = "反序列化队列大小",
            placeHolder = "64",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer dorisDeserializeQueueSize;

    // 其他配置
    @StField(
            tag = "其他配置",
            order = 18,
            required = false,
            enName = "jdbc_url",
            cnName = "JDBC连接URL",
            description = "Doris的JDBC连接URL，用于获取元数据",
            placeHolder = "jdbc:mysql://127.0.0.1:9030/test",
            formType = StField.FormType.TEXT
    )
    private String jdbcUrl;

    @StField(
            tag = "其他配置",
            order = 19,
            required = false,
            enName = "doris_config",
            cnName = "额外配置",
            description = "额外的Doris配置参数，格式：key1=value1;key2=value2",
            placeHolder = "enable_vectorized_engine=true;parallel_fragment_exec_instance_num=1",
            formType = StField.FormType.TEXT_AREA
    )
    private String dorisConfig;

    private Connection connection;

    @Override
    protected boolean doCheckConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection.isValid(30);
            }

            // 使用JDBC连接测试Doris连接
            String url = jdbcUrl;
            if (url == null || url.trim().isEmpty()) {
                // 构建默认的JDBC URL
                String[] feNodes = fenodes.split(",");
                if (feNodes.length > 0) {
                    String[] hostPort = feNodes[0].split(":");
                    String host = hostPort[0];
                    String queryPort = hostPort.length > 1 ?
                        String.valueOf(Integer.parseInt(hostPort[1]) + 1000) : "9030";  // 默认query_port是http_port+1000
                    url = String.format("jdbc:mysql://%s:%s/%s", host, queryPort, database != null ? database : "");
                }
            }

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection testConnection = DriverManager.getConnection(url, username, password);
            boolean isValid = testConnection.isValid(30);
            testConnection.close();

            return isValid;
        } catch (SQLException | ClassNotFoundException e) {
            log.error("Doris连接检查失败", e);
            return false;
        }
    }
}
