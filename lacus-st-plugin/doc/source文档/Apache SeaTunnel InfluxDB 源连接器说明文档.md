### Apache SeaTunnel InfluxDB 源连接器说明文档

#### 简介
Apache SeaTunnel 是一个强大的数据集成工具，支持多种数据源的连接。InfluxDB 源连接器允许 SeaTunnel 从 InfluxDB 数据库中读取数据。本文档基于 SeaTunnel 2.3.11 版本，详细介绍了如何配置和使用 InfluxDB 源连接器。

#### 功能特点
- **批量读取**：支持批量读取 InfluxDB 中的数据。
- **流式读取**：支持流式读取 InfluxDB 中的数据。
- **精确一次**：确保数据读取的精确性，避免数据丢失。
- **列投影**：支持通过 SQL 查询实现列投影，选择需要的字段。
- **并行处理**：支持用户自定义分区，实现并行处理。
- **时间精度**：支持不同的时间精度返回，如小时、分钟、秒等。

#### 配置选项
以下是 InfluxDB 源连接器的配置选项：

| 参数名               | 类型   | 是否必需 | 默认值 | 描述                                                         |
| -------------------- | ------ | -------- | ------ | ------------------------------------------------------------ |
| `url`                | string | 是       | -      | 连接到 InfluxDB 的 URL，例如 `http://influxdb-host:8086`     |
| `sql`                | string | 是       | -      | 用于搜索数据的查询 SQL 语句，例如 `select name,age from test` |
| `schema`             | config | 是       | -      | 上游数据的模式信息，例如 `{ fields { name = string age = int } }` |
| `database`           | string | 是       | -      | InfluxDB 的数据库名称                                        |
| `username`           | string | 否       | -      | InfluxDB 的用户名（可选）                                    |
| `password`           | string | 否       | -      | InfluxDB 的密码（可选）                                      |
| `split_column`       | string | 否       | -      | InfluxDB 的分割列（可选）                                    |
| `epoch`              | string | 否       | `n`    | 返回的时间精度，可选值：H, m, s, MS, u, n                    |
| `query_timeout_sec`  | int    | 否       | 3      | 查询超时时间（秒）                                           |
| `connect_timeout_ms` | long   | 否       | 15000  | 连接超时时间（毫秒）                                         |
| `common-options`     | config | 否       | -      | 源插件通用参数，请参考源通用选项的详细说明                   |

#### 示例配置
以下是一些使用 InfluxDB 源连接器的示例配置：

**多并行和多分区扫描示例：**
```properties
source {
  InfluxDB {
    url = "http://influxdb-host:8086"
    sql = "select label, value, rt, time from test"
    database = "test"
    upper_bound = 100
    lower_bound = 1
    partition_num = 4
    split_column = "value"
    schema {
      fields {
        label = STRING
        value = INT
        rt = STRING
        time = BIGINT
      }
    }
  }
}
```

**不使用分区扫描的示例：**
```properties
source {
  InfluxDB {
    url = "http://influxdb-host:8086"
    sql = "select label, value, rt, time from test"
    database = "test"
    schema {
      fields {
        label = STRING
        value = INT
        rt = STRING
        time = BIGINT
      }
    }
  }
}
```

#### 注意事项
- InfluxDB 的标签（tags）不支持作为分割主键，因为标签的类型只能是字符串。
- InfluxDB 的时间字段不支持作为分割主键，因为时间字段不能参与数学计算。
- 目前，分割列（`split_column`）仅支持整数数据分割，不支持浮点数、字符串、日期等类型。
- `upper_bound` 和 `lower_bound` 用于定义分割列的范围，`partition_num` 定义分区的数量。确保 `upper_bound` 减去 `lower_bound` 能够被 `partition_num` 整除，否则查询结果可能会重叠。

#### 变更日志
本版本的 InfluxDB 源连接器的主要变更和改进记录在官方文档的变更日志中，请参考相关文档获取详细信息。

#### 总结
Apache SeaTunnel 的 InfluxDB 源连接器为用户提供了高效、灵活的数据读取方案，支持多种配置选项和功能特点，适用于各种数据集成场景。通过合理配置和使用，用户可以轻松地从 InfluxDB 数据库中读取数据，并进行进一步的处理和分析。