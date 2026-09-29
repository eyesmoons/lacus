### Apache SeaTunnel Notion 连接器说明文档

#### 简介
Apache SeaTunnel Notion 连接器用于从 Notion 数据库中读取数据。它支持批量读取和流式读取，确保数据的精确一次处理，并提供列投影和并行处理功能。此外，它还支持用户自定义分割，以优化数据读取效率。

#### 关键特性
- **批量读取**：支持批量读取 Notion 数据库中的数据。
- **流式读取**：支持流式读取模式，适用于实时数据处理。
- **精确一次处理**：确保数据在读取过程中不会丢失或重复。
- **列投影**：允许用户选择性地读取特定列的数据。
- **并行处理**：支持并行读取，提高数据处理效率。
- **用户自定义分割**：允许用户自定义数据分割规则，以优化读取性能。

#### 配置选项
以下是 Notion 连接器的配置选项：

| 参数名                        | 类型   | 是否必需 | 默认值 | 描述                                                         |
| ----------------------------- | ------ | -------- | ------ | ------------------------------------------------------------ |
| `url`                         | String | 是       | -      | HTTP 请求的 URL，即 Notion 数据库的 API 地址。               |
| `password`                    | String | 是       | -      | 用于登录的 API 密钥，详细获取方式请参考 [Notion 开发者文档](https://developers.notion.com/docs/authorization)。 |
| `version`                     | String | 是       | -      | Notion API 的版本号，不同版本发布日期不同。                  |
| `method`                      | String | 否       | get    | HTTP 请求方法，目前只支持 `GET` 和 `POST` 方法。             |
| `params`                      | Map    | 否       | -      | HTTP 请求参数。                                              |
| `body`                        | String | 否       | -      | HTTP 请求体。                                                |
| `poll_interval_millis`        | int    | 否       | -      | 在流式读取模式下，请求 Notion API 的间隔时间（毫秒）。       |
| `retry`                       | int    | 否       | -      | 请求失败时的最大重试次数。                                   |
| `retry_backoff_multiplier_ms` | int    | 否       | 100    | 请求失败时的重试间隔乘数（毫秒）。                           |
| `retry_backoff_max_ms`        | int    | 否       | 10000  | 请求失败时的最大重试间隔（毫秒）。                           |
| `format`                      | String | 否       | json   | 上游数据的格式，目前只支持 `json` 文本格式。                 |
| `schema.fields`               | Config | 否       | -      | 上游数据的模式字段。当格式为 `json` 时，必须指定该选项。     |
| `content_json`                | String | 否       | -      | 获取部分 JSON 数据，例如，如果只需要 'book' 部分的数据，可以配置 `content_field = $.store.book.*`。 |
| `json_field`                  | Config | 否       | -      | 配置模式，必须与 `schema` 一起使用。例如，如果数据结构如下： |
|                               |        |          |        | ```json | { "store": { "book": [ ... ] }, "bicycle": { ... }, "expensive": 10 } ``` |
|                               |        |          |        | 可以配置任务如下：                                           |
|                               |        |          |        | ```source { Http { url = "http://mockserver:1080/jsonpath/mock" method = "GET" format = "json" json_field = { category = "$.store.book[*].category" author = "$.store.book[*].author" title = "$.store.book[*].title" price = "$.store.book[*].price" } schema = { fields { category = string author = string title = string price = string } } } } ``` |
| `common-options`              | config | 否       | -      | 源插件通用参数，详细请参考 [源插件通用选项](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/)。 |

#### 示例配置
以下是一个 Notion 连接器的示例配置：

```source {
    Notion {
        url = "https://api.notion.com/v1/users"
        password = "SeaTunnel-test"
        version = "2022-06-28"
        content_field = "$.results.*"
        schema = {
            fields {
                object = string
                id = string
                type = string
                person = {
                    email = string
                }
                avatar_url = string
            }
        }
    }
}
```

#### 变更日志
- [Notion 连接器变更日志](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/changelog/connector-http-notion/)

#### 相关链接
- [Notion | Apache SeaTunnel](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/Notion)
- [收藏！史上最全Apache SeaTunnel Source 连接器盘点(2025版)](https://www.cnblogs.com/seatunnel/p/19048614)
- [Source(V2) of SeaTunnel](https://seatunnel.apache.org/zh-CN/docs/connector-v2/source/)

#### 注意事项
- 请确保在使用 Notion API 密钥时遵守 Notion 的使用条款和隐私政策。
- 示例配置仅供参考，实际使用时请根据具体需求进行调整。

希望这份说明文档能帮助你更好地理解和使用 Apache SeaTunnel Notion 连接器。如果有任何问题或需要进一步的帮助，请随时联系。