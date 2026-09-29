根据您提供的链接内容，以下是对 Apache SeaTunnel Prometheus 连接器的说明文档整理：

---

### Apache SeaTunnel Prometheus 连接器说明文档

#### 简介
Apache SeaTunnel Prometheus 连接器是一个用于读取 Prometheus 数据的数据源连接器。它支持批处理和流处理模式，并具有并行处理能力。

#### 主要特性
- **批处理**
- **流处理**
- **并行**

#### 源选项
连接器提供以下配置选项：

| 名称                        | 类型    | 是否必填 | 默认值          |
| --------------------------- | ------- | -------- | --------------- |
| url                         | String  | 是       | -               |
| query                       | String  | 是       | -               |
| query_type                  | String  | 是       | Instant         |
| content_field               | String  | 是       | $.data.result.* |
| schema.fields               | Config  | 是       | -               |
| format                      | String  | 否       | json            |
| params                      | Map     | 是       | -               |
| poll_interval_millis        | int     | 否       | -               |
| retry                       | int     | 否       | -               |
| retry_backoff_multiplier_ms | int     | 否       | 100             |
| retry_backoff_max_ms        | int     | 否       | 10000           |
| enable_multi_lines          | boolean | 否       | false           |
| common-options              | config  | 否       | -               |

#### 选项说明
- **url [String]**: HTTP 请求路径。
- **query [String]**: Prometheus 表达式查询字符串。
- **query_type [String]**: 查询类型，可以是 `Instant` 或 `Range`。
  - `Instant`: 简单指标的即时查询。
  - `Range`: 一段时间内指标数据。
- **params [Map]**: HTTP 请求参数。
- **poll_interval_millis [int]**: 流模式下请求 HTTP API 间隔（毫秒）。
- **retry [int]**: 请求 HTTP 返回 IOException 时的最大重试次数。
- **retry_backoff_multiplier_ms [int]**: 请求 HTTP 返回 'IOException' 的最大重试次数。
- **retry_backoff_max_ms [int]**: HTTP 请求失败，最大重试回退时间（毫秒）。
- **format [String]**: 上游数据的格式，默认为 json。
- **schema [Config]**: 按照如下填写一个固定值。
  ```json
  schema = {
    fields {
      metric = "map<string, string>"
      value = double
      time = long
    }
  }
  ```
- **fields [Config]**: 上游数据的模式字段。
- **common options**: 源插件常用参数，请参考 Source Common Options 了解详细信息。

#### 示例
##### Instant
```json
source {
  Prometheus {
    plugin_output = "http"
    url = "http://mockserver:1080"
    query = "up"
    query_type = "Instant"
    content_field = "$.data.result.*"
    format = "json"
    schema = {
      fields {
        metric = "map<string, string>"
        value = double
        time = long
      }
    }
  }
}
```

##### Range
```json
source {
  Prometheus {
    plugin_output = "http"
    url = "http://mockserver:1080"
    query = "up"
    query_type = "Range"
    content_field = "$.data.result.*"
    format = "json"
    start = "2024-07-22T20:10:30.781Z"
    end = "2024-07-22T20:11:00.781Z"
    step = "15s"
    schema = {
      fields {
        metric = "map<string, string>"
        value = double
        time = long
      }
    }
  }
}
```

#### 变更日志
有关版本的详细变更日志，请参考官方文档。

#### 版本信息
Apache SeaTunnel 是一个在 Apache 软件基金会（ASF）孵化中的项目，由 Apache Incubator 赞助。目前 SeaTunnel 处于孵化阶段，直到进一步审查表明其基础设施、通信和决策过程稳定为止。

#### 版权信息
Copyright © 2021-2022 The Apache Software Foundation. Apache SeaTunnel, SeaTunnel, 和其羽毛标志是 Apache 软件基金会的商标。

---

希望这份整理的说明文档对您有所帮助。如果您有其他问题或需要进一步的信息，请随时提问。