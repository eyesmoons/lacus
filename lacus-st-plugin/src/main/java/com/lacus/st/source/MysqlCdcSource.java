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
        author = "lacus",
        connectorKey = "Mysql-CDC"
)
@StTag({
        @TagDefinition(name = "数据源配置", displayName = "数据源配置", order = 1, description = "数据源选择和连接配置"),
        @TagDefinition(name = "连接配置", displayName = "连接配置", order = 2, description = "连接配置"),
        @TagDefinition(name = "CDC配置", displayName = "CDC配置", order = 3, description = "CDC配置"),
        @TagDefinition(name = "快照配置", displayName = "快照配置", order = 4, description = "快照配置")
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

    // CDC配置
    @StField(
            tag = "连接配置",
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
            tag = "连接配置",
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
            tag = "连接配置",
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
            tag = "连接配置",
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

    @StField(
            tag = "连接配置",
            order = 5,
            required = false,
            enName = "connection_pool_size",
            cnName = "jdbc连接池大小",
            defaultValue = "20",
            description = "jdbc连接池大小",
            placeHolder = "jdbc连接池大小",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer connection_pool_size;

    // CDC配置
    @StField(
            tag = "CDC配置",
            order = 1,
            required = false,
            enName = "startup_mode",
            cnName = "快照模式",
            defaultValue = "initial",
            description = "快照模式：initial(初始快照), when_needed(需要时), never(从不), schema_only(仅模式)",
            placeHolder = "initial",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"initial", "earliest", "latest", "specific", "timestamp"}
    )
    private String startup_mode;

    @StField(
            tag = "CDC配置",
            order = 2,
            required = false,
            enName = "startup_specific_offset_file",
            cnName = "指定binlog文件",
            description = "从指定的binlog日志文件名开始. 注意, 当使用 startup.mode 选项为 specific 时，此选项为必填项.",
            placeHolder = "指定binlog文件",
            formType = StField.FormType.TEXT
    )
    private Integer startup_specific_offset_file;

    @StField(
            tag = "CDC配置",
            order = 3,
            required = false,
            enName = "startup_specific_offset_pos",
            cnName = "指定binlog位置",
            description = "从指定的binlog日志文件位置开始. 注意, 当使用 startup.mode 选项为 specific 时，此选项为必填项.",
            placeHolder = "指定binlog位置",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer startup_specific_offset_pos;

    @StField(
            tag = "CDC配置",
            order = 4,
            required = false,
            enName = "startup_timestamp",
            cnName = "指定时间戳",
            description = "从指定的binlog时间戳文件位置开始. 注意, 当使用 startup.mode 选项为 timestamp 时，此选项为必填项.",
            placeHolder = "指定时间戳",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer startup_timestamp;

    @StField(
            tag = "CDC配置",
            order = 5,
            required = false,
            enName = "stop_mode",
            cnName = "MySQL CDC停止模式",
            description = "MySQL CDC 消费者的可选停止模式",
            placeHolder = "MySQL CDC停止模式",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"never", "latest", "specific"}
    )
    private Integer stop_mode;

    @StField(
            tag = "CDC配置",
            order = 6,
            required = false,
            enName = "stop_specific_offset_file",
            cnName = "指定binlog停止文件",
            description = "从指定的binlog日志文件名停止. 注意, 当使用 stop.mode 选项为 specific 时，此选项为必填项.",
            placeHolder = "指定binlog停止文件",
            formType = StField.FormType.TEXT
    )
    private Integer stop_specific_offset_file;

    @StField(
            tag = "CDC配置",
            order = 7,
            required = false,
            enName = "stop_specific_offset_pos",
            cnName = "指定binlog停止位置",
            description = "从指定的binlog日志文件位置停止. 注意, 当使用 stop.mode 选项为 specific 时，此选项为必填项.",
            placeHolder = "指定binlog停止位置",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer stop_specific_offset_pos;

    @StField(
            tag = "CDC配置",
            order = 8,
            required = false,
            enName = "exactly_once",
            cnName = "启用精确一次语义",
            description = "启用精确一次语义.",
            placeHolder = "启用精确一次语义",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"true", "false"},
            defaultValue = "false"
    )
    private Integer exactly_once;

    @StField(
            tag = "CDC配置",
            order = 9,
            required = false,
            enName = "format",
            cnName = "MySQL CDC 的可选输出格式",
            description = "MySQL CDC 的可选输出格式, 有效的枚举值为 DEFAULT、COMPATIBLE_DEBEZIUM_JSON.",
            placeHolder = "MySQL CDC 的可选输出格式",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"DEFAULT", "COMPATIBLE_DEBEZIUM_JSON"},
            defaultValue = "DEFAULT"
    )
    private Integer format;

    @StField(
            tag = "CDC配置",
            order = 10,
            required = false,
            enName = "schema_changes_enabled",
            cnName = "启用模式演进",
            description = "模式演进默认是禁用的. 当前我们只支持 add column、drop column、rename column 和 modify column..",
            placeHolder = "启用模式演进",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"true", "false"},
            defaultValue = "false"
    )
    private Integer schema_changes_enabled;

    @StField(
            tag = "CDC配置",
            order = 11,
            required = false,
            enName = "debezium",
            cnName = "debezium配置",
            description = "传递 Debezium的属性 给Debezium嵌入式引擎, 该引擎用于捕获 MySQL 服务的数据变更.",
            placeHolder = "debezium配置",
            formType = StField.FormType.TEXT
    )
    private Integer debezium;

    @StField(
            tag = "CDC配置",
            order = 12,
            required = false,
            enName = "int_type_narrowing",
            cnName = "Int类型收窄",
            description = "Int类型收窄，如果为 true，则 tinyint(1) 类型将被收窄为 boolean 类型（如果没有精度损失）。目前仅支持 MySQL。",
            placeHolder = "Int类型收窄",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"true", "false"},
            defaultValue = "true"
    )
    private Integer int_type_narrowing;

    @StField(
            tag = "快照配置",
            order = 1,
            required = false,
            enName = "snapshot_split_size",
            cnName = "快照分片大小",
            defaultValue = "8096",
            description = "表快照的分割大小（行数）,读取表的快照时,被捕获的表会被分割成多个分割块.",
            placeHolder = "快照分片大小",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer snapshot_split_size;

    @StField(
            tag = "快照配置",
            order = 2,
            required = false,
            enName = "snapshot_fetch_size",
            cnName = "快照拉取数据量",
            defaultValue = "1024",
            description = "每次轮询读取表快照时的最大获取大小.",
            placeHolder = "快照拉取数据量",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer snapshot_fetch_size;

    @StField(
            tag = "块键配置",
            order = 1,
            required = false,
            enName = "chunk_key_even_distribution_factor_upper_bound",
            cnName = "块键分布因子的上限",
            defaultValue = "100",
            description = "块键分布因子的上限. 该因子用于确定表数据是否分布均匀. 如果分布式因子计算结果小于或等于此上限 (即., (MAX(id) - MIN(id) + 1) / row count), 表的分块将被优化以实现均匀分布. 否则, 如果分布因子大于此上限, 该表将被视为分布不均, 并且如果估计的分片数量超过 sample-sharding.threshold 所指定的值, 则将使用基于采样的分片策略. 默认值是100.0.",
            placeHolder = "块键分布因子的上限",
            formType = StField.FormType.TEXT
    )
    private Integer chunk_key_even_distribution_factor_upper_bound;

    @StField(
            tag = "块键配置",
            order = 2,
            required = false,
            enName = "chunk_key_even_distribution_factor_lower_bound",
            cnName = "块键分布因子的下限",
            defaultValue = "0.05",
            description = "块键分布因子的下限. 该因子用于确定表数据是否分布均匀. 如果计算得出的分布因子大于或等于此下限 (即., (MAX(id) - MIN(id) + 1) / row count), 表的分块将被优化以实现均匀分布. 否则, 如果分布因子小于此下限, 该表将被视为分布不均, 并且如果预估的分片数量超过了 sample-sharding.threshold 所指定的值，则将使用基于采样的分片策略. 默认值是 0.05.",
            placeHolder = "块键分布因子的下限",
            formType = StField.FormType.TEXT
    )
    private Integer chunk_key_even_distribution_factor_lower_bound;

    @StField(
            tag = "块键配置",
            order = 3,
            required = false,
            enName = "sample_sharding_threshold",
            cnName = "采样分片阈值",
            defaultValue = "1000",
            description = "此配置指定了触发采样分片策略的预估分片数量阈值. 当分配因子超出由 chunk-key.even-distribution.factor.upper-bound 和 chunk-key.even-distribution.factor.lower-bound 所指定的范围时, 如果估计的分片数量 (按近似行数/块大小 计算) 超过此阈值, 则将使用样本分片策略. 这有助于更高效地处理大型数据集. 默认值为 1000 分片.",
            placeHolder = "采样分片阈值",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer sample_sharding_threshold;

    @StField(
            tag = "块键配置",
            order = 4,
            required = false,
            enName = "inverse_sampling_rate",
            cnName = "采样率的倒数",
            defaultValue = "1000",
            description = "采样分片策略中使用的采样率的倒数. 例如, 如果该值设置为 1000, 则表示在采样过程中应用了 1/1000 的采样率. 此选项在控制采样的粒度方面提供了灵活性, 从而影响最终的分片数量. 在处理非常大的数据集时非常有用, 因为此时更倾向于使用较低的采样率. 默认值为 1000.",
            placeHolder = "采样率的倒数",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer inverse_sampling_rate;

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
        putIfNotEmpty(config, "table-names", getDbTables(connectionConfig));
        putIfNotEmpty(config, "startup.mode", connectionConfig.getString("startup_mode"));
        putIfNotEmpty(config, "startup.specific-offset.file", connectionConfig.getString("startup_specific_offset_file"));
        putIfNotEmpty(config, "startup.specific-offset.pos", connectionConfig.getLong("startup_specific_offset_pos"));
        putIfNotEmpty(config, "startup.timestamp", connectionConfig.getLong("startup_timestamp"));
        putIfNotEmpty(config, "stop.mode", connectionConfig.getString("stop_mode"));
        putIfNotEmpty(config, "stop.specific-offset.file", connectionConfig.getString("stop_specific_offset_file"));
        putIfNotEmpty(config, "stop.specific-offset.pos", connectionConfig.getLong("stop_specific_offset_pos"));
        putIfNotEmpty(config, "snapshot.split.size", connectionConfig.getInteger("snapshot_split_size"));
        putIfNotEmpty(config, "snapshot.fetch.size", connectionConfig.getInteger("snapshot_fetch_size"));
        putIfNotEmpty(config, "server-id", connectionConfig.getString("server_id"));
        putIfNotEmpty(config, "server-time-zone", connectionConfig.getString("server_time_zone"));
        putIfNotEmpty(config, "connect.timeout.ms", connectionConfig.getString("connect_timeout_ms"));
        putIfNotEmpty(config, "connect.max-retries", connectionConfig.getInteger("connect_max_retries"));
        putIfNotEmpty(config, "connection.pool.size", connectionConfig.getInteger("connection_pool_size"));
        putIfNotEmpty(config, "chunk-key.even-distribution.factor.upper-bound", connectionConfig.getDouble("chunk_key_even_distribution_factor_upper_bound"));
        putIfNotEmpty(config, "chunk-key.even-distribution.factor.lower-bound", connectionConfig.getDouble("chunk_key_even_distribution_factor_lower_bound"));
        putIfNotEmpty(config, "sample-sharding.threshold", connectionConfig.getInteger("sample_sharding_threshold"));
        putIfNotEmpty(config, "inverse-sampling.rate", connectionConfig.getInteger("inverse_sampling_rate"));
        putIfNotEmpty(config, "exactly_once", connectionConfig.getBoolean("exactly_once"));
        putIfNotEmpty(config, "format", connectionConfig.getString("format"));
        putIfNotEmpty(config, "schema-changes.enabled", connectionConfig.getBoolean("schema_changes_enabled"));
        putIfNotEmpty(config, "debezium", connectionConfig.getString("debezium"));
        putIfNotEmpty(config, "int_type_narrowing", connectionConfig.getBoolean("int_type_narrowing"));
        return config;
    }
}
