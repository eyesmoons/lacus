### Apache SeaTunnel Amazon DynamoDB 源连接器说明文档

#### 描述

Apache SeaTunnel Amazon DynamoDB 源连接器用于从 Amazon DynamoDB 数据库中读取数据。DynamoDB 是一个支持键值存储和文档数据结构的 NoSQL 数据库服务，因此在使用此连接器时，需要配置数据模式。

#### 关键特性

- **批处理**：支持批量读取数据。
- **流处理**：支持实时数据流处理。
- **精确一次**：确保数据精确读取一次，避免数据丢失或重复。
- **列投影**：支持指定读取的列，提高数据读取效率。
- **并行度**：支持并行读取，提高数据处理性能。
- **支持用户自定义分片**：允许用户自定义数据分片规则，优化数据读取。

#### 选项

连接器提供了以下配置选项：

| 名称                  | 类型   | 是否必需 | 默认值 |
| --------------------- | ------ | -------- | ------ |
| url                   | string | 是       | -      |
| region                | string | 是       | -      |
| access_key_id         | string | 是       | -      |
| secret_access_key     | string | 是       | -      |
| table                 | string | 是       | -      |
| schema                | config | 是       | -      |
| common-options        | 是     | -        |        |
| scan_item_limit       | 否     | -        |        |
| parallel_scan_threads | 否     | -        |        |

#### 详细说明

- **url**：读取 Amazon DynamoDB 的 URL。

- **region**：Amazon DynamoDB 的分区。

- **accessKeyId**：Amazon DynamoDB 的访问 ID。

- **secretAccessKey**：Amazon DynamoDB 的访问密钥。

- **table**：Amazon DynamoDB 的表名。

- **schema**：定义数据模式，因为 DynamoDB 是一个 NoSQL 数据库，无法自动获取数据类型，因此必须配置模式。例如：

    ```json
    schema {
      fields {
        id = int
        key_aa = string
        key_bb = string
      }
    }
    ```

- **common options**：源插件常用参数，具体请参考源插件文档。

- **scan_item_limit**：每个扫描请求返回的项目数。

- **parallel_scan_threads**：并行扫描的逻辑段数。

#### 示例配置

```json
Amazondynamodb {
  url = "http://127.0.0.1:8000"
  region = "us-east-1"
  accessKeyId = "dummy-key"
  secretAccessKey = "dummy-secret"
  table = "TableName"
  schema = {
    fields {
      artist = string
      c_map = "map<string, array<int>>"
      c_array = "array<int>"
      c_string = string
      c_boolean = boolean
      c_tinyint = tinyint
      c_smallint = smallint
      c_int = int
      c_bigint = bigint
      c_float = float
      c_double = double
      c_decimal = "decimal(30, 8)"
      c_null = "null"
      c_bytes = bytes
      c_date = date
      c_timestamp = timestamp
    }
  }
}
```

#### 变更日志

- 本文档基于 Apache SeaTunnel 2.3.11 版本编写，更多变更日志请参考官方文档。

#### 参考链接

- [AmazonDynamoDB - Apache SeaTunnel](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/AmazonDynamoDB)

通过以上说明文档，您可以对 Apache SeaTunnel Amazon DynamoDB 源连接器有一个全面的了解，并能够根据实际需求进行配置和使用。

