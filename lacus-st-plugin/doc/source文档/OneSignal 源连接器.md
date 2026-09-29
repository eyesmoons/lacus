### OneSignal 连接器使用说明

#### 描述
OneSignal 连接器用于从 OneSignal 读取数据。

#### 主要特性
- 支持批量读取（batch）
- 支持流式读取（stream）
- 确保一次处理（exactly-once）
- 列投影（column projection）
- 并行处理（parallelism）
- 支持用户自定义分割（user-defined split）

#### 选项
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

#### 选项说明
- **url**: HTTP 请求的 URL。
- **password**: 登录认证密钥，更多详情请参考 [OneSignal 认证密钥](https://documentation.onesignal.com/docs/accounts-and-keys#user-auth-key)。
- **method**: HTTP 请求方法，仅支持 GET 和 POST 方法。
- **params**: HTTP 参数。
- **body**: HTTP 请求体。
- **poll_interval_millis**: 流式模式下请求 HTTP API 的间隔（毫秒）。
- **retry**: 如果请求 HTTP 返回 IOException，最大重试次数。
- **retry_backoff_multiplier_ms**: 请求 HTTP 失败时重试回退时间的乘数（毫秒）。
- **retry_backoff_max_ms**: 请求 HTTP 失败时最大重试回退时间（毫秒）。
- **format**: 上游数据的格式，目前仅支持 JSON 文本，默认为 JSON。
- **schema**: 上游数据的模式，当格式为 JSON 时，需要指定 schema。例如：
  ```json
  {
    "code": int,
    "data": string,
    "success": boolean
  }
  ```
  连接器将生成以下格式的数据：
  ```
  code        data        success
  200        get success        true
  ```
- **content_json**: 获取部分 JSON 数据。例如，如果只需要 'book' 部分的数据，可以配置 `content_field = $.store.book.*`。
- **json_field**: 配置 schema，必须与 schema 一起使用。例如：
  ```json
  {
    "store": {
      "book": [
        {
          "category": "reference",
          "author": "Nigel Rees",
          "title": "Sayings of the Century",
          "price": 8.95
        },
        {
          "category": "fiction",
          "author": "Evelyn Waugh",
          "title": "Sword of Honour",
          "price": 12.99
        }
      ],
      "bicycle": {
        "color": "red",
        "price": 19.95
      }
    },
    "expensive": 10
  }
  ```
  配置任务如下：
  ```json
  source {
    Http {
      url = "http://mockserver:1080/jsonpath/mock"
      method = "GET"
      format = "json"
      json_field = {
        "category": "$.store.book[*].category",
        "author": "$.store.book[*].author",
        "title": "$.store.book[*].title",
        "price": "$.store.book[*].price"
      }
      schema = {
        fields {
          category = string
          author = string
          title = string
          price = string
        }
      }
    }
  }
  ```
- **common-options**: 源插件通用参数，请参考源通用选项的详细信息。

#### 示例
```json
OneSignal {
  url = "https://onesignal.com/api/v1/apps"
  password = "SeaTunnel-test"
  schema = {
    fields {
      id = string
      name = string
      gcm_key = string
      chrome_key = string
      chrome_web_key = string
      chrome_web_origin = string
      chrome_web_gcm_sender_id = string
      chrome_web_default_notification_icon = string
      chrome_web_sub_domain = string
      apns_env = string
      apns_certificates = string
      apns_p8 = string
      apns_team_id = string
      apns_key_id = string
      apns_bundle_id = string
      safari_apns_certificate = string
      safari_site_origin = string
      safari_push_id = string
      safari_icon_16_16 = string
      safari_icon_32_32 = string
      safari_icon_64_64 = string
      safari_icon_128_128 = string
      safari_icon_256_256 = string
      site_name = string
      created_at = string
      updated_at = string
      players = int
      messageable_players = int
      basic_auth_key = string
      additional_data_is_root_payload = string
    }
  }
}
```

#### 更新日志
请参考 [Change Log](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/OneSignal#changelog)。

#### 版本信息
Apache SeaTunnel 是 Apache 软件基金会（ASF）孵化项目，由 Apache Incubator 赞助。孵化状态表示项目尚未完全得到 ASF 的认可，直到进一步审查表明其基础设施、通信和决策过程已稳定。

#### 版权信息
Copyright © 2021-2022 The Apache Software Foundation. Apache SeaTunnel, SeaTunnel, 和其羽毛标志是 The Apache Software Foundation 的商标。