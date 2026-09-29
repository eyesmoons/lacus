根据您提供的链接内容，我为您整理了 Apache SeaTunnel 的 Persistiq 数据源连接器的说明文档：

### Persistiq 连接器介绍

#### 简介
Persistiq 连接器用于从 Persistiq 读取数据。它支持批处理和流处理模式，并确保精确一次处理。此外，它还支持模式投影、并行处理以及用户自定义拆分。

#### 主要特性
- **批处理**：支持批量数据读取。
- **流处理**：支持实时数据流读取。
- **精确一次处理**：确保数据处理的精确性，避免数据丢失或重复。
- **模式投影**：允许用户定义数据的模式。
- **并行处理**：支持并行处理，提高数据处理效率。
- **用户自定义拆分**：允许用户自定义数据拆分方式。

#### 选项
以下是 Persistiq 连接器的配置选项：

| 名称                        | 类型    | 是否必需 | 默认值 |
| --------------------------- | ------- | -------- | ------ |
| url                         | String  | 是       | -      |
| password                    | String  | 是       | -      |
| method                      | String  | 否       | get    |
| schema                      | Config  | 否       | -      |
| schema.fields               | Config  | 否       | -      |
| format                      | String  | 否       | json   |
| params                      | Map     | 否       | -      |
| body                        | String  | 否       | -      |
| json_field                  | Config  | 否       | -      |
| content_json                | String  | 否       | -      |
| poll_interval_millis        | int     | 否       | -      |
| retry                       | int     | 否       | -      |
| retry_backoff_multiplier_ms | int     | 否       | 100    |
| retry_backoff_max_ms        | int     | 否       | 10000  |
| enable_multi_lines          | boolean | 否       | false  |
| common-options              | config  | 否       | -      |

#### 选项详细说明
- **url**：HTTP 请求的 URL。
- **password**：用于登录的 API 密钥，可以在 Persistiq 网站上获取。
- **method**：HTTP 请求方法，仅支持 GET 和 POST 方法。
- **params**：HTTP 参数。
- **body**：HTTP 请求体。
- **poll_interval_millis**：在流模式下，请求 HTTP API 的间隔（毫秒）。
- **retry**：如果请求 HTTP 返回 IOException，最大重试次数。
- **retry_backoff_multiplier_ms**：如果请求 HTTP 失败，重试回退次数（毫秒）的乘数。
- **retry_backoff_max_ms**：如果请求 HTTP 失败，最大重试回退次数（毫秒）。
- **format**：上游数据的格式，目前仅支持 JSON 文本，默认为 JSON。
- **schema**：上游数据的模式。
- **schema.fields**：上游数据的模式字段。
- **json_field**：帮助配置模式，必须与 schema 一起使用。
- **content_json**：可以获取一些 JSON 数据。如果您只需要 'book' 部分的数据，配置 content_field = "$.store.book.*"。
- **content**：获取特定部分的数据。
- **common-options**：源插件通用参数，请参考源通用选项获取详细信息。

#### 示例
以下是一个 Persistiq 连接器的配置示例：

```plaintext
Persistiq{
    url = "https://api.persistiq.com/v1/users"
    password = "Your password"
    content_field = "$.users.*"
    schema = {
        fields {
            id = string
            name = string
            email = string
            activated = boolean
            default_mailbox_id = string
            salesforce_id = string
        }
    }
}
```

#### 变更日志
有关版本的变更日志，请参考官方文档。

#### 社区与支持
- **社区**：Apache SeaTunnel 社区提供支持和交流。
- **GitHub**：[Apache SeaTunnel GitHub 仓库](https://github.com/apache/seatunnel)
- **Issue Tracker**：[Apache SeaTunnel Issue Tracker](https://issues.apache.org/jira/browse/SEATUNNEL)
- **Pull Requests**：[Apache SeaTunnel Pull Requests](https://github.com/apache/seatunnel/pulls)

#### 版权信息
Apache SeaTunnel 是 Apache 软件基金会（ASF）孵化项目，由 Apache Incubator 赞助。孵化状态表示项目尚未完全获得 ASF 的认可，但正在积极开发中。

希望这份整理的说明文档对您有所帮助！如果有任何进一步的问题，请随时提问。