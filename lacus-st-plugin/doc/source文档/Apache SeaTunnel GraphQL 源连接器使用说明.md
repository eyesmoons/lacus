### Apache SeaTunnel GraphQL 源连接器使用说明

#### 描述
Apache SeaTunnel 的 GraphQL 源连接器用于读取 GraphQL 数据。它支持批处理、流处理和并行处理，能够从 GraphQL 服务中高效地提取数据。

#### 主要特性
- **批处理**：一次性处理大量数据。
- **流处理**：实时处理数据流。
- **并行**：支持并行处理以提高效率。

#### 源选项
以下是 GraphQL 源连接器的主要配置选项：

| 名称                        | 类型    | 是否必填 | 默认值                  |
| --------------------------- | ------- | -------- | ----------------------- |
| url                         | String  | 是       | -                       |
| query                       | String  | 是       | -                       |
| variables                   | Config  | 否       | -                       |
| enable_subscription         | boolean | 否       | false                   |
| timeout                     | Long    | 否       | -                       |
| content_field               | String  | 是       | $.data.{query_object}.* |
| schema.fields               | Config  | 是       | -                       |
| format                      | String  | 否       | json                    |
| params                      | Map     | 是       | -                       |
| poll_interval_millis        | int     | 否       | -                       |
| retry                       | int     | 否       | -                       |
| retry_backoff_multiplier_ms | int     | 否       | 100                     |
| retry_backoff_max_ms        | int     | 否       | 10000                   |
| enable_multi_lines          | boolean | 否       | false                   |
| common-options              | config  | 否       | -                       |

#### 选项说明
- **url**：HTTP 请求路径。
- **query**：GraphQL 表达式查询字符串。
- **variables**：GraphQL 变量，例如：
  ```json
  variables = {
    limit = 2
  }
  ```
- **enable_subscription**：
  - `true`：构建一个套接字读取器来订阅 GraphQL 服务。
  - `false`：构建 GraphQL 服务的 HTTP 阅读器订阅。
- **timeout**：超时时间。
- **content_field**：SONPath 通配符，用于指定数据字段。
- **params**：HTTP 请求参数。
- **poll_interval_millis**：流模式下请求 HTTP API 间隔（毫秒）。
- **retry**：如果请求 HTTP 返回 `IOException` 的最大重试次数。
- **retry_backoff_multiplier_ms**：如果请求 HTTP 失败，则重试回退时间（毫秒）倍率。
- **retry_backoff_max_ms**：如果 HTTP 请求失败，最大重试回退时间（毫秒）。
- **format**：上游数据的格式，默认为 `json`。
- **schema**：填写一个固定值，例如：
  ```json
  schema = {
    fields {
      metric = "map<string, string>"
      value = double
      time = long
    }
  }
  ```
- **fields**：上游数据的模式字段。
- **common options**：源插件常用参数，请参考 `Source Common Options` 获取详细信息。

#### 示例
以下是一个使用 GraphQL 源连接器的示例配置：

```sql
source {
  GraphQL {
    url = "http://192.168.1.103:9081/v1/graphql"
    format = "json"
    content_field = "$.data.source"
    query = """
    query MyQuery($limit: Int) {
      source(limit: $limit) {
        id
        val_bool
        val_double
        val_float
      }
    }
    """
    variables = {
      limit = 2
    }
    schema = {
      fields {
        id = "int"
        val_bool = "boolean"
        val_double = "double"
        val_float = "float"
      }
    }
  }
}
```

#### 变更日志
更多关于 SeaTunnel 的变更日志和更新信息，请参考官方文档。

#### 参考链接
- [GraphQL 源连接器文档](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/GraphQL/)
- [Apache SeaTunnel 官方网站](https://seatunnel.apache.org/)

通过以上说明文档，您应该能够了解和使用 Apache SeaTunnel 的 GraphQL 源连接器进行数据集成。如果有更多问题，请参考官方文档或社区支持。