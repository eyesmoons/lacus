根据您提供的链接内容，我为您整理了关于 Apache SeaTunnel JDBC Kingbase Source Connector 的说明文档。

### Apache SeaTunnel JDBC Kingbase Source Connector

#### 简介
Apache SeaTunnel 是一个开源的数据集成和数据管道工具，支持多种数据源和目标系统。JDBC Kingbase Source Connector 是 SeaTunnel 的一部分，用于通过 JDBC 从 Kingbase 数据库中读取数据。

#### 支持的版本
- **Kingbase 版本**: 8.6
- **SeaTunnel 版本**: 2.3.11

#### 关键特性
- **批处理支持**: 支持批量读取数据。
- **流处理支持**: 支持流式读取数据。
- **精确一次**: 保证数据读取的精确一次处理。
- **列投影**: 支持指定读取的列。
- **并行处理**: 支持并行读取数据，提高性能。
- **用户自定义分区**: 支持自定义分区列，以便并行处理。

#### 描述
通过 JDBC 从外部数据源（如 Kingbase）读取数据。

#### 支持的数据源信息
| 数据源   | 支持的版本 | 驱动类名               | URL 示例                                   | Maven 依赖 |
| -------- | ---------- | ---------------------- | ------------------------------------------ | ---------- |
| Kingbase | 8.6        | `com.kingbase8.Driver` | `jdbc:kingbase8://localhost:54321/db_test` | 下载       |

#### 数据类型映射
| Kingbase 数据类型 | SeaTunnel 数据类型                              |
| ----------------- | ----------------------------------------------- |
| BOOL              | BOOLEAN                                         |
| INT2              | SHORT                                           |
| SMALLSERIAL       | SMALLINT                                        |
| SERIAL            | INT                                             |
| INT4              | INT                                             |
| INT8              | BIGINT                                          |
| BIGSERIAL         | BIGINT                                          |
| FLOAT4            | FLOAT                                           |
| FLOAT8            | DOUBLE                                          |
| NUMERIC           | DECIMAL((指定列的指定大小), (指定列的小数位数)) |
| BPCHAR            | STRING                                          |
| CHARACTER         | STRING                                          |
| VARCHAR           | STRING                                          |
| TEXT              | STRING                                          |
| TIMESTAMP         | LOCALDATETIME                                   |
| TIME              | LOCALTIME                                       |
| DATE              | LOCALDATE                                       |
| 其他数据类型      | 不支持                                          |

#### 源选项
| 名称                         | 类型       | 是否必需 | 默认值          | 描述                                                         |
| ---------------------------- | ---------- | -------- | --------------- | ------------------------------------------------------------ |
| url                          | String     | 是       | -               | JDBC 连接的 URL，例如：`jdbc:kingbase8://localhost:54321/test` |
| driver                       | String     | 是       | -               | 连接远程数据源的 JDBC 类名，应为 `com.kingbase8.Driver`      |
| user                         | String     | 否       | -               | 连接实例的用户名                                             |
| password                     | String     | 否       | -               | 连接实例的密码                                               |
| query                        | String     | 是       | -               | 查询语句                                                     |
| connection_check_timeout_sec | Int        | 否       | 30              | 用于验证连接完成的数据库操作等待秒数                         |
| partition_column             | String     | 否       | -               | 用于并行性的分区列，仅支持数值类型列和字符串类型列           |
| partition_lower_bound        | BigDecimal | 否       | -               | 扫描的分区列最小值，如果未设置，SeaTunnel 将查询数据库获取最小值 |
| partition_upper_bound        | BigDecimal | 否       | -               | 扫描的分区列最大值，如果未设置，SeaTunnel 将查询数据库获取最大值 |
| partition_num                | Int        | 否       | job parallelism | 分区计数，仅支持正整数，默认值为作业并行度                   |
| fetch_size                   | Int        | 否       | 0               | 对于返回大量对象的查询，可以配置查询中使用的行获取大小，通过减少满足选择条件所需的数据库查询次数来提高性能。0 表示使用 JDBC 默认值 |
| common-options               | -          | 否       | -               | 源插件公共参数，请参考源公共选项的详细信息                   |

#### 提示
- 如果未设置 `partition_column`，将运行在单并发模式下；如果设置了 `partition_column`，将根据任务的并发性并行执行。

#### 任务示例
##### 简单示例
```sql
env {
  parallelism = 2
  job.mode = "BATCH"
}

source {
  Jdbc {
    driver = "com.kingbase8.Driver"
    url = "jdbc:kingbase8://localhost:54321/db_test"
    user = "root"
    password = ""
    query = "select * from source"
  }
}

transform {
  # 如果您想了解更多关于如何配置 SeaTunnel 以及查看转换插件的完整列表，请访问 https://seatunnel.apache.org/docs/transform/sql
}

sink {
  Console {}
}
```

##### 并行示例
```sql
source {
  Jdbc {
    driver = "com.kingbase8.Driver"
    url = "jdbc:kingbase8://localhost:54321/db_test"
    user = "root"
    password = ""
    query = "select * from source"
    # 并行分片读取字段
    partition_column = "id"
    # 片段数量
    partition_num = 10
  }
}
```

##### 并行边界示例
```sql
source {
  Jdbc {
    driver = "com.kingbase8.Driver"
    url = "jdbc:kingbase8://localhost:54321/db_test"
    user = "root"
    password = ""
    query = "select * from source"
    partition_column = "id"
    partition_num = 10
    # 读取起始边界
    partition_lower_bound = 1
    # 读取结束边界
    partition_upper_bound = 500
  }
}
```

#### Changelog
- 更改日志（具体内容请参考官方文档）

#### 版本
- SeaTunnel 版本：2.3.11

#### 社区
- Apache SeaTunnel 是 Apache 软件基金会（ASF）孵化项目，由 Apache Incubator 赞助。

#### 版权
- 版权 © 2021-2022 The Apache Software Foundation. Apache SeaTunnel, SeaTunnel, 和其羽毛标志是 The Apache Software Foundation 的商标。

请参考官方文档获取更多详细信息：[Apache SeaTunnel JDBC Kingbase Source Connector](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/Kingbase/)