Apache SeaTunnel Http Connector 是一个用于从 HTTP 读取数据的源连接器。它支持批处理和流处理，能够精确保证一次性和列投影，并支持用户自定义分片。以下是 Http Connector 的主要特性和使用示例：

### 主要特性
- **批处理和流处理**：支持批处理和流处理模式，满足不同场景的数据读取需求。
- **精确一次**：保证数据读取的精确性，避免数据丢失或重复。
- **列投影**：支持列投影，只读取需要的列，提高数据读取效率。
- **并行度**：支持并行度配置，提高数据读取速度。
- **用户自定义分片**：支持用户自定义分片规则，灵活处理大数据量。

### 支持的数据源信息
为了使用 Http 连接器，需要以下依赖项：
- 可以通过 `install-plugin.sh` 脚本或从 Maven 中央仓库下载。

### 源选项
以下是 Http Connector 的主要源选项：

| 名称                                | 类型    | 是否必须 | 默认值     | 描述                                                         |
| ----------------------------------- | ------- | -------- | ---------- | ------------------------------------------------------------ |
| url                                 | String  | 是       | -          | Http 请求 URL。                                              |
| schema                              | Config  | 否       | -          | Http 和 SeaTunnel 数据结构映射。                             |
| schema.fields                       | Config  | 否       | -          | 上游数据的 schema 字段。                                     |
| json_field                          | Config  | 否       | -          | 此参数帮助您配置 schema，因此此参数必须与 schema 一起使用。  |
| pageing                             | Config  | 否       | -          | 此参数用于分页查询。                                         |
| pageing.page_field                  | String  | 否       | -          | 此参数用于指定请求中的页面字段名称。                         |
| pageing.use_placeholder_replacement | Boolean | 否       | false      | 如果为 true，则使用占位符替换（${field}）用于 headers、parameters 和 body 值，否则使用基于键的替换。 |
| pageing.total_page_size             | Int     | 否       | -          | 此参数用于控制总页数。                                       |
| pageing.batch_size                  | Int     | 否       | -          | 每个请求返回的批量大小，用于在总页数未知时确定是否继续。     |
| pageing.start_page_number           | Int     | 否       | 1          | 指定同步开始的页码。                                         |
| pageing.page_type                   | String  | 否       | PageNumber | 此参数用于指定页面类型，如果未设置则为 PageNumber，仅支持 PageNumber 和 Cursor。 |
| pageing.cursor_field                | String  | 否       | -          | 此参数用于指定请求参数中的游标字段名称。                     |
| pageing.cursor_response_field       | String  | 否       | -          | 此参数指定从中检索游标的响应字段。                           |
| content_json                        | String  | 否       | -          | 此参数可以获取一些 json 数据。                               |
| format                              | String  | 否       | text       | 上游数据的格式，目前仅支持 json text，默认为 text。          |
| method                              | String  | 否       | get        | Http 请求方法，仅支持 GET、POST 方法。                       |
| headers                             | Map     | 否       | -          | Http 头信息。                                                |
| params                              | Map     | 否       | -          | Http 参数。                                                  |
| body                                | String  | 否       | -          | Http 请求体，程序将自动添加 http header application/json，body 是 jsonbody。 |
| poll_interval_millis                | Int     | 否       | -          | 流模式下请求 http api 的间隔（毫秒）。                       |
| retry                               | Int     | 否       | -          | 如果请求 http 返回 IOException 的最大重试次数。              |
| retry_backoff_multiplier_ms         | Int     | 否       | 100        | 请求 http 失败时的重试退避时间（毫秒）乘数。                 |
| retry_backoff_max_ms                | Int     | 否       | 10000      | 请求 http 失败时的最大重试退避时间（毫秒）。                 |
| enable_multi_lines                  | Boolean | 否       | false      |                                                              |
| connect_timeout_ms                  | Int     | 否       | 12000      | 连接超时设置，默认 12 秒。                                   |
| socket_timeout_ms                   | Int     | 否       | 60000      | Socket 超时设置，默认 60 秒。                                |
| common-options                      | 否      | -        | -          | 源插件通用参数，请参考 Source Common Options 获取详细信息。  |
| keep_params_as_form                 | Boolean | 否       | false      | 是否按照表单提交参数，用于兼容旧行为。                       |
| keep_page_param_as_http_param       | Boolean | 否       | false      | 是否将分页参数设置为 params。用于兼容旧行为。                |

### 如何创建 Http 数据同步作业
以下是一个简单的 Http 数据同步作业配置示例：

```json
{
  "env": {
    "parallelism": 1,
    "job.mode": "BATCH"
  },
  "source": {
    "Http": {
      "plugin_output": "http",
      "url": "http://mockserver:1080/example/http",
      "method": "GET",
      "format": "json",
      "schema": {
        "fields": {
          "c_map": "map<string, string>",
          "c_array": "array<int>",
          "c_string": "string",
          "c_boolean": "boolean",
          "c_tinyint": "tinyint",
          "c_smallint": "smallint",
          "c_int": "int",
          "c_bigint": "bigint",
          "c_float": "float",
          "c_double": "double",
          "c_bytes": "bytes",
          "c_date": "date",
          "c_decimal": "decimal(38, 18)",
          "c_timestamp": "timestamp",
          "c_row": {
            "C_MAP": "map<string, string>",
            "C_ARRAY": "array<int>",
            "C_STRING": "string",
            "C_BOOLEAN": "boolean",
            "C_TINYINT": "tinyint",
            "C_SMALLINT": "smallint",
            "C_INT": "int",
            "C_BIGINT": "bigint",
            "C_FLOAT": "float",
            "C_DOUBLE": "double",
            "C_BYTES": "bytes",
            "C_DATE": "date",
            "C_DECIMAL": "decimal(38, 18)",
            "C_TIMESTAMP": "timestamp"
          }
        }
      }
    }
  },
  "sink": {
    "Console": {
      "parallelism": 1
    }
  }
}
```

### 参数解释
- **format**：当您指定 format 为 json 时，您还应该指定 schema 选项。例如，如果上游数据如下：

```json
{
  "code": 200,
  "data": "get success",
  "success": true
}
```

您应该指定 schema 如下：

```json
{
  "fields": {
    "code": "int",
    "data": "string",
    "success": "boolean"
  }
}
```

连接器将生成如下数据：

```
code        data        success
200        get success        true
```

当您指定 format 为 text 时，连接器不会对上游数据做任何处理。例如，如果上游数据如下：

```json
{
  "code": 200,
  "data": "get success",
  "success": true
}
```

连接器将生成如下数据：

```
content
{"code": 200, "data": "get success", "success": true}
```

- **keep_params_as_form**：为了兼容旧版本的 http。当设置为 true 时，params 和 pageing 将以表单形式提交。当设置为 false 时，params 将添加到 url 路径中，而 pageing 不会添加到 body 或表单中。它将替换 params 和 body 中的占位符。
- **keep_page_param_as_http_param**：是否将分页参数设置为 params。当设置为 true 时，pageing 设置为 params。当设置为 false 时，当页面字段存在于 body 或 params 中时，替换值。
- **params**：默认情况下，参数将添加到 url 路径中。如果您需要保持旧版本行为，请检查 keep_params_as_form。
- **body**：HTTP body 用于在请求或响应中携带实际数据，包括 JSON、表单提交。
- **content_json**：此参数可以获取一些 json 数据。如果您只需要 'book' 部分的数据，配置 content_field = “$.store.book.*”。
- **json_field**：此参数帮助您配置 schema，因此此参数必须与 schema 一起使用。
- **pageing**：当前支持的分页类型是 PageNumber 和 Cursor。如果您需要使用分页，您需要配置 pageing。默认分页类型是 PageNumber。

### 示例
以下是一些使用 Http Connector 的示例：

1. **使用 PageNumber 分页**：
   - 在 URL 参数中：将页面参数添加到 params 部分。
   - 在请求体中：在 body JSON 中包含页面参数。
   - 在头信息中：将页面参数添加到 headers 部分。

2. **使用 Cursor 分页**：
   - 设置 `pageing.page_type` 为 "Cursor"。
   - 指定 `cursor_field` 和 `cursor_response_field`。

通过以上说明文档，您应该能够了解和使用 Apache SeaTunnel Http Connector 进行 HTTP 数据源连接。