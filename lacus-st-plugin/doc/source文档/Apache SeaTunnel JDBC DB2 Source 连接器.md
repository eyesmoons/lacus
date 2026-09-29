根据您提供的链接内容，以下是对 Apache SeaTunnel 连接 DB2 数据源的说明文档：

### Apache SeaTunnel JDBC DB2 Source 连接器

#### 简介
Apache SeaTunnel 的 JDBC DB2 Source 连接器是一个用于通过 JDBC 读取外部数据源数据的连接器。它支持 Spark、Flink 和 SeaTunnel Zeta 等多种引擎。

#### 支持的引擎
- Spark
- Flink
- SeaTunnel Zeta

#### 描述
通过 JDBC 读取外部数据源数据。适用于需要从 DB2 数据库中读取数据的场景。

#### 使用依赖关系
- **适用于 Spark/Flink 引擎**：您需要确保 jdbc 驱动程序 jar 包已放置在目录 `${SEATUNNEL_HOME}/plugins/` 中。
- **适用于 SeaTunnel Zeta 引擎**：您需要确保 jdbc 驱动程序 jar 包已放置在目录 “`${SEATUNNEL_HOME}/lib/`” 中。

#### 关键特性
- 批处理
- 流处理
- 精确一次
- 列映射
- 并行度
- 支持用户自定义拆分
- 支持查询 SQL，可以实现映射效果

#### 支持的数据源信息
| 数据源 | 支持版本                           | 驱动                             | URL                                 | Maven |
| ------ | ---------------------------------- | -------------------------------- | ----------------------------------- | ----- |
| DB2    | 不同的依赖版本有不同的驱动程序类。 | `com.ibm.db2.jdbc.app.DB2Driver` | `jdbc:db2://127.0.0.1:50000/dbname` | 下载  |

#### 数据库相关性
请下载“Maven”对应的支持列表，并将其复制到 “`${SEATUNNEL_HOME}/plugins/jdbc/lib/`” 工作目录。例如，DB2 数据源：`cp DB2-connector-java-xxx.jar ${SEATUNNEL_HOME}/plugins/jdbc/lib/`

#### 数据类型映射
| DB2 数据类型                       | SeaTunnel 数据类型 |
| ---------------------------------- | ------------------ |
| BOOLEAN                            | BOOLEAN            |
| SMALLINT                           | SHORT              |
| INT, INTEGER                       | INTEGER            |
| BIGINT                             | LONG               |
| DECIMAL, NUMERIC                   | DECIMAL(38,18)     |
| REAL, FLOAT                        | FLOAT              |
| DOUBLE, DOUBLE PRECISION, DECFLOAT | DOUBLE             |
| CHAR, VARCHAR, LONG VARCHAR, CLOB  | STRING             |
| BLOB                               | BYTES              |
| DATE                               | DATE               |
| TIME                               | TIME               |
| TIMESTAMP                          | TIMESTAMP          |
| ROWID                              |                    |
| XML                                | Not supported yet  |

#### 源选项
| 名称                         | 类型       | 必需 | 默认值          | 描述                                                         |
| ---------------------------- | ---------- | ---- | --------------- | ------------------------------------------------------------ |
| url                          | String     | 是   | -               | JDBC 连接的 URL。参考案例：`jdbc:db2://127.0.0.1:50000/dbname` |
| driver                       | String     | 是   | -               | 用于连接到远程数据源的 jdbc 类名，如果使用 db2，则值为 `com.ibm.db2.jdbc.app.DB2Driver` |
| user                         | String     | 否   | -               | 连接实例用户名                                               |
| password                     | String     | 否   | -               | 连接实例密码                                                 |
| query                        | String     | 是   | -               | 查询语句                                                     |
| connection_check_timeout_sec | Int        | 否   | 30              | 等待用于验证连接的数据库操作完成的时间（秒）                 |
| partition_column             | String     | 否   | -               | 并行分区的列名，只支持数值类型，只支持数字类型主键，只能配置一列。 |
| partition_lower_bound        | BigDecimal | 否   | -               | 扫描的 partition_column 最小值，如果未设置，SeaTunnel 将查询数据库获取最小值。 |
| partition_upper_bound        | BigDecimal | 否   | -               | 扫描的 partition_column 最大值，如果没有设置，SeaTunnel 将查询数据库获取最大值。 |
| partition_num                | Int        | 否   | job parallelism | 分区计数的数量，只支持正整数。默认值是作业并行性             |
| fetch_size                   | Int        | 否   | 0               | 对于返回大量对象的查询，您可以配置查询中使用的行提取大小，通过减少满足选择条件所需的数据库请求次数来提高性能。0 表示使用 jdbc 默认值。 |
| properties                   | Map        | 否   | -               | 其他连接配置参数，当属性和 URL 具有相同的参数时，优先级由驱动程序的特定实现决定。例如，在 MySQL 中，属性优先于 URL。 |
| common-options               | 否         | -    | -               | source 插件常用参数，详见 [Source common Options]（../source-common-options.md） |

#### 小贴士
- 如果未设置 `partition_column`，它将以单并发运行，如果设置了 `partition_column`，它将根据任务的并发度并行执行。

#### 任务示例
##### 简单
此示例以单并行方式在您的测试“database”中查询类型容器（type_bin）'table'的16条数据。并查询其所有字段。您还可以指定要查询哪些字段以将最终输出到控制台。

```plaintext
# 定义运行时环境
env {
  parallelism = 2
  job.mode = "BATCH"
}

source {
  Jdbc {
    url = "jdbc:db2://127.0.0.1:50000/dbname"
    driver = "com.ibm.db2.jdbc.app.DB2Driver"
    connection_check_timeout_sec = 100
    user = "root"
    password = "123456"
    query = "select * from table_xxx"
  }
}

transform {
  # 如果你想了解更多关于如何配置seatunnel的信息，并查看transform插件的完整列表,
  # 请前往 https://seatunnel.apache.org/docs/transform-v2/sql
}

sink {
  Console {}
}
```

##### 并行
并行读取您的查询表，利用您配置的分片字段以及分片数据。若您希望读取整个表，您可以采取此操作。

```plaintext
source {
  Jdbc {
    url = "jdbc:db2://127.0.0.1:50000/dbname"
    driver = "com.ibm.db2.jdbc.app.DB2Driver"
    connection_check_timeout_sec = 100
    user = "root"
    password = "123456"
    # 根据需要定义查询逻辑
    query = "select * from type_bin"
    # 并行分片读取字段
    partition_column = "id"
    # 碎片数量
    partition_num = 10
  }
}
```

##### 并行的同时指定边界
在查询的上下界范围内指定数据更为高效。根据您配置的上下边界读取数据源，效率更佳。

```plaintext
source {
  Jdbc {
    url = "jdbc:db2://127.0.0.1:50000/dbname"
    driver = "com.ibm.db2.jdbc.app.DB2Driver"
    connection_check_timeout_sec = 100
    user = "root"
    password = "123456"
    # 根据需求定义查询逻辑
    query = "select * from type_bin"
    partition_column = "id"
    # 读取起始边界
    partition_lower_bound = 1
    # 读取结束边界
    partition_upper_bound = 500
    partition_num = 10
  }
}
```

#### 变更日志
请参考官方文档中的 Change Log 部分。

#### 社区与支持
- Apache SeaTunnel 是一个在 Apache 软件基金会（ASF）孵化中的项目，由 Apache Incubator 赞助。
- 您可以通过以下链接获取更多信息：
  - [Apache SeaTunnel 官方文档](https://seatunnel.apache.org/)
  - [GitHub](https://github.com/apache/seatunnel)
  - [Issue Tracker](https://issues.apache.org/jira/browse/SEATUNNEL)
  - [Pull Requests](https://github.com/apache/seatunnel/pulls)
  - [订阅邮件组](https://lists.apache.org/list.html?listname=seatunnel-user)

#### 版权信息
Copyright © 2021-2022 The Apache Software Foundation. Apache SeaTunnel, SeaTunnel, and its feather logo are trademarks of The Apache Software Foundation.

希望这份说明文档对您有所帮助！如果您有任何其他问题，请随时提问。