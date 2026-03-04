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
 *
 * @author lacus
 */
@Slf4j
@StComponent(
        type = StComponent.ComponentType.SOURCE,
        name = "mysql_source",
        displayName = "MySQL数据源",
        description = "从MySQL数据库读取数据的组件，支持批量读取和连接池配置",
        version = "2.0.0",
        author = "lacus",
        connectorKey = "Jdbc"
)
@StTag({
        @TagDefinition(name = "数据源配置", displayName = "数据源配置", order = 1, description = "数据源选择和数据表配置"),
        @TagDefinition(name = "连接配置", displayName = "连接配置", order = 2, description = "数据库连接相关配置"),
        @TagDefinition(name = "查询配置", displayName = "查询配置", order = 3, description = "SQL查询相关配置"),
        @TagDefinition(name = "性能配置", displayName = "性能配置", order = 4, description = "性能优化相关配置"),
        @TagDefinition(name = "分片配置", displayName = "分片配置", order = 5, description = "分片配置"),
        @TagDefinition(name = "分区配置", displayName = "分区配置", order = 6, description = "并行分区相关配置"),
        @TagDefinition(name = "其他配置", displayName = "其他配置", order = 7, description = "其他扩展配置")
})
@AutoService(StComponentInterface.class)
public class MysqlSource extends AbstractStSource {

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
            formType = StField.FormType.MULTI_SELECT,
            dictType = StField.DictType.URL,
            dictUrl = "/metadata/table/listTable"
    )
    private String table;

    // 查询配置
    @StField(
            tag = "查询配置",
            order = 1,
            required = false,
            enName = "where_condition",
            cnName = "WHERE条件",
            description = "查询条件，会自动添加到查询语句中",
            placeHolder = "id > 100",
            formType = StField.FormType.TEXT_AREA
    )
    private String where_condition;

    // 性能配置
    @StField(
            tag = "性能配置",
            order = 1,
            required = false,
            enName = "connection_check_timeout_sec",
            cnName = "连接检查超时时间(秒)",
            defaultValue = "30",
            description = "验证数据库连接所使用的操作完成的等待时间（秒）",
            placeHolder = "30",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer connection_check_timeout_sec;

    @StField(
            tag = "性能配置",
            order = 2,
            required = false,
            enName = "fetch_size",
            cnName = "数据拉取大小",
            defaultValue = "1000",
            description = "单次获取的记录数量，影响内存使用和网络传输",
            placeHolder = "1000",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer fetch_size;

    @StField(
            tag = "分片配置",
            order = 1,
            required = false,
            enName = "split_size",
            cnName = "分片大小",
            defaultValue = "8096",
            description = "表的分割大小（行数），当读取表时，捕获的表会被分割成多个分片。",
            placeHolder = "表的分片大小（行数）",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer split_size;

    @StField(
            tag = "分片配置",
            order = 2,
            required = false,
            enName = "split.even-distribution.factor.lower-bound",
            cnName = "分片键分布因子的下限",
            defaultValue = "0.05",
            description = "分片键分布因子的下限。该因子用于判断表数据的分布是否均匀。如果计算得到的分布因子大于或等于该下限（即，(MAX(id) - MIN(id) + 1) / 行数），则会对表的分片进行优化，以确保数据的均匀分布。反之，如果分布因子较低，则表数据将被视为分布不均匀。如果估算的分片数量超过 sample-sharding.threshold 所指定的值，则会采用基于采样的分片策略。默认值为 0.05。",
            placeHolder = "分片键分布因子的下限",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Double split_even_distribution_factor_lower_bound;

    @StField(
            tag = "分片配置",
            order = 3,
            required = false,
            enName = "split.even-distribution.factor.upper-bound",
            cnName = "分片键分布因子的上限",
            defaultValue = "100",
            description = "分片键分布因子的上限。该因子用于判断表数据的分布是否均匀。如果计算得到的分布因子小于或等于该上限（即，(MAX(id) - MIN(id) + 1) / 行数），则会对表的分片进行优化，以确保数据的均匀分布。反之，如果分布因子较大，则表数据将被视为分布不均匀，并且如果估算的分片数量超过 sample-sharding.threshold 所指定的值，则会采用基于采样的分片策略。默认值为 100.0。",
            placeHolder = "分片键分布因子的上限",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Double split_even_distribution_factor_upper_bound;

    @StField(
            tag = "分片配置",
            order = 4,
            required = false,
            enName = "split.sample-sharding.threshold",
            cnName = "分片估算阈值",
            defaultValue = "10000",
            description = "此配置指定了触发样本分片策略的估算分片数阈值。当分布因子超出由 chunk-key.even-distribution.factor.upper-bound 和 chunk-key.even-distribution.factor.lower-bound 指定的范围，并且估算的分片数量（计算方法为大致行数 / 分片大小）超过此阈值时，将使用样本分片策略。此配置有助于更高效地处理大型数据集。默认值为 1000 个分片。",
            placeHolder = "指定触发样本分片策略的估算分片数阈值",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer split_sample_sharding_threshold;

    @StField(
            tag = "分片配置",
            order = 5,
            required = false,
            enName = "split.inverse-sampling.rate",
            cnName = "样本分片策略中使用的采样率的倒数",
            defaultValue = "1000",
            description = "样本分片策略中使用的采样率的倒数。例如，如果该值设置为 1000，则表示在采样过程中应用 1/1000 的采样率。此选项提供了灵活性，可以控制采样的粒度，从而影响最终的分片数量。特别适用于处理非常大的数据集，在这种情况下通常会选择较低的采样率。默认值为 1000。",
            placeHolder = "样本分片策略中使用的采样率的倒数",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer split_inverse_rate;

    @StField(
            tag = "分片配置",
            order = 6,
            required = false,
            enName = "int_type_narrowing",
            cnName = "Int类型是否收窄",
            defaultValue = "false",
            description = "Int类型收窄，如果为 true，则 tinyint(1) 类型将被收窄为 boolean 类型（如果没有精度损失）。目前仅支持 MySQL",
            placeHolder = "Int类型收窄",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"true", "false"}
    )
    private Integer int_type_narrowing;

    // 分区配置
    @StField(
            tag = "分区配置",
            order = 1,
            required = false,
            enName = "partition_column",
            cnName = "分区列",
            description = "用于并行度分区的列名，仅支持数字类型的主键",
            placeHolder = "请输入分区列",
            formType = StField.FormType.TEXT
    )
    private String partition_column;

    @StField(
            tag = "分区配置",
            order = 2,
            required = false,
            enName = "partition_lower_bound",
            cnName = "分区下界",
            description = "扫描时partition_column的最小值",
            placeHolder = "请输入分区下界",
            formType = StField.FormType.NUMBER
    )
    private Long partition_lower_bound;

    @StField(
            tag = "分区配置",
            order = 3,
            required = false,
            enName = "partition_upper_bound",
            cnName = "分区上界",
            description = "扫描时partition_column的最大值",
            placeHolder = "请输入分区上界",
            formType = StField.FormType.NUMBER
    )
    private Long partition_upper_bound;

    @StField(
            tag = "分区配置",
            order = 4,
            required = false,
            enName = "partition_num",
            cnName = "分区数量",
            defaultValue = "4",
            description = "分区数量，仅支持正整数",
            placeHolder = "请输入分区数量",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer partition_num;

    // 其他配置
    @StField(
            tag = "其他配置",
            order = 1,
            required = false,
            enName = "properties",
            cnName = "连接参数",
            description = "额外的连接配置参数，格式：key1=value1;key2=value2",
            placeHolder = "useSSL=false;characterEncoding=utf8",
            formType = StField.FormType.TEXT_AREA
    )
    private String properties;

    @Override
    protected boolean doCheckConnection() {
        return true;
    }

    @Override
    public JSONObject buildTaskConfig(JSONObject connectionConfig, Long datasourceId) {
        JSONObject config = new JSONObject();
        putIfNotEmpty(config, "url", connectionConfig.getString("url"));
        config.put("driver", "com.mysql.cj.jdbc.Driver");
        putIfNotEmpty(config, "user", connectionConfig.getString("username"));
        putIfNotEmpty(config, "password", connectionConfig.getString("password"));
        putIfNotEmpty(config, "connection_check_timeout_sec", connectionConfig.getString("connection_check_timeout_sec"));
        putIfNotEmpty(config, "partition_column", connectionConfig.getString("partition_column"));
        putIfNotEmpty(config, "partition_lower_bound", connectionConfig.getString("partition_lower_bound"));
        putIfNotEmpty(config, "partition_upper_bound", connectionConfig.getString("partition_upper_bound"));
        putIfNotEmpty(config, "partition_num", connectionConfig.getString("partition_num"));
        putIfNotEmpty(config, "fetch_size", connectionConfig.getString("fetch_size"));
        putIfNotEmpty(config, "properties", connectionConfig.getString("properties"));
        String whereCondition = connectionConfig.getString("where_condition");
        putIfNotEmpty(config, "where_condition", whereCondition);
        putIfNotEmpty(config, "split.size", connectionConfig.getString("split_size"));
        putIfNotEmpty(config, "split.even-distribution.factor.lower-bound", connectionConfig.getString("split.even-distribution.factor.lower-bound"));
        putIfNotEmpty(config, "split.even-distribution.factor.upper-bound", connectionConfig.getString("split.even-distribution.factor.upper-bound"));
        putIfNotEmpty(config, "split.sample-sharding.threshold", connectionConfig.getString("split.sample-sharding.threshold"));
        putIfNotEmpty(config, "split.inverse-sampling.rate", connectionConfig.getString("split.inverse-sampling.rate"));
        putIfNotEmpty(config, "int_type_narrowing", connectionConfig.getBoolean("int_type_narrowing"));
        addTableList(config, connectionConfig, whereCondition);
        return config;
    }
}
