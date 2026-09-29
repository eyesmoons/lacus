根据您提供的链接内容，以下是对 Apache SeaTunnel Easysearch 连接器的详细说明和用法的整理：

### Easysearch 连接器

#### 支持的引擎
- Spark
- Flink
- SeaTunnel Zeta

#### 描述
Easysearch 源连接器用于从 INFINI Easysearch 读取数据。

#### 使用依赖
- 依赖 easysearch-client

#### 关键特性
- 批处理
- 流处理
- 精确一次
- 列映射
- 并行度
- 支持用户自定义拆分

#### 支持的引擎版本
支持发布的所有版本 INFINI Easysearch。

#### 数据类型映射
| Easysearch 数据类型 | SeaTunnel 数据类型   |
| ------------------- | -------------------- |
| STRING              | STRING               |
| KEYWORD             | STRING               |
| TEXT                | STRING               |
| BOOLEAN             | BOOLEAN              |
| BYTE                | BYTE                 |
| SHORT               | SHORT                |
| INTEGER             | INT                  |
| LONG                | LONG                 |
| FLOAT               | FLOAT                |
| HALF_FLOAT          | FLOAT                |
| DOUBLE              | DOUBLE               |
| Date                | LOCAL_DATE_TIME_TYPE |

#### 参数说明
- **hosts** (array): Easysearch 集群 http 地址，格式为 "host:port"，允许指定多个主机。例如 ["host1:9200","host2:9200"]。
- **username** (string): 安全用户名。
- **password** (string): 安全密码。
- **index** (string): Easysearch 搜索索引名称，支持 * 模糊匹配。
- **source** (array): 索引字段。可以通过指定字段 "_id" 来获取文档 id。如果 sink_id 指向其他索引，由于 Easysearch 的限制，您需要为 _id 指定一个别名。若不配置 source，则必须配置 schema。
- **query** (json): Easysearch DSL。可以控制读取数据的范围。
- **scroll_time** (String): Easysearch 将为滚动请求保持搜索上下文活动的时间量。
- **scroll_size** (int): 每次 Easysearch 滚动请求返回的最大请求数。
- **schema**: 数据的结构，包括字段名和字段类型。如果不配置 schema，则必须配置 source。
- **tls_verify_certificate** (boolean): 为 HTTPS 端点启用证书验证。
- **tls_verify_hostname** (boolean): 为 HTTPS 端点启用主机名验证。
- **tls_keystore_path** (string): PEM 或 JKS 密钥存储的路径。运行 SeaTunnel 的操作系统用户必须能够读取此文件。
- **tls_keystore_password** (string): 指定密钥存储的密钥密码。
- **tls_truststore_path** (string): PEM 或 JKS 信任存储的路径。运行 SeaTunnel 的操作系统用户必须能够读取此文件。
- **tls_truststore_password** (string): 指定的信任存储的密钥密码。

#### 常用参数
Source 插件常用参数，详见 [Source common Options]（../source-common-options.md）。

#### 示例
##### 简单的例子
```plaintext
Easysearch {
  hosts = ["localhost:9200"]
  index = "seatunnel-*"
  source = ["_id","name","age"]
  query = {"range":{"firstPacket":{"gte":1700407367588,"lte":1700407367588}}}
}
```

##### 复杂的例子
```plaintext
Easysearch {
  hosts = ["Easysearch:9200"]
  index = "st_index"
  schema = {
    fields {
      c_map = "map<string, tinyint>"
      c_array = "array<tinyint>"
      c_string = string
      c_boolean = boolean
      c_tinyint = tinyint
      c_smallint = smallint
      c_int = int
      c_bigint = bigint
      c_float = float
      c_double = double
      c_decimal = "decimal(2, 1)"
      c_bytes = bytes
      c_date = date
      c_timestamp = timestamp
    }
  }
  query = {"range":{"firstPacket":{"gte":1700407367588,"lte":1700407367588}}}
}
```

##### SSL (禁用证书验证)
```plaintext
source {
  Easysearch {
    hosts = ["https://localhost:9200"]
    username = "admin"
    password = "admin"
    tls_verify_certificate = false
  }
}
```

##### SSL (禁用主机名验证)
```plaintext
source {
  Easysearch {
    hosts = ["https://localhost:9200"]
    username = "admin"
    password = "admin"
    tls_verify_hostname = false
  }
```

##### SSL (启用证书验证)
```plaintext
source {
  Easysearch {
    hosts = ["https://localhost:9200"]
    username = "admin"
    password = "admin"
    tls_keystore_path = "${your Easysearch home}/config/certs/http.p12"
    tls_keystore_password = "${your password}"
  }
}
```

#### 变更日志
- [Change Log](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/changelog/connector-easysearch/)

#### 版本信息
- Apache SeaTunnel 是一个在 Apache 软件基金会（ASF）孵化器中的项目，由 Apache Incubator 赞助。孵化期要求所有新接受的项目，直到进一步审查表明其基础设施、通信和决策过程已经稳定，与其它成功的 ASF 项目一致。

#### 版权信息
- Copyright © 2021-2022 The Apache Software Foundation. Apache SeaTunnel, SeaTunnel, and its feather logo are trademarks of The Apache Software Foundation.

希望这份整理对您有所帮助！如果您有任何其他问题，请随时提问。