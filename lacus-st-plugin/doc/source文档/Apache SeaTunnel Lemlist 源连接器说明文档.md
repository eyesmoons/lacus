### Apache SeaTunnel Lemlist Source Connector 说明文档

#### 简介
Apache SeaTunnel 是一个开源的数据集成工具，支持大规模数据的实时和离线同步。Lemlist Source Connector 是 SeaTunnel 中的一个数据源连接器，用于从 Lemlist 平台读取数据。

#### 描述
Lemlist Source Connector 用于从 Lemlist 读取数据。它支持批处理和流式处理模式，确保数据的精确一次处理，并提供列投影、并行处理和用户自定义拆分等高级功能。

#### 关键特性
- **批处理**：支持批量读取数据。
- **流式处理**：支持实时数据流读取。
- **精确一次处理**：确保数据在读取过程中不会丢失。
- **列投影**：允许用户选择性地读取特定列。
- **并行处理**：支持多线程并行处理，提高读取效率。
- **用户自定义拆分**：允许用户自定义数据拆分规则。

#### 选项
以下是 Lemlist Source Connector 的主要配置选项：

| 名称                        | 类型    | 是否必需 | 默认值 |
| --------------------------- | ------- | -------- | ------ |
| url                         | String  | 是       | -      |
| password                    | String  | 是       | -      |
| method                      | String  | 否       | get    |
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
- **password**：用于登录的 API 密钥，更多详情请参考 [Lemlist 集成设置](https://app.lemlist.com/settings/integrations)。
- **method**：HTTP 请求方法，仅支持 GET 和 POST 方法。
- **params**：HTTP 参数。
- **body**：HTTP 请求体。
- **poll_interval_millis**：在流式模式下，请求 HTTP API 的间隔（毫秒）。
- **retry**：如果请求 HTTP 返回 IOException，最大重试次数。
- **retry_backoff_multiplier_ms**：如果请求 HTTP 失败，重试回退时间的乘数（毫秒）。
- **retry_backoff_max_ms**：如果请求 HTTP 失败，最大重试回退时间（毫秒）。
- **format**：上游数据的格式，目前仅支持 JSON 文本，默认为 JSON。
- **schema**：上游数据的模式字段。
- **content_json**：此参数可以获取一些 JSON 数据。如果你只需要 'book' 部分的数据，配置 content_field = “$.store.book.*”。
- **json_field**：此参数帮助你配置模式，因此必须与 schema 一起使用。

#### 示例
以下是一个使用 Lemlist Source Connector 的示例配置：

```plaintext
Lemlist {
    url = "https://api.lemlist.com/api/campaigns"
    password = "SeaTunnel-test"
    schema {
        fields {
            _id = string
            name = string
        }
    }
}
```

#### 更新日志
更多关于 Lemlist Source Connector 的更新日志和详细信息，请参考 [Apache SeaTunnel 官方文档](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/Lemlist)。

#### 注意事项
- 确保你已经安装并配置了 Apache SeaTunnel。
- 确保你有 Lemlist 的 API 密钥，并正确配置了 `password` 选项。
- 根据实际需求调整其他配置选项。

希望这份说明文档能帮助你更好地使用 Apache SeaTunnel Lemlist Source Connector。如果有更多问题，请参考官方文档或社区支持。