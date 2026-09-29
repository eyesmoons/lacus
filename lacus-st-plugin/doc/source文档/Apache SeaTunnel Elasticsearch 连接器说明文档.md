Apache SeaTunnel 中 Elasticsearch 连接器的说明文档整理：

---

### **Apache SeaTunnel Elasticsearch 连接器说明文档**

#### **简介**
Apache SeaTunnel 的 Elasticsearch 连接器支持读取 Elasticsearch 2.x 和 8.x 版本之间的数据。它是一个强大的数据源连接器，适用于多种数据集成场景。

#### **核心功能**
1. **批处理与流处理**：支持批处理和流处理模式，满足不同场景的数据同步需求。
2. **精准一次**：确保数据在同步过程中的精确性和一致性。
3. **列投影**：允许用户指定需要读取的列，提高数据同步的灵活性。
4. **并行度**：支持自定义并行度，优化数据处理性能。
5. **自定义分片**：支持用户自定义的分片策略，适应复杂的集群环境。

#### **配置参数选项**
以下是 Elasticsearch 连接器的主要配置参数：

| 参数名称                | 类型   | 是否必须 | 默认值或描述                                                 |
| ----------------------- | ------ | -------- | ------------------------------------------------------------ |
| hosts                   | 数组   | 是       | Elasticsearch 集群的 HTTP 地址，格式为 host:port             |
| username                | 字符串 | 否       | 用户名                                                       |
| password                | 字符串 | 否       | 密码                                                         |
| index                   | 字符串 | 否       | 单索引同步配置，如果 index_list 没有配置，则必须配置 index   |
| index_list              | 数组   | 否       | 用来定义多索引同步任务                                       |
| source                  | 数组   | 否       | 索引的字段                                                   |
| query                   | JSON   | 否       | Elasticsearch 原生查询语句，用于控制读取哪些数据写入到其他数据源 |
| search_type             | 枚举   | 否       | 查询类型，可选值：DSL（默认）或 SQL                          |
| search_api_type         | 枚举   | 否       | 分页 API 类型，可选值：SCROLL（默认）或 PIT                  |
| sql_query               | JSON   | 否       | SQL 查询语句，当 search_type 为 SQL 时必须                   |
| scroll_time             | 字符串 | 否       | 1m                                                           |
| scroll_size             | 整型   | 否       | 100                                                          |
| tls_verify_certificate  | 布尔型 | 否       | 启用 HTTPS 端点的证书验证                                    |
| tls_verify_hostname     | 布尔型 | 否       | 启用 HTTPS 端点的主机名验证                                  |
| array_column            | 映射   | 否       | 指定数组类型字段                                             |
| tls_keystore_path       | 字符串 | 否       | PEM 或 JKS 密钥库的路径                                      |
| tls_keystore_password   | 字符串 | 否       | 指定密钥库的密钥密码                                         |
| tls_truststore_path     | 字符串 | 否       | PEM 或 JKS 信任库的路径                                      |
| tls_truststore_password | 字符串 | 否       | 指定信任库的密钥密码                                         |
| pit_keep_alive          | 长整型 | 否       | PIT 应保持活动的时间量（以毫秒为单位）                       |
| pit_batch_size          | 长整型 | 否       | 每次 PIT 搜索请求返回的最大数量                              |
| common-options          | 否     | 否       | Source 插件常用参数                                          |

#### **参数详细说明**
- **hosts**：Elasticsearch 集群的 HTTP 地址，格式为 host:port，可以指定多个主机。
- **username** 和 **password**：用于认证的 用户名和密码。
- **index**：Elasticsearch 索引名称，支持模糊匹配。
- **source**：索引的字段，可以通过指定字段 _id 来获取文档 ID。如果将 _id 写入到其他索引，由于 Elasticsearch 的限制，需要为 _id 指定一个别名。
- **array_column**：由于 Elasticsearch 中没有数组索引，因此需要指定数组类型。例如，假设 tags 和 phones 是数组类型：`array_column = {tags = "array<cstring>", phones = "array<cstring>"}`
- **query**：Elasticsearch 原生查询语句，用于控制读取哪些数据写入到其他数据源。
- **search_type**：查询类型，可选值：DSL（默认）或 SQL。
- **search_api_type**：分页 API 类型，可选值：SCROLL（默认）或 PIT。
- **sql_query**：SQL 查询语句，当 search_type 为 SQL 时必须。
- **scroll_time**：Seatunnel 底层会使用滚动查询来查询数据，所以需要使用这个参数控制搜索上下文的时间长度。
- **scroll_size**：滚动查询的最大文档数量。
- **tls_verify_certificate** 和 **tls_verify_hostname**：分别用于启用 HTTPS 端点的证书验证和主机名验证。
- **tls_keystore_path**、**tls_keystore_password**、**tls_truststore_path** 和 **tls_truststore_password**：用于配置 SSL/TLS 的密钥库路径和密码。
- **pit_keep_alive** 和 **pit_batch_size**：PIT 应保持活动的时间量和每次 PIT 搜索请求返回的最大数量。

#### **使用案例**
1. **案例一**：从满足 `seatunnel-*` 匹配的索引中按照 `query` 读取数据，查询只会返回文档 ID、name、age、tags、phones 三个字段。在这个例子中，使用了 `source` 字段配置应该读取哪些字段，使用 `array_column` 指定了 tags，phones 应该被当做数组处理。

   ```json
   Elasticsearch {
       hosts = ["localhost:9200"]
       index = "seatunnel-*"
       array_column = {tags = "array<cstring>", phones = "array<cstring>"}
       source = ["_id", "name", "age", "tags", "phones"]
       query = {"range": {"firstPacket": {"gte": 1669225429990, "lte": 1669225429990}}}
   }
   ```

2. **案例二：多索引同步**：此示例演示了如何从 `read_index1` 和 `read_index2` 中读取不同的数据数据，并将其分别写入 `read_index1_copy`, `read_index12_copy` 索引。

   ```json
   source {
       Elasticsearch {
           hosts = ["https://elasticsearch:9200"]
           username = "elastic"
           password = "elasticsearch"
           tls_verify_certificate = false
           tls_verify_hostname = false
           index_list = [
               {
                   index = "read_index1"
                   query = {"range": {"c_int": {"gte": 10, "lte": 20}}}
                   source = ["c_map", "c_array", "c_string", "c_boolean", "c_tinyint", "c_smallint", "c_bigint", "c_float", "c_double", "c_decimal", "c_bytes", "c_int", "c_date", "c_timestamp"]
                   array_column = {c_array = "array<tinyint>"}
               },
               {
                   index = "read_index2"
                   query = {"match_all": {}}
                   source = ["c_int2", "c_date2", "c_null"]
               }
           ]
       }
   }
   ```

3. **案例三：SSL（禁用证书验证）**

   ```json
   source {
       Elasticsearch {
           hosts = ["https://localhost:9200"]
           username = "elastic"
           password = "elasticsearch"
           tls_verify_certificate = false
       }
   }
   ```

4. **案例四：SSL（禁用主机名验证）**

   ```json
   source {
       Elasticsearch {
           hosts = ["https://localhost:9200"]
           username = "elastic"
           password = "elasticsearch"
           tls_verify_hostname = false
       }
   }
   ```

5. **案例五：SSL（启用证书验证）**

   ```json
   source {
       Elasticsearch {
           hosts = ["https://localhost:9200"]
           username = "elastic"
           password = "elasticsearch"
           tls_keystore_path = "${your elasticsearch home}/config/certs/http.p12"
           tls_keystore_password = "${your password}"
       }
   }
   ```

6. **案例六：sql 方式查询**：注意，sql 查询不支持 map 和数组类型。

   ```json
   source {
       Elasticsearch {
           hosts = ["https://elasticsearch:9200"]
           username = "elastic"
           password = "elasticsearch"
           tls_verify_certificate = false
           tls_verify_hostname = false
           index = "st_index_sql"
           sql_query = "select * from st_index_sql where c_int>10 and c_int<20"
           search_type = "sql"
       }
   }
   ```

7. **Demo7: PIT 方式滚动查询**

   ```json
   source {
       Elasticsearch {
           hosts = ["https://elasticsearch:9200"]
           username = "elastic"
           password = "elasticsearch"
           tls_verify_certificate = false
           tls_verify_hostname = false
           index = "st_index"
           query = {"range": {"c_int": {"gte": 10, "lte": 20}}}
           # 使用 DSL 查询和 PIT API
           search_type = DSL
           search_api_type = PIT
           pit_keep_alive = 60000 # 1 minute in milliseconds
           pit_batch_size = 100
       }
   }
   ```

---

以上是对 Apache SeaTunnel 中 Elasticsearch 连接器的详细说明文档整理。希望这些信息能帮助您更好地理解和使用该连接器。