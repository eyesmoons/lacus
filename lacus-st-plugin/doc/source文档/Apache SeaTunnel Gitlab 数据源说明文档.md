### Apache SeaTunnel Gitlab 数据源说明文档

#### 简介
Apache SeaTunnel 是一个开源的数据集成平台，支持实时海量数据同步。Gitlab 数据源是 SeaTunnel 连接器家族中的一个，用于从 Gitlab 读取数据。

#### 主要功能
- **批量处理**：支持批量读取 Gitlab 数据。
- **流式处理**：支持流式读取 Gitlab 数据。
- **精确一次**：确保数据读取的精确性，一次写入只处理一次数据。
- **列投影**：支持指定读取的列。
- **并行处理**：支持并行读取数据，提高处理效率。
- **支持用户自定义分割**：允许用户自定义数据分割方式。

#### 选项说明
| 参数名                      | 类型   | 是否必须 | 默认值 | 说明                                                         |
| --------------------------- | ------ | -------- | ------ | ------------------------------------------------------------ |
| url                         | String | 是       | -      | HTTP 请求的 URL。                                            |
| access_token                | String | 是       | -      | 个人访问令牌。                                               |
| method                      | String | 否       | get    | HTTP 请求方法，仅支持 GET 和 POST 方法。                     |
| params                      | Map    | 否       | -      | HTTP 参数。                                                  |
| body                        | String | 否       | -      | HTTP 请求体。                                                |
| poll_interval_millis        | int    | 否       | -      | 流式模式下请求 HTTP API 的间隔（毫秒）。                     |
| retry                       | int    | 否       | -      | 如果请求 HTTP 返回 IOException，最大重试次数。               |
| retry_backoff_multiplier_ms | int    | 否       | 100    | 请求 HTTP 失败时重试回退时间的乘数（毫秒）。                 |
| retry_backoff_max_ms        | int    | 否       | 10000  | 请求 HTTP 失败时最大重试回退时间（毫秒）。                   |
| format                      | String | 否       | json   | 上游数据的格式，目前仅支持 JSON 文本，默认为 JSON。          |
| schema                      | Config | 否       | -      | 上游数据的模式。当 format 为 JSON 时，需要指定 schema。      |
| content_json                | String | 否       | -      | 可以获取一些 JSON 数据。如果只需要 'book' 部分的数据，配置 content_field = $.store.book.*。 |
| json_field                  | Config | 否       | -      | 帮助配置 schema，必须与 schema 一起使用。                    |
| common-options              | config | 否       | -      | 源插件公共参数，请参考源公共选项的详细信息。                 |

#### 参数详细说明
- **url**：HTTP 请求的 URL。
- **access_token**：个人访问令牌，用于认证。
- **method**：HTTP 请求方法，仅支持 GET 和 POST 方法。
- **params**：HTTP 参数，可以传递额外的参数到 Gitlab API。
- **body**：HTTP 请求体，用于 POST 请求。
- **poll_interval_millis**：流式模式下请求 HTTP API 的间隔（毫秒）。
- **retry**：如果请求 HTTP 返回 IOException，最大重试次数。
- **retry_backoff_multiplier_ms**：请求 HTTP 失败时重试回退时间的乘数（毫秒）。
- **retry_backoff_max_ms**：请求 HTTP 失败时最大重试回退时间（毫秒）。
- **format**：上游数据的格式，目前仅支持 JSON 文本，默认为 JSON。
- **schema**：上游数据的模式。当 format 为 JSON 时，需要指定 schema。
- **content_json**：可以获取一些 JSON 数据。如果只需要 'book' 部分的数据，配置 content_field = $.store.book.*。
- **json_field**：帮助配置 schema，必须与 schema 一起使用。

#### 示例
```plaintext
Gitlab{
    url = "https://gitlab.com/api/v4/projects"
    access_token = "xxxxx"
    schema {
        fields {
            id = int
            description = string
            name = string
            name_with_namespace = string
            path = string
            http_url_to_repo = string
        }
    }
}
```

#### Changelog
- [Change Log](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/changelog/connector-http-gitlab/)

#### 版本
- [2.3.11](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/Gitlab/)

#### 社区
- [GitHub](https://github.com/apache/seatunnel)
- [Issue Tracker](https://issues.apache.org/jira/browse/SEATUNNEL)
- [Pull Requests](https://github.com/apache/seatunnel/pulls)

#### 订阅邮件组
- [How to Subscribe](https://seatunnel.apache.org/zh-CN/docs/2.3.11/how-to-subscribe/)

#### 版权信息
- Copyright © 2021-2022 The Apache Software Foundation.
- Apache SeaTunnel, SeaTunnel, and its feather logo are trademarks of The Apache Software Foundation.

通过以上说明文档，您可以对 Apache SeaTunnel Gitlab 数据源有一个全面的了解，并能够根据实际需求进行配置和使用。