Apache SeaTunnel 是一个高性能、分布式的数据集成框架，支持多种数据源的连接。Amazon SQS 源连接器是 SeaTunnel 的一部分，用于从 Amazon Simple Queue Service (SQS) 读取数据。以下是关于 Amazon SQS 连接器的功能、配置和使用方法的说明。

### 功能特性
1. **批处理**：支持批量读取数据。
2. **流处理**：支持流式读取数据。
3. **精确一次**：确保数据精确处理一次，不丢失。
4. **列投影**：支持指定需要读取的列。
5. **并行度**：支持并行读取数据，提高处理效率。
6. **支持用户自定义分片**：允许用户自定义数据分片规则。

### 描述
AmazonSqs 源连接器用于从 Amazon SQS 读取数据。它支持 Spark、Flink 和 SeaTunnel Zeta 等多种处理引擎。

### 源选项
以下是 Amazon SQS 源连接器的配置选项：

| 名称                    | 类型   | 必需 | 默认值 | 描述                                                         |
| ----------------------- | ------ | ---- | ------ | ------------------------------------------------------------ |
| url                     | String | 是   | -      | 从 Amazon SQS 读取的队列 URL。                               |
| region                  | String | 否   | -      | SQS 服务的 AWS 分区。                                        |
| schema                  | Config | 否   | -      | 数据的结构，包括字段名和字段类型。                           |
| format                  | String | 否   | json   | 数据格式。默认格式为 json。可选文本格式、canal-json 和 debezium-json。 |
| format_error_handle_way | String | 否   | fail   | 数据格式错误的处理方法。默认值为 fail，可选值为 fail 和 skip。 |
| field_delimiter         | String | 否   | ,      | 自定义数据格式的字段分隔符。                                 |
| common-options          | 否     | -    | -      | 源插件常用参数，详见源通用选项。                             |

### 任务示例
以下是一个使用 Amazon SQS 源连接器的任务示例：

```plaintext
source {
  AmazonSqs {
    url = "http://127.0.0.1:4566"
    region = "us-east-1"
    format = text
    field_delimiter = "#"
    schema = {
      fields {
        artist = string
        c_map = "map<string, array<int>>"
        c_array = "array<int>"
        c_string = string
        c_boolean = boolean
        c_tinyint = tinyint
        c_smallint = smallint
        c_int = int
        c_bigint = bigint
        c_float = float
        c_double = double
        c_decimal = "decimal(30, 8)"
        c_null = "null"
        c_bytes = bytes
        c_date = date
        c_timestamp = timestamp
      }
    }
  }
}

transform {
  # 如果你想了解更多关于如何配置 SeaTunnel 的信息，并查看转换插件的完整列表,
  # 请前往 https://seatunnel.apache.org/docs/transform-v2/sql
}

sink {
  Console {}
}
```

### 变更日志
请参考 SeaTunnel 的官方文档获取最新的变更日志。

### 社区与支持
- **社区**：Apache SeaTunnel 社区提供各种资源和支持。
- **GitHub**：[Apache SeaTunnel GitHub 仓库](https://github.com/apache/seatunnel)
- **Issue Tracker**：[Apache SeaTunnel Issue Tracker](https://issues.apache.org/jira/browse/SEATUNNEL)
- **Pull Requests**：[Apache SeaTunnel Pull Requests](https://github.com/apache/seatunnel/pulls)
- **订阅邮件组**：[Apache SeaTunnel 邮件组](https://mailchimp.com/lists/public/)

### 版权信息
Apache SeaTunnel 是 Apache 软件基金会（ASF）的一个孵化项目，由 Apache Incubator 赞助。孵化项目是指所有新接受的项目，直到进一步审查表明其基础设施、通信和决策过程已稳定，与其它成功的 ASF 项目一致。孵化状态并不一定反映代码的完整性或稳定性，但它表明该项目尚未得到 ASF 的完全认可。

版权 © 2021-2022 The Apache Software Foundation. Apache SeaTunnel、SeaTunnel 及其羽毛标志是 The Apache Software Foundation 的商标。