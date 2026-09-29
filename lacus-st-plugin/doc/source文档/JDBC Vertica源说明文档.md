根据您提供的链接内容，以下是 Apache SeaTunnel 中 JDBC Vertica Source Connector 的详细说明和使用指南：

### 简介
Apache SeaTunnel 的 JDBC Vertica Source Connector 允许您通过 JDBC 读取外部数据源的数据。它支持以下引擎：
- Spark
- Flink
- SeaTunnel Zeta

### 依赖配置
#### Spark/Flink 引擎
您需要确保 JDBC 驱动 jar 包已放置在 `${SEATUNNEL_HOME}/plugins/` 目录下。

#### SeaTunnel Zeta 引擎
您需要确保 JDBC 驱动 jar 包已放置在 `${SEATUNNEL_HOME}/lib/` 目录下。

### 关键特性
- 批处理
- 流处理
- 精确一次性处理
- 列投影
- 并行处理
- 支持用户自定义拆分
- 支持查询 SQL 并实现投影效果

### 支持的数据源信息
| 数据源  | 支持的版本                 | 驱动                      | URL                                     | Maven |
| ------- | -------------------------- | ------------------------- | --------------------------------------- | ----- |
| Vertica | 不同依赖版本有不同的驱动类 | `com.vertica.jdbc.Driver` | `jdbc:vertica://localhost:5433/vertica` | 下载  |

### 数据类型映射
| Vertical Data Type                                           | SeaTunnel Data Type                                          |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| BIT                                                          | BOOLEAN                                                      |
| TINYINT                                                      | TINYINT                                                      |
| TINYINT UNSIGNED                                             | TINYINT UNSIGNED                                             |
| SMALLINT                                                     | SMALLINT                                                     |
| SMALLINT UNSIGNED                                            | SMALLINT UNSIGNED                                            |
| MEDIUMINT                                                    | MEDIUMINT                                                    |
| MEDIUMINT UNSIGNED                                           | MEDIUMINT UNSIGNED                                           |
| INT                                                          | INTEGER                                                      |
| YEAR                                                         | INT                                                          |
| INT UNSIGNED                                                 | INT UNSIGNED                                                 |
| INTEGER UNSIGNED                                             | LONG                                                         |
| BIGINT                                                       | LONG                                                         |
| BIGINT UNSIGNED                                              | DECIMAL(20,0)                                                |
| DECIMAL(x,y) (Get the designated column's specified column size.<38) | DECIMAL(x,y)                                                 |
| DECIMAL(x,y) (Get the designated column's specified column size.>38) | DECIMAL(38,18)                                               |
| DECIMAL UNSIGNED                                             | DECIMAL((Get the designated column's specified column size)+1, (Gets the designated column's number of digits to right of the decimal point.))) |
| FLOAT                                                        | FLOAT                                                        |
| FLOAT UNSIGNED                                               | FLOAT                                                        |
| DOUBLE                                                       | DOUBLE                                                       |
| DOUBLE UNSIGNED                                              | DOUBLE                                                       |
| CHAR                                                         | VARCHAR                                                      |
| VARCHAR                                                      | VARCHAR                                                      |
| TINYTEXT                                                     | VARCHAR                                                      |
| MEDIUMTEXT                                                   | TEXT                                                         |
| TEXT                                                         | TEXT                                                         |
| LONGTEXT                                                     | TEXT                                                         |
| JSON                                                         | STRING                                                       |
| DATE                                                         | DATE                                                         |
| TIME                                                         | TIME                                                         |
| DATETIME                                                     | TIMESTAMP                                                    |
| TIMESTAMP                                                    | TIMESTAMP                                                    |
| TINYBLOB                                                     | BYTES                                                        |
| MEDIUMBLOB                                                   | BYTES                                                        |
| BLOB                                                         | BYTES                                                        |
| LONGBLOB                                                     | BYTES                                                        |
| BINARY                                                       | VARBINARY                                                    |
| VARBINARY                                                    | VARBINARY                                                    |
| BIT(n)                                                       | BYTES                                                        |
| GEOMETRY                                                     | Not supported yet                                            |
| UNKNOWN                                                      | Not supported yet                                            |

### 源选项
| 名称                         | 类型       | 是否必需 | 默认值          | 描述                                                         |
| ---------------------------- | ---------- | -------- | --------------- | ------------------------------------------------------------ |
| url                          | String     | 是       | -               | JDBC 连接的 URL。参考示例：`jdbc:vertica://localhost:5433/vertica` |
| driver                       | String     | 是       | -               | 用于连接远程数据源的 JDBC 类名，如果使用 Vertica，值为 `com.vertica.jdbc.Driver` |
| user                         | String     | 否       | -               | 连接实例用户名                                               |
| password                     | String     | 否       | -               | 连接实例密码                                                 |
| query                        | String     | 是       | -               | 查询语句                                                     |
| connection_check_timeout_sec | Int        | 否       | 30              | 用于验证连接完成的数据库操作等待秒数                         |
| partition_column             | String     | 否       | -               | 并行处理的分区列名，仅支持数值类型，仅支持数值类型主键，且仅可配置一列 |
| partition_lower_bound        | BigDecimal | 否       | -               | 扫描的分区_column 最小值，如果未设置，SeaTunnel 将查询数据库获取最小值 |
| partition_upper_bound        | BigDecimal | 否       | -               | 扫描的分区_column 最大值，如果未设置，SeaTunnel 将查询数据库获取最大值 |
| partition_num                | Int        | 否       | job parallelism | 分区计数数量，仅支持正整数。默认值为 job parallelism         |
| fetch_size                   | Int        | 否       | 0               | 对于返回大量对象的查询，您可以配置查询中使用的行获取大小，以提高性能，减少满足选择标准所需的数据库访问次数。零表示使用 JDBC 默认值 |
| properties                   | Map        | 否       | -               | 额外的连接配置参数，当 properties 和 URL 有相同的参数时，优先级由驱动实现的具体决定。例如，在 MySQL 中，properties 优先于 URL |
| common-options               | No         | -        | -               | 源插件公共参数，请参考源公共选项的详细信息                   |

### 提示
- 如果未设置 partition_column，将运行在单并发中；如果设置了 partition_column，将根据任务的并发性并行执行。

### 任务示例
#### 简单示例
```sql
# 定义运行时环境
env {
  parallelism = 2
  job.mode = "BATCH"
}

source {
  Jdbc {
    url = "jdbc:vertica://localhost:5433/vertica"
    driver = "com.vertica.jdbc.Driver"
    connection_check_timeout_sec = 100
    user = "root"
    password = "123456"
    query = "select * from type_bin limit 16"
  }
}

transform {
  # 如果您想了解更多关于如何配置 SeaTunnel 以及查看转换插件的完整列表，请访问 https://seatunnel.apache.org/docs/transform-v2/sql
}

sink {
  Console {}
}
```

#### 并行示例
```sql
source {
  Jdbc {
    url = "jdbc:vertica://localhost:5433/vertica"
    driver = "com.vertica.jdbc.Driver"
    connection_check_timeout_sec = 100
    user = "root"
    password = "123456"
    # 定义查询逻辑
    query = "select * from type_bin"
    # 并行分片读取字段
    partition_column = "id"
    # 片段数量
    partition_num = 10
  }
}
```

#### 并行边界示例
```sql
source {
  Jdbc {
    url = "jdbc:vertica://localhost:5433/vertica"
    driver = "com.vertica.jdbc.Driver"
    connection_check_timeout_sec = 100
    user = "root"
    password = "123456"
    # 定义查询逻辑
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

希望这些信息对您有所帮助！如果您有任何其他问题，请随时提问。