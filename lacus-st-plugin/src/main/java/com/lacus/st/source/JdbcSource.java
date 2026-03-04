package com.lacus.st.source;

import com.alibaba.fastjson2.JSONObject;
import com.google.auto.service.AutoService;
import com.lacus.st.abstracts.AbstractStSource;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.annotation.StField;
import com.lacus.st.annotation.StTag;
import com.lacus.st.annotation.StTag.TagDefinition;
import com.lacus.st.interfaces.StComponentInterface;
import lombok.extern.slf4j.Slf4j;

/**
 * MySQL数据源组件
 */
@Slf4j
@StComponent(
        type = StComponent.ComponentType.SOURCE,
        name = "jdbc_source",
        displayName = "JDBC数据源",
        description = "从JDBC数据库读取数据的组件",
        version = "2.0.0",
        author = "lacus",
        connectorKey = "Jdbc"
)
@StTag({
        @TagDefinition(name = "数据源配置", displayName = "数据源配置", order = 1, description = "数据源选择和连接配置"),
        @TagDefinition(name = "查询配置", displayName = "查询配置", order = 2, description = "查询配置"),
        @TagDefinition(name = "连接器配置", displayName = "连接器配置", order = 3, description = "连接器配置"),
        @TagDefinition(name = "分片配置", displayName = "分片配置", order = 4, description = "分片配置"),
        @TagDefinition(name = "分区配置", displayName = "分区配置", order = 5, description = "分区配置"),
        @TagDefinition(name = "性能配置", displayName = "性能配置", order = 6, description = "性能优化相关配置"),
        @TagDefinition(name = "数据类型转换配置", displayName = "数据类型转换配置", order = 7, description = "数据类型转换配置"),
        @TagDefinition(name = "其他配置", displayName = "其他配置", order = 8, description = "其他扩展配置")
})

@AutoService(StComponentInterface.class)
public class JdbcSource extends AbstractStSource {

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

    @StField(
            tag = "查询配置",
            order = 1,
            required = false,
            enName = "where_condition",
            cnName = "过滤条件",
            description = "适用于所有表或查询的通用行过滤条件，必须以 WHERE 开头。例如：WHERE id > 100",
            placeHolder = "过滤条件，例如：id > 100",
            formType = StField.FormType.TEXT_AREA
    )
    private Integer where_condition;

    // 连接器配置
    @StField(
            tag = "连接器配置",
            order = 1,
            required = false,
            enName = "compatible_mode",
            cnName = "数据库的兼容模式",
            defaultValue = "mysql",
            description = "数据库的兼容模式。当数据库支持多种兼容模式时需要设置该参数。例如：使用 OceanBase 数据库时，需要设置为 mysql 或 oracle；使用 StarRocks 数据库时，需要设置为 starrocks。",
            placeHolder = "请输入数据库的兼容模式",
            formType = StField.FormType.TEXT
    )
    private Integer compatibleMode;

    @StField(
            tag = "连接器配置",
            order = 2,
            required = false,
            enName = "dialect",
            cnName = "SQL 方言",
            description = "指定的 SQL 方言。如果未指定或指定的方言不存在，则仍会根据 URL 自动识别获取；该参数的优先级高于 URL。例如：使用 StarRocks 时，需要将其设置为 starrocks。",
            placeHolder = "SQL 方言",
            formType = StField.FormType.TEXT
    )
    private String dialect;

    @StField(
            tag = "连接器配置",
            order = 3,
            required = false,
            enName = "connection_check_timeout_sec",
            cnName = "验证连接超时时间（单位：秒）",
            description = "用于验证连接的数据库操作的超时时间（单位：秒）。",
            placeHolder = "验证连接超时时间（单位：秒）",
            formType = StField.FormType.TEXT
    )
    private String connectionCheckTimeoutSec;

    @StField(
            tag = "分片配置",
            order = 1,
            required = false,
            enName = "split_size",
            cnName = "分片大小（行数）",
            description = "每个分片包含的行数。在读取表数据时，被采集的表会根据该参数拆分为多个分片。注意： 该参数仅在使用 table_path 参数时生效，使用 query 参数时不生效。",
            placeHolder = "分片大小（行数）",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private String split_size;

    @StField(
            tag = "分片配置",
            order = 2,
            required = false,
            enName = "split_even_distribution_factor_upper_bound",
            cnName = "分片上限",
            description = "分片上限",
            placeHolder = "分片上限",
            formType = StField.FormType.POSITIVE_NUMBER,
            defaultValue = "0.05"
    )
    private String splitUpperBound;

    @StField(
            tag = "分片配置",
            order = 3,
            required = false,
            enName = "split_even_distribution_factor_lower_bound",
            cnName = "分片下限",
            description = "分片下限",
            placeHolder = "分片下限",
            formType = StField.FormType.POSITIVE_NUMBER,
            defaultValue = "0.05"
    )
    private String splitLowerBound;

    @StField(
            tag = "分片配置",
            order = 4,
            required = false,
            enName = "split_sample_sharding_threshold",
            cnName = "分片数量阈值",
            description = "分片数量阈值",
            placeHolder = "分片数量阈值",
            formType = StField.FormType.POSITIVE_NUMBER,
            defaultValue = "1000"
    )
    private String splitThreshold;

    @StField(
            tag = "分片配置",
            order = 5,
            required = false,
            enName = "split_inverse_sampling_rate",
            cnName = "采样率倒数",
            description = "采样分片策略中使用的采样率倒数。",
            placeHolder = "采样率倒数",
            formType = StField.FormType.POSITIVE_NUMBER,
            defaultValue = "1000"
    )
    private String splitInverseRate;

    @StField(
            tag = "分片配置",
            order = 6,
            required = false,
            enName = "split_string_split_mode",
            cnName = "字符串分片算法",
            description = "支持多种字符串分片算法。默认使用 sample 算法，通过对字符串值进行采样来确定分片边界。也可以切换为 charset_based，以启用基于字符集的字符串分片算法。当设置为 charset_based 时，算法假定分区列中的字符位于 ASCII 32–126 范围内，该范围覆盖了大多数基于字符的分片场景。。",
            placeHolder = "字符串分片算法",
            formType = StField.FormType.POSITIVE_NUMBER,
            defaultValue = "1000"
    )
    private String split_string_split_mode;

    @StField(
            tag = "分片配置",
            order = 7,
            required = false,
            enName = "split_string_split_mode_collate",
            cnName = "排序规则",
            description = "当 string_split_mode 设置为 charset_based 且表使用了特殊排序规则（collation）时，用于指定所采用的排序规则。如果未指定该参数，则使用数据库的默认排序规则。",
            placeHolder = "排序规则",
            formType = StField.FormType.TEXT
    )
    private String split_string_split_mode_collate;

    @StField(
            tag = "分区配置",
            order = 1,
            required = false,
            enName = "partition_column",
            cnName = "分区字段",
            placeHolder = "请输入分区字段",
            formType = StField.FormType.TEXT,
            description = "分区字段"
    )
    private String partitionColumn;


    @StField(
            tag = "分区配置",
            order = 2,
            required = false,
            enName = "partition_upper_bound",
            cnName = "分区上界",
            placeHolder = "请输入分区上界",
            formType = StField.FormType.POSITIVE_NUMBER,
            description = "扫描时 partition_column 的最大值，如果未设置，SeaTunnel 将查询数据库以获取最大值。"
    )
    private String partitionUpperBound;

    @StField(
            tag = "分区配置",
            order = 3,
            required = false,
            enName = "partition_lower_bound",
            cnName = "分区下界",
            description = "扫描时 partition_column 的最小值，如果未设置，SeaTunnel 将查询数据库以获取最小值。",
            placeHolder = "请输入分区下界",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private String partitionLowerBound;

    @StField(
            tag = "分区配置",
            order = 4,
            required = false,
            enName = "partition_num",
            cnName = "分区数量",
            description = "分区数量，仅支持正整数。默认值为作业并行度。",
            placeHolder = "请输入分区数量",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private String partitionNum;

    @StField(
            tag = "数据类型转换配置",
            order = 10,
            required = false,
            enName = "decimal_type_narrowing",
            cnName = "Decimal 类型收窄开关",
            placeHolder = "Decimal 类型收窄开关",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"true", "false"},
            defaultValue = "false",
            description = "Decimal 类型收窄开关。当该参数为 true 时，如果在不损失精度的情况下，decimal 类型将被收窄为 int 或 long 类型。目前仅支持 Oracle。"
    )
    private String decimalTypeNarrowing;

    @StField(
            tag = "数据类型转换配置",
            order = 1,
            required = false,
            enName = "int_type_narrowing",
            cnName = "int 类型收窄开关",
            placeHolder = "int 类型收窄开关",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"true", "false"},
            defaultValue = "false",
            description = "Int 类型收窄开关。当该参数为 true 时，如果在不损失精度的情况下，tinyint(1) 类型将被收窄为 boolean 类型。目前仅支持 MySQL。"
    )
    private String int_type_narrowing;

    @StField(
            tag = "数据类型转换配置",
            order = 2,
            required = false,
            enName = "handle_blob_as_string",
            cnName = "BLOB 是否转为 STRING",
            placeHolder = "BLOB 是否转为 STRING",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"true", "false"},
            defaultValue = "false",
            description = "当该参数为 true 时，BLOB 类型将被转换为 STRING 类型。仅支持 Oracle 数据库。该参数适用于处理 Oracle 中超过默认大小限制的大型 BLOB 字段。在将 Oracle 的 BLOB 字段传输到 Doris 等系统时，将该参数设置为 true 可以提高数据传输效率。"
    )
    private String handle_blob_as_string;

    @StField(
            tag = "性能配置",
            order = 1,
            required = false,
            enName = "use_select_count",
            cnName = "是否启用SELECT COUNT统计",
            description = "在动态分片拆分阶段，使用 SELECT COUNT 来获取表记录数，而不是采用其他统计方式。该功能目前仅支持 jdbc-oracle。在某些场景下，当通过 ANALYZE TABLE 等方式更新统计信息较慢时，直接使用 SELECT COUNT 会更高效。",
            placeHolder = "是否启用SELECT COUNT统计",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"true", "false"},
            defaultValue = "false"
    )
    private String use_select_count;

    @StField(
            tag = "性能配置",
            order = 2,
            required = false,
            enName = "skip_analyze",
            cnName = "是否跳过表记录数分析",
            description = "在动态分片拆分阶段跳过表记录数的统计分析。该功能目前仅支持 jdbc-oracle。适用于以下场景：已通过定时执行 ANALYZE TABLE 等 SQL 来周期性更新相关表的统计信息；或表数据变更不频繁。",
            placeHolder = "是否跳过表记录数分析",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"true", "false"},
            defaultValue = "true"
    )
    private String skip_analyze;

    @StField(
            tag = "其他配置",
            order = 1,
            required = false,
            enName = "properties",
            cnName = "额外的连接配置参数",
            description = "额外的连接配置参数。当 properties 与 URL 中存在相同参数时，其优先级由具体的 JDBC 驱动实现决定。例如，在 MySQL 中，properties 的优先级高于 URL。",
            placeHolder = "额外的连接配置参数",
            formType = StField.FormType.TEXT
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
        putIfNotEmpty(config, "user", connectionConfig.getString("username"));
        putIfNotEmpty(config, "password", connectionConfig.getString("password"));
        putIfNotEmpty(config, "compatible_mode", connectionConfig.getString("compatible_mode"));
        putIfNotEmpty(config, "dialect", connectionConfig.getString("dialect"));
        putIfNotEmpty(config, "connection_check_timeout_sec", connectionConfig.getInteger("connection_check_timeout_sec"));
        putIfNotEmpty(config, "partition_column", connectionConfig.getString("partition_column"));
        putIfNotEmpty(config, "partition_upper_bound", connectionConfig.getLong("partition_upper_bound"));
        putIfNotEmpty(config, "partition_lower_bound", connectionConfig.getLong("partition_lower_bound"));
        putIfNotEmpty(config, "partition_num", connectionConfig.getInteger("partition_num"));
        putIfNotEmpty(config, "decimal_type_narrowing", connectionConfig.getBoolean("decimal_type_narrowing"));
        putIfNotEmpty(config, "int_type_narrowing", connectionConfig.getBoolean("int_type_narrowing"));
        putIfNotEmpty(config, "handle_blob_as_string", connectionConfig.getBoolean("handle_blob_as_string"));
        putIfNotEmpty(config, "use_select_count", connectionConfig.getBoolean("use_select_count"));
        putIfNotEmpty(config, "skip_analyze", connectionConfig.getBoolean("skip_analyze"));
        String whereCondition = connectionConfig.getString("where_condition");
        putIfNotEmpty(config, "where_condition", whereCondition);
        putIfNotEmpty(config, "split.size", connectionConfig.getInteger("split_size"));
        putIfNotEmpty(config, "split.even-distribution.factor.upper-bound", connectionConfig.getInteger("split_even_distribution_factor_upper_bound"));
        putIfNotEmpty(config, "split.even-distribution.factor.lower-bound", connectionConfig.getInteger("split_even_distribution_factor_lower_bound"));
        putIfNotEmpty(config, "split.sample-sharding.threshold", connectionConfig.getInteger("split_sample_sharding_threshold"));
        putIfNotEmpty(config, "split.inverse-sampling.rate", connectionConfig.getInteger("split_inverse_sampling_rate"));
        putIfNotEmpty(config, "split.string_split_mode", connectionConfig.getString("split_string_split_mode"));
        putIfNotEmpty(config, "split.string_split_mode_collate", connectionConfig.getString("split_string_split_mode_collate"));
        addTableList(config, connectionConfig, whereCondition);
        return config;
    }
}
