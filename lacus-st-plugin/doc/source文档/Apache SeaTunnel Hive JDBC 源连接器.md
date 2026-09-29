Apache SeaTunnel Hive JDBC Connector 是一个用于通过 JDBC 读取外部数据源数据的连接器。它支持多种数据源，包括 Hive，并且可以在 Spark、Flink 和 SeaTunnel Zeta 等多种计算引擎上运行。以下是关于该连接器的详细说明：

### 支持的 Hive 版本
- 确定支持 3.1.3 和 3.1.2，其他版本需要测试。

### 支持的引擎
- Spark
- Flink
- SeaTunnel Zeta

### 关键特性
- 批处理
- 流处理
- 精确一次
- 列投影
- 并行性
- 支持用户自定义 split
- 支持查询 SQL，可以实现投影效果

### 支持的数据源信息
- 数据源：Hive
- 支持的版本：不同的依赖版本有不同的驱动程序类。
- 驱动：`org.apache.hive.jdbc.HiveDriver`
- 连接串：`jdbc:hive2://localhost:10000/default`
- Maven：Download

### 数据库相关性
- 请下载“Maven”对应的支持列表，并将其复制到 `$SEATUNNEL_HOME/plugins/jdbc/lib/` 工作目录。
- 例如，Hive数据源：`cp Hive-jdbc-xxx.jar $SEATUNNEL_HOME/plugins/jdbc/lib/`

### 数据类型映射
| Hive 数据类型    | SeaTunnel 数据类型 |
| ---------------- | ------------------ |
| BOOLEAN          | BOOLEAN            |
| TINYINT          | -                  |
| SMALLINT         | SHORT              |
| INT              | INT                |
| INTEGER          | INT                |
| BIGINT           | LONG               |
| FLOAT            | FLOAT              |
| DOUBLE           | DOUBLE             |
| DOUBLE PRECISION | DOUBLE             |
| DECIMAL(x,y)     | DECIMAL(x,y)       |
| NUMERIC(x,y)     | DECIMAL(x,y)       |
| CHAR             | -                  |
| VARCHAR          | STRING             |
| STRING           | STRING             |
| DATE             | DATE               |
| DATETIME         | DATETIME           |
| TIMESTAMP        | TIMESTAMP          |
| BINARY           | -                  |
| ARRAY            | -                  |
| INTERVAL         | -                  |
| MAP              | -                  |
| STRUCT           | -                  |
| UNIONTYPE        | Not supported yet  |

### 源配置项
- **url**：String，是，JDBC连接的URL。参考示例: `jdbc:hive2://localhost:10000/default`
- **driver**：String，是，用于连接到远程数据源的jdbc类名，如果使用Hive，则值为 `org.apache.hive.jdbc.HiveDriver`.
- **user**：String，否，连接实例用户名
- **password**：String，否，连接实例密码
- **query**：String，是，查询sql
- **connection_check_timeout_sec**：Int，否，30，等待用于验证连接的数据库操作完成的时间（秒）
- **partition_column**：String，否，-，并行分区的列名，只支持数值类型，只支持数字类型主键，只能配置一列。
- **partition_lower_bound**：BigDecimal，否，-，扫描的分区列最小值，如果未设置，SeaTunnel将查询数据库获取最小值。
- **partition_upper_bound**：BigDecimal，否，-，扫描的分区列最大值，如果没有设置，SeaTunnel将查询数据库获取最大值。
- **partition_num**：Int，否，job parallelism，分区数量，仅支持正整数。 默认值是作业并行数
- **fetch_size**：Int，否，0，对于返回大量对象的查询，您可以配置查询中使用的行提取大小，通过减少满足选择条件所需的数据库查询次数来提高性能。0表示使用jdbc默认值。
- **common-options**：否，-，源插件常用参数，请参考 源通用选项 详见
- **use_kerberos**：Boolean，否，no，是否启用Kerberos，默认值为false
- **kerberos_principal**：String，否，-，使用kerberos时，我们应该设置kerberos主体，例如“test_user@xxx”。
- **kerberos_keytab_path**：String，否，-，使用kerberos时，我们应该设置kerberos主体文件路径，如“/home/test/test_user.keytab”。
- **krb5_path**：String，否，/etc/krb5.conf，使用kerberos时，我们应该设置krb5路径文件路径，如“/seatunnel/krb5.conf”，或使用默认路径“/etc/krb5.conf”。

### 提示
- 如果未设置 `partition_column`，它将以单并发运行，如果设置了 `partition_column`，它将根据任务的并发性并行执行。
- 当您的分片读取字段是 bigint（及以上）等大数字类型并且数据分布不均匀时，建议将并行级别设置为 1，以确保 数据倾斜问题已得到解决。

### 任务示例
#### 简单任务
此示例以单并行方式查询测试数据库中表 `type_bin` 的 16 条数据，并查询其所有字段。您还可以指定要查询哪些字段以将最终输出到控制台。

```plaintext
# 定义运行时环境
env {
 parallelism = 2
 job.mode = "BATCH"
}
source {
 Jdbc {
 url = "jdbc:hive2://localhost:10000/default"
 driver = "org.apache.hive.jdbc.HiveDriver"
 connection_check_timeout_sec = 100
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
```

#### 并行任务
与您配置的分片字段和分片数据并行读取查询表。如果您想读取整个表，可以这样做。

```plaintext
source {
 Jdbc {
 url = "jdbc:hive2://localhost:10000/default"
 driver = "org.apache.hive.jdbc.HiveDriver"
 connection_check_timeout_sec = 100
 # Define query logic as required
 query = "select * from type_bin"
 # Parallel sharding reads fields
 partition_column = "id"
 # Number of fragments
 partition_num = 10
 }
}
```

#### 并行度临界值
指定并行度的值在分区字段的值上下界之间，这样可以更高效的读取数据。

```plaintext
source {
 Jdbc {
 url = "jdbc:hive2://localhost:10000/default"
 driver = "org.apache.hive.jdbc.HiveDriver"
 connection_check_timeout_sec = 100
 # Define query logic as required
 query = "select * from type_bin"
 partition_column = "id"
 # Read start boundary
 partition_lower_bound = 1
 # Read end boundary
 partition_upper_bound = 500
 partition_num = 10
 }
}
```

以上是 Apache SeaTunnel Hive JDBC Connector 的详细说明，希望对您有所帮助。