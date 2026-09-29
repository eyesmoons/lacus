### Apache SeaTunnel Klaviyo 连接器说明文档

#### 简介
Apache SeaTunnel Klaviyo 连接器是一个用于从 Klaviyo 平台读取数据的源连接器。Klaviyo 是一个强大的营销自动化工具，帮助企业通过电子邮件、短信等方式进行客户关系管理。通过这个连接器，用户可以将 Klaviyo 中的数据集成到 SeaTunnel 的数据处理流程中。

#### 版本
- 版本：2.3.11

#### 描述
该连接器用于从 Klaviyo 读取数据，支持批处理和流式处理模式，确保数据的精确一次处理。它还支持列投影、并行处理以及用户自定义分割。

#### 主要特性
- **批处理**：支持批量读取数据。
- **流式处理**：支持实时流式读取数据。
- **精确一次**：确保数据处理的精确性，避免数据丢失或重复。
- **列投影**：允许用户选择性地读取特定的列。
- **并行处理**：支持并行处理，提高数据处理效率。
- **用户自定义分割**：允许用户自定义数据分割方式。

#### 选项
以下是 Klaviyo 连接器的配置选项：

| 名称                        | 类型   | 是否必需 | 默认值 | 描述                                                         |
| --------------------------- | ------ | -------- | ------ | ------------------------------------------------------------ |
| url                         | String | 是       | -      | HTTP 请求的 URL。                                            |
| private_key                 | String | 是       | -      | 用于登录的 API 私钥，更多详情请参考 [Klaviyo 认证文档](https://developers.klaviyo.com/en/docs/authenticate-#private-key-authentication)。 |
| revision                    | String | 是       | -      | API 端点版本（格式：YYYY-MM-DD）。                           |
| method                      | String | 否       | get    | HTTP 请求方法，仅支持 GET 和 POST 方法。                     |
| params                      | Map    | 否       | -      | HTTP 参数。                                                  |
| body                        | String | 否       | -      | HTTP 请求体。                                                |
| poll_interval_millis        | int    | 否       | -      | 流式模式下请求 HTTP API 的间隔（毫秒）。                     |
| retry                       | int    | 否       | -      | 如果请求 HTTP 返回 IOException，最大重试次数。               |
| retry_backoff_multiplier_ms | int    | 否       | 100    | 请求 HTTP 失败时重试回退时间的乘数（毫秒）。                 |
| retry_backoff_max_ms        | int    | 否       | 10000  | 请求 HTTP 失败时最大重试回退时间（毫秒）。                   |
| format                      | String | 否       | json   | 上游数据的格式，目前仅支持 JSON 文本。                       |
| schema                      | Config | 否       | -      | 上游数据的模式配置。                                         |
| schema.fields               | Config | 否       | -      | 上游数据模式中的字段配置。                                   |
| content_json                | String | 否       | -      | 获取部分 JSON 数据。例如，如果只需要 'book' 部分的数据，可以配置 `content_field = $.store.book.*`。 |
| json_field                  | Config | 否       | -      | 配置模式，必须与 schema 一起使用。                           |
| content_field               | Config | 否       | -      | 配置获取 JSON 数据的字段路径。                               |
| common-options              | config | 否       | -      | 源插件通用参数，请参考 [源插件通用选项](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/)。 |

#### 示例配置
```properties
Klaviyo {
    url = "https://a.klaviyo.com/api/lists/"
    private_key = "SeaTunnel-test"
    revision = "2020-10-17"
    method = "GET"
    format = "json"
    schema = {
        fields {
            type = string
            id = string
            attributes = {
                name = string
                created = string
                updated = string
            }
            links = {
                self = string
            }
        }
    }
}
```

#### 更新日志
- 请参考 [Klaviyo 连接器的更新日志](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/Klaviyo/changelog/) 获取更多详细信息。

#### 社区与支持
- [Apache SeaTunnel 社区](https://seatunnel.apache.org/)
- [GitHub](https://github.com/apache/seatunnel)
- [Issue Tracker](https://issues.apache.org/jira/browse/SEATUNNEL)
- [Pull Requests](https://github.com/apache/seatunnel/pulls)

#### 版权信息
- 版权 © 2021-2022 The Apache Software Foundation.
- Apache SeaTunnel, SeaTunnel, 和其羽毛标志是 The Apache Software Foundation 的商标。

通过以上说明文档，用户可以详细了解如何配置和使用 Apache SeaTunnel Klaviyo 连接器，从而实现从 Klaviyo 平台高效地读取数据。