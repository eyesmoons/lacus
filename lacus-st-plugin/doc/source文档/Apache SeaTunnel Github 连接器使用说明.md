### Apache SeaTunnel Github 连接器使用说明

#### 描述
Apache SeaTunnel 的 Github 源连接器用于从 Github 读取数据。它支持批处理和流处理，能够确保数据的精确一次处理，并支持列投影和用户自定义分片。

#### 关键特性
- **批处理**：支持批量读取数据。
- **流处理**：支持实时流式读取数据。
- **精确一次**：确保数据只被处理一次，避免重复处理。
- **列投影**：可以选择性地读取数据列。
- **并行度**：支持并行处理，提高数据处理效率。
- **支持用户自定义分片**：允许用户自定义数据分片规则。

#### 选项
| 名称                        | 类型    | 必填 | 默认值 |
| --------------------------- | ------- | ---- | ------ |
| url                         | String  | 是   | -      |
| access_token                | String  | 否   | -      |
| method                      | String  | 否   | get    |
| schema.fields               | Config  | 否   | -      |
| format                      | String  | 否   | json   |
| params                      | Map     | 否   | -      |
| body                        | String  | 否   | -      |
| json_field                  | Config  | 否   | -      |
| content_json                | String  | 否   | -      |
| poll_interval_millis        | int     | 否   | -      |
| retry                       | int     | 否   | -      |
| retry_backoff_multiplier_ms | int     | 否   | 100    |
| retry_backoff_max_ms        | int     | 否   | 10000  |
| enable_multi_lines          | boolean | 否   | false  |
| common-options              | config  | 否   | -      |

#### 详细说明
- **url [String]**：HTTP 请求 URL。
- **access_token [String]**：GitHub个人访问令牌，请参阅：创建个人访问令牌 - Github文档。
- **method [String]**：HTTP 请求方法。目前支持 GET 和 POST。
- **params [Map]**：HTTP 参数。
- **body [String]**：HTTP 请求体。
- **poll_interval_millis [int]**：流模式下请求 API 的间隔时间（毫秒）。
- **retry [int]**：请求失败（IOException）时最大重试次数。
- **retry_backoff_multiplier_ms [int]**：请求失败时的退避时间（毫秒）乘数。
- **retry_backoff_max_ms [int]**：请求失败时的最大退避时间（毫秒）。
- **format [String]**：上游数据的格式，现在仅支持 json text，默认是 json。
- **schema [Config]**：上游数据的字段定义。
- **content_json [String]**：该参数可用于提取一些 json 数据。如果你只需要 “book” 部分的数据，可以配置 content_field = “$.store.book.*”。
- **json_field [Config]**：该参数用于帮助你配置 schema，因此必须与 schema 一起使用。

#### 示例
```plaintext
Github {
    url = "https://api.github.com/orgs/apache/repos"
    access_token = "xxxx"
    method = "GET"
    format = "json"
    schema = {
        fields {
            id = int
            name = string
            description = string
            html_url = string
            stargazers_count = int
            forks = int
        }
    }
}
```

#### 变更日志
- 本文档基于 Apache SeaTunnel 2.3.11 版本。

#### 参考链接
- [Apache SeaTunnel Github 连接器文档](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/Github)

#### 注意事项
- 使用 Github 连接器时，请确保你有相应的访问权限和令牌。
- 确保你的请求 URL 和参数配置正确，以避免请求失败。

通过以上说明，你应该能够了解如何使用 Apache SeaTunnel 的 Github 连接器来读取数据。如果有更多问题，请参考官方文档或社区支持。