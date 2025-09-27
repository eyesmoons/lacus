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
 * Oracle数据源组件
 * 
 * @author lacus
 */
@Slf4j
@StComponent(
        type = StComponent.ComponentType.SOURCE,
        name = "oracle_source",
        displayName = "Oracle数据源",
        description = "从Oracle数据库读取数据，支持企业级数据库功能",
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
public class OracleSource extends AbstractStSource {

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
            enName = "schema",
            cnName = "模式名",
            placeHolder = "请选择模式",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.URL,
            dictUrl = "/metadata/schema/list/{datasourceId}"
    )
    private String schema;

    @StField(
            tag = "数据源配置",
            order = 3,
            required = true,
            enName = "table_list",
            cnName = "表名",
            placeHolder = "请选择数据表",
            formType = StField.FormType.MULTI_SELECT,
            dictType = StField.DictType.URL,
            dictUrl = "/metadata/table/listTable"
    )
    private String tableList;

    // 连接配置
    @StField(
            tag = "连接配置",
            order = 4,
            required = true,
            enName = "url",
            cnName = "JDBC连接URL",
            description = "Oracle数据库连接URL",
            placeHolder = "jdbc:oracle:thin:@localhost:1521:orcl",
            formType = StField.FormType.TEXT
    )
    private String url;

    @StField(
            tag = "连接配置",
            order = 5,
            required = true,
            enName = "driver",
            cnName = "驱动类名",
            defaultValue = "oracle.jdbc.driver.OracleDriver",
            description = "Oracle JDBC驱动类名",
            placeHolder = "oracle.jdbc.driver.OracleDriver",
            formType = StField.FormType.TEXT
    )
    private String driver;

    @StField(
            tag = "连接配置",
            order = 6,
            required = true,
            enName = "user",
            cnName = "用户名",
            placeHolder = "请输入数据库用户名",
            formType = StField.FormType.TEXT
    )
    private String user;

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

    // 查询配置
    @StField(
            tag = "查询配置",
            order = 8,
            required = true,
            enName = "query",
            cnName = "查询语句",
            description = "SQL查询语句，用于读取数据",
            placeHolder = "SELECT * FROM schema.table_name",
            formType = StField.FormType.TEXT_AREA
    )
    private String query;

    @StField(
            tag = "查询配置",
            order = 9,
            required = false,
            enName = "where_condition",
            cnName = "WHERE条件",
            description = "查询条件，会自动添加到查询语句中",
            placeHolder = "ROWNUM <= 10000",
            formType = StField.FormType.TEXT_AREA
    )
    private String whereCondition;

    // 分区配置
    @StField(
            tag = "分区配置",
            order = 10,
            required = false,
            enName = "partition_column",
            cnName = "分区列",
            description = "用于并行度分区的列名，建议使用数字类型列",
            placeHolder = "ID",
            formType = StField.FormType.TEXT
    )
    private String partitionColumn;

    @StField(
            tag = "分区配置",
            order = 11,
            required = false,
            enName = "partition_lower_bound",
            cnName = "分区下界",
            description = "扫描时partition_column的最小值",
            placeHolder = "1",
            formType = StField.FormType.NUMBER
    )
    private Long partitionLowerBound;

    @StField(
            tag = "分区配置",
            order = 12,
            required = false,
            enName = "partition_upper_bound",
            cnName = "分区上界",
            description = "扫描时partition_column的最大值",
            placeHolder = "100000",
            formType = StField.FormType.NUMBER
    )
    private Long partitionUpperBound;

    @StField(
            tag = "分区配置",
            order = 13,
            required = false,
            enName = "partition_num",
            cnName = "分区数量",
            defaultValue = "4",
            description = "分区数量，仅支持正整数",
            placeHolder = "4",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer partitionNum;

    // 性能配置
    @StField(
            tag = "性能配置",
            order = 14,
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
            order = 15,
            required = false,
            enName = "fetch_size",
            cnName = "数据拉取大小",
            defaultValue = "1000",
            description = "单次获取的记录数量，影响内存使用和网络传输",
            placeHolder = "1000",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer fetchSize;

    @StField(
            tag = "性能配置",
            order = 16,
            required = false,
            enName = "split_size",
            cnName = "分片大小",
            defaultValue = "8096",
            description = "表的分割大小（行数），用于并行读取",
            placeHolder = "8096",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer splitSize;

    // Oracle特殊配置
    @StField(
            tag = "Oracle配置",
            order = 17,
            required = false,
            enName = "oracle_tns_admin",
            cnName = "TNS_ADMIN路径",
            description = "TNS配置文件路径",
            placeHolder = "/opt/oracle/network/admin",
            formType = StField.FormType.TEXT
    )
    private String oracleTnsAdmin;

    @StField(
            tag = "Oracle配置",
            order = 18,
            required = false,
            enName = "oracle_wallet_location",
            cnName = "钱包位置",
            description = "Oracle钱包文件位置，用于SSL连接",
            placeHolder = "/opt/oracle/wallet",
            formType = StField.FormType.TEXT
    )
    private String oracleWalletLocation;

    @StField(
            tag = "Oracle配置",
            order = 19,
            required = false,
            enName = "oracle_ssl",
            cnName = "启用SSL",
            defaultValue = "false",
            description = "是否启用SSL连接",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"true", "false"}
    )
    private Boolean oracleSsl;

    @StField(
            tag = "Oracle配置",
            order = 20,
            required = false,
            enName = "oracle_service_name",
            cnName = "服务名",
            description = "Oracle服务名称",
            placeHolder = "orcl.example.com",
            formType = StField.FormType.TEXT
    )
    private String oracleServiceName;

    @StField(
            tag = "Oracle配置",
            order = 21,
            required = false,
            enName = "oracle_sid",
            cnName = "SID",
            description = "Oracle系统标识符",
            placeHolder = "orcl",
            formType = StField.FormType.TEXT
    )
    private String oracleSid;

    // 其他配置
    @StField(
            tag = "其他配置",
            order = 22,
            required = false,
            enName = "properties",
            cnName = "连接参数",
            description = "额外的连接配置参数，格式：key1=value1;key2=value2",
            placeHolder = "oracle.net.CONNECT_TIMEOUT=10000;oracle.jdbc.ReadTimeout=60000",
            formType = StField.FormType.TEXT_AREA
    )
    private String properties;

    @StField(
            tag = "其他配置",
            order = 23,
            required = false,
            enName = "table_path",
            cnName = "表路径",
            description = "表的完整路径，格式：schema.table",
            placeHolder = "SCOTT.EMP",
            formType = StField.FormType.TEXT
    )
    private String tablePath;

    private Connection connection;

    @Override
    protected boolean doCheckConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection.isValid(connectionCheckTimeoutSec != null ? connectionCheckTimeoutSec : 30);
            }

            // 创建新连接进行测试
            Class.forName(driver);
            Connection testConnection = DriverManager.getConnection(url, user, password);
            boolean isValid = testConnection.isValid(connectionCheckTimeoutSec != null ? connectionCheckTimeoutSec : 30);
            testConnection.close();
            
            return isValid;
        } catch (SQLException | ClassNotFoundException e) {
            log.error("Oracle连接检查失败", e);
            return false;
        }
    }
}