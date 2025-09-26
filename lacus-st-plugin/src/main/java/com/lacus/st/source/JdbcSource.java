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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        author = "lacus"
)
@StTag({
        @TagDefinition(name = "数据源配置", displayName = "数据源配置", order = 1, description = "数据源选择和连接配置"),
        @TagDefinition(name = "查询配置", displayName = "查询配置", order = 2, description = "数据查询相关配置"),
        @TagDefinition(name = "性能配置", displayName = "性能配置", order = 3, description = "性能优化相关配置"),
        @TagDefinition(name = "其他配置", displayName = "其他配置", order = 4, description = "其他扩展配置")
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
            enName = "table_list",
            cnName = "表名",
            placeHolder = "请选择数据表",
            formType = StField.FormType.MULTI_SELECT,
            dictType = StField.DictType.URL,
            dictUrl = "/metadata/table/listTable"
    )
    private String tableList;

    // 连接器配置
    @StField(
            tag = "连接器配置",
            order = 4,
            required = true,
            enName = "query",
            cnName = "查询语句",
            placeHolder = "请输入查询语句",
            formType = StField.FormType.TEXT_AREA
    )
    private String query;

    @StField(
            tag = "连接器配置",
            order = 5,
            required = false,
            enName = "connection_check_timeout_sec",
            cnName = "连接检查超时时间(秒)",
            defaultValue = "30",
            description = "验证数据库连接所使用的操作完成的等待时间（秒）。",
            placeHolder = "请输入连接检查超时时间",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer connectionCheckTimeoutSec;

    @StField(
            tag = "连接器配置",
            order = 6,
            required = false,
            enName = "partition_column",
            cnName = "分区列",
            description = "用于并行度分区的列名，仅支持数字类型，仅支持数字类型的主键，并且只能配置一列。",
            placeHolder = "请输入分区列",
            formType = StField.FormType.TEXT
    )
    private String partitionColumn;

    @StField(
            tag = "连接器配置",
            order = 7,
            required = false,
            enName = "partition_lower_bound",
            cnName = "分区下界",
            description = "扫描时 partition_column 的最小值，如果未设置，SeaTunnel 将查询数据库以获取最小值。",
            placeHolder = "请输入分区下界",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private String partitionLowerBound;

    @StField(
            tag = "连接器配置",
            order = 8,
            required = false,
            enName = "partition_upper_bound",
            cnName = "分区上界",
            placeHolder = "请输入分区上界",
            formType = StField.FormType.POSITIVE_NUMBER,
            description = "扫描时 partition_column 的最大值，如果未设置，SeaTunnel 将查询数据库以获取最大值。"
    )
    private String partitionUpperBound;

    @StField(
            tag = "连接器配置",
            order = 9,
            required = false,
            enName = "partition_num",
            cnName = "作业并行度",
            description = "分区数量，仅支持正整数。默认值为作业并行度。",
            placeHolder = "请输入作业并行度",
            formType = StField.FormType.POSITIVE_NUMBER,
            defaultValue = "1000"
    )
    private String partitionNum;

    @StField(
            tag = "连接器配置",
            order = 10,
            required = false,
            enName = "fetch_size",
            cnName = "数据拉取大小",
            placeHolder = "请输入排序字段，例如：id DESC",
            formType = StField.FormType.POSITIVE_NUMBER,
            defaultValue = "0",
            description = "对于返回大量对象的查询， 可以通过配置查询中使用的行获取大小(row fetch size)来提高性能， 这样可以减少满足选择条件所需的数据库访问次数。 " +
                    "值为零表示使用JDBC的值为零表示使用JDBC的默认值。"
    )
    private String fetchSize;

    @StField(
            tag = "连接器配置",
            order = 11,
            required = false,
            enName = "properties",
            cnName = "连接参数",
            description = "额外的连接配置参数，当属性和 URL 中有相同的参数时，优先级由驱动程序的具体实现决定。例如，在 MySQL 中，属性优先于 URL。",
            placeHolder = "请输入连接参数",
            formType = StField.FormType.TEXT_AREA
    )
    private String properties;

    @StField(
            tag = "连接器配置",
            order = 12,
            required = false,
            enName = "table_path",
            cnName = "表的完整路径",
            description = "表的完整路径，您可以使用此配置代替 query。\n" +
                    "示例：\n" +
                    "mysql: \"testdb.table1\"\n" +
                    "oracle: \"test_schema.table1\"\n" +
                    "sqlserver: \"testdb.test_schema.table1\"\n" +
                    "postgresql: \"testdb.test_schema.table1\"",
            placeHolder = "请输入表的完整路径",
            formType = StField.FormType.TEXT_AREA
    )
    private String tablePath;

    @StField(
            tag = "连接器配置",
            order = 13,
            required = false,
            enName = "where_condition",
            cnName = "where条件",
            placeHolder = "请输入where条件",
            description = "所有表/查询的通用行过滤条件，必须以 where 开头。例如 where id > 100。",
            formType = StField.FormType.TEXT_AREA
    )
    private String whereCondition;

    @StField(
            tag = "连接器配置",
            order = 14,
            required = false,
            enName = "split.size",
            cnName = "表的分片大小",
            description = "表的分割大小（行数），当读取表时，捕获的表会被分割成多个分片。",
            placeHolder = "请输入表的分片大小",
            defaultValue = "8096",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer splitSize;

    @StField(
            tag = "连接器配置",
            order = 15,
            required = false,
            enName = "split.even-distribution.factor.lower-bound",
            cnName = "分片键分布因子的下限",
            description = "分片键分布因子的下限。该因子用于判断表数据的分布是否均匀。如果计算得到的分布因子大于或等于该下限（即，(MAX(id) - MIN(id) + 1) / 行数），则会对表的分片进行优化，以确保数据的均匀分布。反之，如果分布因子较低，则表数据将被视为分布不均匀。如果估算的分片数量超过 sample-sharding.threshold 所指定的值，则会采用基于采样的分片策略。默认值为 0.05。",
            placeHolder = "请输入分片键分布因子的下限",
            defaultValue = "0.05",
            formType = StField.FormType.NUMBER
    )
    private Integer splitEvenDistributionFactorLowerBound;

    @StField(
            tag = "连接器配置",
            order = 16,
            required = false,
            enName = "split.even-distribution.factor.upper-bound",
            cnName = "分片键分布因子的上线",
            description = "分片键分布因子的上限。该因子用于判断表数据的分布是否均匀。如果计算得到的分布因子小于或等于该上限（即，(MAX(id) - MIN(id) + 1) / 行数），则会对表的分片进行优化，以确保数据的均匀分布。反之，如果分布因子较大，则表数据将被视为分布不均匀，并且如果估算的分片数量超过 sample-sharding.threshold 所指定的值，则会采用基于采样的分片策略。默认值为 100.0。",
            placeHolder = "请输入分片键分布因子的上线",
            defaultValue = "100",
            formType = StField.FormType.NUMBER
    )
    private Integer splitEvenDistributionFactorUpperBound;

    @StField(
            tag = "连接器配置",
            order = 17,
            required = false,
            enName = "split.sample-sharding.threshold",
            cnName = "样本分片阈值",
            description = "此配置指定了触发样本分片策略的估算分片数阈值。当分布因子超出由 chunk-key.even-distribution.factor.upper-bound 和 chunk-key.even-distribution.factor.lower-bound 指定的范围，并且估算的分片数量（计算方法为大致行数 / 分片大小）超过此阈值时，将使用样本分片策略。此配置有助于更高效地处理大型数据集。默认值为 1000 个分片。",
            placeHolder = "请输入样本分片阈值",
            defaultValue = "10000",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer splitSampleShardingThreshold;

    @StField(
            tag = "连接器配置",
            order = 18,
            required = false,
            enName = "split.inverse-sampling.rate",
            cnName = "逆采样率",
            description = "样本分片策略中使用的采样率的倒数。例如，如果该值设置为 1000，则表示在采样过程中应用 1/1000 的采样率。此选项提供了灵活性，可以控制采样的粒度，从而影响最终的分片数量。特别适用于处理非常大的数据集，在这种情况下通常会选择较低的采样率。默认值为 1000。",
            placeHolder = "请输入逆采样率",
            defaultValue = "1000",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer splitInverseSamplingRate;

    // 内部变量
    private Connection connection;
    private PreparedStatement statement;
    private ResultSet resultSet;

    @Override
    protected boolean doCheckConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                return false;
            }

            // 执行简单查询测试连接
            PreparedStatement testStatement = connection.prepareStatement("SELECT 1");
            ResultSet testResult = testStatement.executeQuery();
            testResult.close();
            testStatement.close();
            return true;
        } catch (SQLException e) {
            log.error("检查MySQL连接失败", e);
            return false;
        }
    }
}
