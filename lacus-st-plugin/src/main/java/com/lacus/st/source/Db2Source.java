package com.lacus.st.source;

import com.alibaba.fastjson2.JSONObject;
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
 * DB2数据源组件
 *
 * @author lacus
 */
@Slf4j
@StComponent(
        type = StComponent.ComponentType.SOURCE,
        name = "db2_source",
        displayName = "DB2数据源",
        description = "从IBM DB2数据库读取数据",
        version = "2.0.0",
        author = "lacus",
        connectorKey = "Jdbc"
)
@StTag({
        @TagDefinition(name = "数据源配置", displayName = "数据源配置", order = 1, description = "数据源选择和连接配置"),
        @TagDefinition(name = "查询配置", displayName = "查询配置", order = 2, description = "数据查询相关配置"),
        @TagDefinition(name = "性能配置", displayName = "性能配置", order = 3, description = "性能优化相关配置"),
        @TagDefinition(name = "分区配置", displayName = "分区配置", order = 4, description = "分区配置"),
        @TagDefinition(name = "其他配置", displayName = "其他配置", order = 5, description = "其他配置")
})

@AutoService(StComponentInterface.class)
public class Db2Source extends AbstractStSource {

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

    // 查询配置
    @StField(
            tag = "查询配置",
            order = 1,
            required = true,
            enName = "query",
            cnName = "查询语句",
            description = "SQL查询语句，用于读取数据",
            placeHolder = "SELECT * FROM schema.table_name",
            formType = StField.FormType.TEXT_AREA
    )
    private String query;

    // 性能配置
    @StField(
            tag = "性能配置",
            order = 1,
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
            tag = "分区配置",
            order = 1,
            required = false,
            enName = "partition_column",
            cnName = "分区列名",
            description = "并行分区的列名，只支持数值类型，只支持数字类型主键，只能配置一列。",
            placeHolder = "并行分区的列名",
            formType = StField.FormType.TEXT
    )
    private Integer partitionColumn;

    @StField(
            tag = "分区配置",
            order = 2,
            required = false,
            enName = "partition_lower_bound",
            cnName = "分区下限",
            description = "扫描的partition_column最小值，如果未设置，SeaTunnel将查询数据库获取最小值。",
            placeHolder = "分区下限",
            formType = StField.FormType.TEXT
    )
    private Integer partitionLowerBound;

    @StField(
            tag = "分区配置",
            order = 3,
            required = false,
            enName = "partition_upper_bound",
            cnName = "分区上限",
            description = "扫描的partition_column最大值，如果没有设置，SeaTunnel将查询数据库获取最大值。",
            placeHolder = "分区上限",
            formType = StField.FormType.TEXT
    )
    private Integer partitionUpperBound;

    @StField(
            tag = "分区配置",
            order = 4,
            required = false,
            enName = "partition_num",
            cnName = "分区数",
            description = "分区计数的数量，只支持正整数。默认值是作业并行性",
            placeHolder = "请输入分区数",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer partitionNum;

    // 其他配置
    @StField(
            tag = "其他配置",
            order = 1,
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
            tag = "其他配置",
            order = 2,
            required = false,
            enName = "properties",
            cnName = "其他连接配置参数",
            description = "其他连接配置参数，当属性和URL具有相同的参数时，优先级由驱动程序的特定实现决定。例如，在MySQL中，属性优先于URL。",
            placeHolder = "其他连接配置参数",
            formType = StField.FormType.TEXT_AREA
    )
    private Integer properties;

    @Override
    protected boolean doCheckConnection() {
       return true;
    }

    @Override
    public JSONObject buildTaskConfig(JSONObject connectionConfig, Long datasourceId) {
        JSONObject config = new JSONObject();
        putIfNotEmpty(config, "url", connectionConfig.getString("url"));
        config.put("driver", "com.ibm.db2.jdbc.app.DB2Driver");
        putIfNotEmpty(config, "user", connectionConfig.getString("username"));
        putIfNotEmpty(config, "password", connectionConfig.getString("password"));
        putIfNotEmpty(config, "query", connectionConfig.getString("query"));
        putIfNotEmpty(config, "connection_check_timeout_sec", connectionConfig.getString("connection_check_timeout_sec"));
        putIfNotEmpty(config, "partition_column", connectionConfig.getString("partition_column"));
        putIfNotEmpty(config, "partition_lower_bound", connectionConfig.getString("partition_lower_bound"));
        putIfNotEmpty(config, "partition_upper_bound", connectionConfig.getString("partition_upper_bound"));
        putIfNotEmpty(config, "partition_num", connectionConfig.getString("partition_num"));
        putIfNotEmpty(config, "fetch_size", connectionConfig.getString("fetch_size"));
        putIfNotEmpty(config, "properties", connectionConfig.getString("properties"));
        return config;
    }
}
