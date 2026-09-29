根据您提供的链接内容，以下是整理的关于 Apache SeaTunnel Snowflake 连接器的说明文档：

---

### Apache SeaTunnel Snowflake 连接器说明文档

#### 简介
Apache SeaTunnel 是一个开源的数据集成工具，支持多种数据源和目标的数据处理。Snowflake 连接器是 SeaTunnel 的一部分，用于通过 JDBC 从 Snowflake 数据库中读取数据。

#### 支持的引擎
- Spark
- Flink
- SeaTunnel Zeta

#### 主要特性
- 批处理和流处理支持
- 精确一次（Exactly-once）语义
- 列投影
- 并行处理
- 支持用户自定义分区
- 支持查询 SQL 并实现投影效果

#### 描述
通过 JDBC 读取外部数据源的数据。

#### 支持的数据源列表
| 数据源    | 支持的版本               | 驱动                                      | URL                                                    | Maven |
| --------- | ------------------------ | ----------------------------------------- | ------------------------------------------------------ | ----- |
| snowflake | 不同依赖版本有不同驱动类 | net.snowflake.client.jdbc.SnowflakeDriver | jdbc:snowflake://<account_name>.snowflakecomputing.com | 下载  |

#### 数据库依赖
请下载与 'Maven' 相对应的支持列表，并将其复制到 `$SEATUNNEL_HOME/plugins/jdbc/lib/' 工作目录。例如 Snowflake 数据源：`cp snowflake-connector-java-xxx.jar $SEATUNNEL_HOME/plugins/jdbc/lib/`

#### 数据类型映射
| Snowflake 数据类型               | SeaTunnel 数据类型 |
| -------------------------------- | ------------------ |
| BOOLEAN                          | BOOLEAN            |
| TINYINT                          | SHORT_TYPE         |
| SMALLINT                         | SHORT_TYPE         |
| BYTEINT                          | SHORT_TYPE         |
| INT                              | INT                |
| INTEGER                          | INT                |
| BIGINT                           | LONG               |
| DECIMAL                          | DECIMAL(x,y)       |
| NUMERIC                          | DECIMAL(x,y)       |
| NUMBER                           | DECIMAL(x,y)       |
| REAL                             | FLOAT              |
| DOUBLE                           | DOUBLE             |
| CHAR                             | STRING             |
| VARCHAR                          | STRING             |
| TEXT                             | STRING             |
| VARIANT                          | STRING             |
| OBJECT                           | STRING             |
| DATE                             | DATE               |
| TIME                             | TIME               |
| DATETIME                         | TIMESTAMP          |
| TIMESTAMP_LTZ                    | TIMESTAMP          |
| TIMESTAMP_NTZ                    | TIMESTAMP          |
| TIMESTAMP_TZ                     | TIMESTAMP          |
| BINARY                           | BYTES              |
| VARBINARY                        | BYTES              |
| GEOGRAPHY (WKB or EWKB)          | BYTES              |
| GEOMETRY (WKB or EWKB)           | BYTES              |
| GEOGRAPHY (GeoJSON, WKT or EWKT) | STRING             |
| GEOMETRY (GeoJSON, WKB or EWKB)  | STRING             |

#### 选项
| 名称                         | 类型       | 是否必需 | 默认值          | 描述                                                         |
| ---------------------------- | ---------- | -------- | --------------- | ------------------------------------------------------------ |
| url                          | String     | 是       | -               | JDBC 连接的 URL。例如：jdbc:snowflake://<account_name>.snowflakecomputing.com |
| driver                       | String     | 是       | -               | 用于连接远程数据源的 JDBC 类名。如果使用 Snowflake，值为 `net.snowflake.client.jdbc.SnowflakeDriver` |
| user                         | String     | 否       | -               | 连接实例用户名                                               |
| password                     | String     | 否       | -               | 连接实例密码                                                 |
| query                        | String     | 是       | -               | 查询语句                                                     |
| connection_check_timeout_sec | Int        | 否       | 30              | 等待数据库操作完成以验证连接的秒数                           |
| partition_column             | String     | 否       | -               | 用于并行处理的分区列名，仅支持数值类型，仅支持数值类型主键，且只能配置一个列 |
| partition_lower_bound        | BigDecimal | 否       | -               | 扫描的分区_column 最小值，如果未设置，SeaTunnel 将查询数据库获取最小值 |
| partition_upper_bound        | BigDecimal | 否       | -               | 扫描的分区_column 最大值，如果未设置，SeaTunnel 将查询数据库获取最大值 |
| partition_num                | Int        | 否       | job parallelism | 分区计数，仅支持正整数。默认值为 job parallelism             |
| fetch_size                   | Int        | 否       | 0               | 对于返回大量对象的查询，可以配置查询中使用的行获取大小，以减少满足选择标准所需的数据库访问次数。零表示使用 jdbc 默认值 |
| properties                   | Map        | 否       | -               | 额外的连接配置参数。当 properties 和 URL 有相同的参数时，优先级由驱动实现决定。例如，在 MySQL 中，properties 优先于 URL |
| common-options               | No         | No       | -               | 源插件公共参数，请参考 Source Common Options 详细信息        |

#### 提示
- 如果未设置 `partition_column`，将运行在单并发中；如果设置了 `partition_column`，将根据任务的并发性并行执行。
- JDBC 驱动连接参数在 JDBC 连接字符串中受支持。例如，可以添加 `?GEOGRAPHY_OUTPUT_FORMAT='EWKT'` 来指定地理空间数据类型。有关可配置参数和地理空间数据类型的更多信息，请访问 Snowflake 官方文档。

#### 任务示例
##### simple
本示例在单并发中查询 `type_bin 'table' 16` 数据，并查询所有字段。您也可以指定要查询哪些字段以输出到控制台。

```plaintext
# Defining the runtime environment
env {
parallelism = 2
job.mode = "BATCH"
}
source {
Jdbc {
url = "jdbc:snowflake://<account_name>.snowflakecomputing.com"
driver = "net.snowflake.client.jdbc.SnowflakeDriver"
connection_check_timeout_sec = 100
user = "root"
password = "123456"
query = "select * from type_bin limit 16"
}
}
transform {
# If you would like to get more information about how to configure seatunnel and see full list of transform plugins,
# please go to https://seatunnel.apache.org/docs/transform-v2/sql
}
sink {
Console {}
}
}
```

##### parallel
使用配置的 shard 字段和 shard 数据并行读取查询表。如果您想读取整个表，可以这样做。

```plaintext
Jdbc {
url = "jdbc:snowflake://<account_name>.snowflakecomputing.com"
driver = "net.snowflake.client.jdbc.SnowflakeDriver"
connection_check_timeout_sec = 100
user = "root"
password = "123456"
# Define query logic as required
query = "select * from type_bin"
# Parallel sharding reads fields
partition_column = "id"
# Number of fragments
partition_num = 10
}
```

##### parallel boundary
指定查询的上下界中的数据，按配置的上下界读取数据源更高效。

```plaintext
Jdbc {
url = "jdbc:snowflake://<account_name>.snowflakecomputing.com"
driver = "net.snowflake.client.jdbc.SnowflakeDriver"
connection_check_timeout_sec = 100
user = "root"
password = "123456"
# Define query logic as required
query = "select * from type_bin"
partition_column = "id"
# Read start boundary
partition_lower_bound = 1
# Read end boundary
partition_upper_bound = 500
partition_num = 10
}
```

#### Changelog
Change Log

编辑此页

上一页

Sls

下一页

Socket

SeaTunnel

FAQ

版本

社区

GitHub

Issue Tracker

Pull Requests

订阅邮件组

How to Subscribe

订阅邮件

邮件归档

Apache SeaTunnel 是一个在 Apache 软件基金会（ASF）孵化器中的项目，由 Apache Incubator 赞助。孵化是所有新接受项目的必要过程，直到进一步的审查表明基础设施、通信和决策过程已经稳定，与其他成功的 ASF 项目一致。虽然孵化状态不一定反映代码的完整性或稳定性，但它表明该项目尚未得到 ASF 的完全认可。

版权所有 © 2021-2022 The Apache Software Foundation。Apache SeaTunnel、SeaTunnel 及其羽毛标志是 The Apache Software Foundation 的商标。

---

希望这份说明文档对您有所帮助！如果有任何进一步的问题，请随时提问。