根据您提供的链接内容，我整理了以下关于 Apache SeaTunnel StarRocks 连接器的使用说明和配置细节：

### StarRocks 源连接器说明

#### 描述
StarRocks 源连接器用于通过 StarRocks 读取外部数据源的数据。其内部实现是从 FE（前端）获取查询计划，将查询计划作为参数传递给 BE（后端）节点，然后从 BE 节点获取数据结果。

#### 主要功能
- 批处理
- 流处理
- 精确一次
- 列投影
- 并行度
- 支持用户定义拆分

### 配置选项

| 名称                    | 类型   | 是否必须 | 默认值            |
| ----------------------- | ------ | -------- | ----------------- |
| nodeUrls                | list   | 是       | -                 |
| username                | string | 是       | -                 |
| password                | string | 是       | -                 |
| database                | string | 是       | -                 |
| table                   | string | 否       | -                 |
| scan_filter             | string | 否       | -                 |
| schema                  | config | 否       | -                 |
| table_list              | array  | 否       | -                 |
| request_tablet_size     | int    | 否       | Integer.MAX_VALUE |
| scan_connect_timeout_ms | int    | 否       | 30000             |
| scan_query_timeout_sec  | int    | 否       | 3600              |
| scan_keep_alive_min     | int    | 否       | 10                |
| scan_batch_rows         | int    | 否       | 1024              |
| scan_mem_limit          | long   | 否       | 2147483648        |
| max_retries             | int    | 否       | 3                 |
| scan.params.*           | string | 否       | -                 |

#### 配置选项说明

- **nodeUrls**: StarRocks 集群地址配置格式，例如 `["fe_ip:fe_http_port", ...]`。
- **username**: StarRocks 用户名称。
- **password**: StarRocks 用户密码。
- **database**: StarRocks 数据库名。
- **table**: StarRocks 表名。
- **scan_filter**: 过滤查询的表达式，该表达式透明地传输到 StarRocks，StarRocks 使用此表达式完成源端数据过滤。
- **schema**: 要生成的 StarRocks 的 schema。
- **table_list**: StarRocks 表名列表，当需要同时读取多表时使用此配置代替 `table`。
- **request_tablet_size**: 与分区对应的 StarRocks tablet 的数量。此值设置得越小，生成的分区就越多，这将增加引擎的并行度，但同时也会给 StarRocks 造成更大的压力。
- **scan_connect_timeout_ms**: 发送到 StarRocks 的请求连接超时。
- **scan_query_timeout_sec**: 在 StarRocks 中，查询超时时间的默认值为 1 小时，-1 表示没有超时限制。
- **scan_keep_alive_min**: 查询任务的保持连接时长，单位是分钟，默认值为 10 分钟。
- **scan_batch_rows**: 一次从 BE 节点读取的最大数据行数。
- **scan_mem_limit**: 单个查询在 BE 节点上允许的最大内存空间，单位为字节，默认值为 2147483648 字节（即 2 GB）。
- **max_retries**: 发送到 StarRocks 的重试请求次数。
- **scan.params.**: 从 BE 节点扫描数据相关的参数。

### 示例

#### 示例 1
```yaml
source {
  StarRocks {
    nodeUrls = ["starrocks_e2e:8030"]
    username = root
    password = ""
    database = "test"
    table = "e2e_table_source"
    scan_batch_rows = 10
    max_retries = 3
    schema {
      fields {
        BIGINT_COL = BIGINT
        LARGEINT_COL = STRING
        SMALLINT_COL = SMALLINT
        TINYINT_COL = TINYINT
        BOOLEAN_COL = BOOLEAN
        DECIMAL_COL = "DECIMAL(20, 1)"
        DOUBLE_COL = DOUBLE
        FLOAT_COL = FLOAT
        INT_COL = INT
        CHAR_COL = STRING
        VARCHAR_11_COL = STRING
        STRING_COL = STRING
        DATETIME_COL = TIMESTAMP
        DATE_COL = DATE
      }
    }
    scan.params.scanner_thread_pool_thread_num = "3"
  }
}
```

#### 示例 2: 读取多表
```yaml
source {
  StarRocks {
    nodeUrls = ["starrocks_e2e:8030"]
    username = root
    password = ""
    database = "test"
    table_list = [
      {
        table = "e2e_table_source"
        schema = {
          fields {
            BIGINT_COL = BIGINT
            LARGEINT_COL = STRING
            SMALLINT_COL = SMALLINT
            TINYINT_COL = TINYINT
            BOOLEAN_COL = BOOLEAN
            DECIMAL_COL = "DECIMAL(20, 1)"
            DOUBLE_COL = DOUBLE
            FLOAT_COL = FLOAT
            INT_COL = INT
            CHAR_COL = STRING
            VARCHAR_11_COL = STRING
            STRING_COL = STRING
            DATETIME_COL = TIMESTAMP
            DATE_COL = DATE
          }
        }
      },
      {
        table = "e2e_table_source_2"
        schema = {
          fields {
            BIGINT_COL_2 = BIGINT
            LARGEINT_COL_2 = STRING
            SMALLINT_COL_2 = SMALLINT
            TINYINT_COL_2 = TINYINT
            BOOLEAN_COL_2 = BOOLEAN
            DECIMAL_COL_2 = "DECIMAL(20, 1)"
            DOUBLE_COL_2 = DOUBLE
            FLOAT_COL_2 = FLOAT
            INT_COL_2 = INT
            CHAR_COL_2 = STRING
            VARCHAR_11_COL_2 = STRING
            STRING_COL_2 = STRING
            DATETIME_COL_2 = TIMESTAMP
            DATE_COL_2 = DATE
          }
        }
      }
    ]
    scan_batch_rows = 10
    max_retries = 3
    scan.params.scanner_thread_pool_thread_num = "3"
  }
}
```

### 总结
StarRocks 源连接器提供了丰富的配置选项，可以满足不同的数据读取需求。通过合理配置这些选项，可以有效地从 StarRocks 数据源中读取数据，并将其用于数据同步、流处理等任务。