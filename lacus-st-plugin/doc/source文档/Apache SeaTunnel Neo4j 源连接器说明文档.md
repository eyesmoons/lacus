根据您提供的链接内容，以下是对 Apache SeaTunnel Neo4j 源连接器的说明文档整理：

---

### Apache SeaTunnel Neo4j 源连接器说明文档

#### 简介
Apache SeaTunnel Neo4j 源连接器用于从 Neo4j 数据库中读取数据。它支持批处理和流处理模式，并确保精确一次数据处理。

#### 版本信息
- 版本：2.3.11
- neo4j-java-driver 版本：4.4.9

#### 主要功能
1. **批处理**：支持批量读取数据。
2. **流处理**：支持实时数据流读取。
3. **精确一次**：确保数据处理的一次性，避免数据重复或丢失。
4. **列投影**：允许指定需要返回的字段。
5. **并行度**：支持并行处理，提高数据读取效率。
6. **支持用户定义拆分**：允许用户自定义数据拆分逻辑。

#### 配置选项
以下是连接器的主要配置选项：

| 名称                       | 类型   | 是否必须 | 默认值 | 描述                                                     |
| -------------------------- | ------ | -------- | ------ | -------------------------------------------------------- |
| uri                        | String | 是       | -      | Neo4j 数据库的 URI，参考配置：`neo4j://localhost:7687`。 |
| username                   | String | 否       | -      | Neo4j 用户名。                                           |
| password                   | String | 否       | -      | Neo4j 密码。如果提供了“用户名”，则需要。                 |
| bearer_token               | String | 否       | -      | Neo4j 的 base64 编码 bearer token 用于鉴权。             |
| kerberos_ticket            | String | 否       | -      | Neo4j 的 base64 编码 kerberos ticket 用于鉴权。          |
| database                   | String | 是       | -      | 数据库名。                                               |
| query                      | String | 是       | -      | 查询语句。                                               |
| schema.fields              | String | 是       | -      | 返回 query 的字段。                                      |
| max_transaction_retry_time | Long   | 否       | 30     | 最大事务重试时间（秒）。如果超过，则事务失败。           |
| max_connection_timeout     | Long   | 否       | 30     | 等待 TCP 连接建立的最长时间（秒）。                      |

#### 示例配置
以下是一个示例配置，展示如何使用 Neo4j 源连接器：

```plaintext
source {
  Neo4j {
    uri = "neo4j://localhost:7687"
    username = "neo4j"
    password = "1234"
    database = "neo4j"
    max_transaction_retry_time = 1
    max_connection_timeout = 1
    query = "MATCH (a:Person) RETURN a.name, a.age"
    schema {
      fields {
        a.age=INT
        a.name=STRING
      }
    }
  }
}
```

#### 变更日志
- 更新内容、版本变更等信息可以在官方文档的变更日志部分查看。

#### 官方文档链接
- [Neo4j 源连接器官方文档](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/Neo4j/)

---

以上是对 Apache SeaTunnel Neo4j 源连接器的说明文档整理，希望对您有所帮助。如果您有任何其他问题或需要进一步的帮助，请随时联系。