根据您提供的链接内容，以下是对 Apache SeaTunnel Jira 连接器的说明文档整理：

---

### Apache SeaTunnel Jira 连接器使用说明

#### 描述
Apache SeaTunnel 的 Jira 源连接器用于从 Jira 系统中读取数据。它支持批处理和流处理模式，确保数据的精确一次性处理，并具备列投影和并行度控制功能。此外，它还支持用户自定义的分片，以适应不同的数据处理需求。

#### 关键特性
- **批处理**：支持批量读取数据。
- **流处理**：支持实时数据流读取。
- **精确一次**：确保数据在处理过程中只被处理一次，避免数据重复。
- **列投影**：可以选择性地读取特定的列。
- **并行度**：支持并行读取数据，提高数据处理效率。
- **支持用户定义的分片**：允许用户自定义数据分片规则。

#### 选项配置
以下是 Jira 连接器的主要配置选项：

| 名称                          | 类型   | 是否必需 | 默认值 | 描述                                                         |
| ----------------------------- | ------ | -------- | ------ | ------------------------------------------------------------ |
| `url`                         | String | 是       | -      | HTTP 请求的 URL。                                            |
| `email`                       | String | 是       | -      | Jira 邮箱地址。                                              |
| `api_token`                   | String | 是       | -      | Jira API 接口令牌。获取方式：[Jira API 令牌](https://id.atlassian.com/manage-profile/security/api-tokens)。 |
| `method`                      | String | 否       | `get`  | HTTP 请求方法，支持 `GET` 和 `POST`。                        |
| `params`                      | Map    | 否       | -      | HTTP 请求参数。                                              |
| `body`                        | String | 否       | -      | HTTP 请求体。                                                |
| `poll_interval_millis`        | int    | 否       | -      | 请求 API 的间隔时间（毫秒）。                                |
| `retry`                       | int    | 否       | -      | 请求失败时的最大重试次数。                                   |
| `retry_backoff_multiplier_ms` | int    | 否       | 100    | 重试退避时间倍数（毫秒）。                                   |
| `retry_backoff_max_ms`        | int    | 否       | 10000  | 重试退避最大时间（毫秒）。                                   |
| `format`                      | String | 否       | `json` | 上游数据的格式，目前仅支持 `json` 和 `text`。                |
| `schema.fields`               | Config | 否       | -      | 上游数据的字段定义。如果 `format` 为 `json`，需要配置此选项。 |
| `content_json`                | String | 否       | -      | 用于提取 JSON 数据的参数。                                   |
| `json_field`                  | Config | 否       | -      | 用于帮助配置 `schema` 的参数，必须与 `schema` 一起使用。     |
| `content_field`               | String | 否       | -      | 用于提取特定 JSON 数据的路径。                               |
| `common-options`              | config | 否       | -      | 源插件通用参数。                                             |

#### 示例配置
以下是一个 Jira 连接器的配置示例：

```properties
Jira {
    url = "https://liugddx.atlassian.net/rest/api/3/search"
    email = "test@test.com"
    api_token = "xxx"
    schema {
        fields {
            expand = string
            startAt = bigint
            maxResults = int
            total = int
        }
    }
}
```

#### 示例说明
- `url`：Jira API 的搜索接口 URL。
- `email`：用于 Jira API 认证的邮箱地址。
- `api_token`：Jira API 认证令牌。
- `schema`：定义了从 Jira 读取的数据的字段。

#### 变更日志
更多关于 Jira 连接器的变更日志和详细信息，请参考 [Jira 连接器文档](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/Jira/)。

#### 社区与支持
- **社区**：[Apache SeaTunnel 社区](https://seatunnel.apache.org/)
- **GitHub**：[Apache SeaTunnel GitHub 仓库](https://github.com/apache/seatunnel)
- **Issue Tracker**：[Apache SeaTunnel Issue Tracker](https://issues.apache.org/jira/browse/SEATUNNEL)
- **Pull Requests**：[Apache SeaTunnel Pull Requests](https://github.com/apache/seatunnel/pulls)

#### 版权信息
Apache SeaTunnel 是 Apache 软件基金会（ASF）的一个孵化项目，由 Apache Incubator 赞助。更多详情请参考 [Apache SeaTunnel 官方网站](https://seatunnel.apache.org/)。

---

希望这份说明文档对您有所帮助！如果您有任何其他问题或需要进一步的帮助，请随时联系。