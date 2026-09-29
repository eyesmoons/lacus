根据您提供的链接内容，以下是Apache SeaTunnel中Qdrant数据源连接器的说明文档：

### Qdrant 数据源连接器

**简介**
Qdrant是一个高性能的向量搜索引擎和向量数据库。该连接器可用于从Qdrant集合中读取数据。

**选项**

| 名称            | 类型   | 必填 | 默认值    |
| --------------- | ------ | ---- | --------- |
| collection_name | string | 是   | -         |
| schema          | config | 是   | -         |
| host            | string | 否   | localhost |
| port            | int    | 否   | 6334      |
| api_key         | string | 否   | -         |
| use_tls         | bool   | 否   | false     |

**详细说明**

- **collection_name (string)**: 要从中读取数据的Qdrant集合的名称。
- **schema (config)**: 将要读取到的表的模式。例如：
  ```json
  schema = {
    "fields": {
      "age": int,
      "address": string,
      "some_vector": float_vector
    }
  }
  ```
  Qdrant中的每个条目称为一个点。`float_vector`类型的列从每个点的向量中读取，其他列从与该点关联的JSON有效负载中读取。如果列被标记为主键，Qdrant点的ID将写入其中。它可以是`string`或`int`类型。因为Qdrant仅允许使用正整数和UUID作为点ID。如果集合是用单个默认/未命名向量创建的，请使用`default_vector`作为向量名称。
  ```json
  schema = {
    "fields": {
      "age": int,
      "address": string,
      "default_vector": float_vector
    }
  }
  ```
  Qdrant中点的ID将写入标记为主键的列中。它可以是`int`或`string`类型。
- **host (string)**: Qdrant实例的主机名。默认为`localhost`。
- **port (int)**: Qdrant实例的gRPC端口。
- **api_key (string)**: 用于身份验证的API密钥（如果设置）。
- **use_tls (bool)**: 是否使用TLS（SSL）连接。如果使用Qdrant云（https），则需要。

**通用选项**
源插件的通用参数，请参考源通用选项了解详情。

**变更日志**
编辑此页
上一页
Apache Pulsar
下一页
Rabbitmq
SeaTunnel
FAQ
版本
社区
GitHub
Issue Tracker
Pull Requests
订阅邮件组
How to Subscribe
订阅邮件
邮件归档

**版权信息**
Copyright © 2021-2022 The Apache Software Foundation. Apache SeaTunnel, SeaTunnel, and its feather logo are trademarks of The Apache Software Foundation.

希望这份整理后的说明文档对您有所帮助。如果您有任何其他问题或需要进一步的帮助，请随时告诉我。