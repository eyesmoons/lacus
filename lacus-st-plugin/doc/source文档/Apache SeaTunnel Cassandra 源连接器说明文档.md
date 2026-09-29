根据您提供的链接内容，以下是 Apache SeaTunnel Cassandra 源连接器的说明文档：

### Apache SeaTunnel Cassandra 源连接器

#### 描述
Apache SeaTunnel Cassandra 源连接器用于从 Apache Cassandra 数据库中读取数据。它支持批处理和流处理，能够确保数据的精确一次处理，并支持列投影和用户自定义分片。

#### 关键特性
- **批处理**：支持批量读取数据。
- **流处理**：支持实时数据流读取。
- **精确一次**：确保数据在读取过程中只被处理一次。
- **列投影**：支持指定读取的列。
- **并行度**：支持并行读取以提高性能。
- **支持用户自定义分片**：允许用户定义数据的分片规则。

#### 选项
以下是连接器支持的各种配置选项：

| 名称              | 类型   | 必需 | 默认值      |
| ----------------- | ------ | ---- | ----------- |
| host              | String | 是   | -           |
| keyspace          | String | 是   | -           |
| cql               | String | 是   | -           |
| username          | String | 否   | -           |
| password          | String | 否   | -           |
| datacenter        | String | 否   | datacenter1 |
| consistency_level | String | 否   | LOCAL_ONE   |

#### 选项详细说明
- **host [string]**：Cassandra 的集群地址，格式为 `host:port`，允许指定多个 hosts。例如 `cassandra1:9042,cassandra2:9042`。
- **keyspace [string]**：Cassandra 的键空间。
- **cql [String]**：查询 CQL，用于通过 Cassandra 会话搜索数据。
- **username [string]**：Cassandra 用户的用户名。
- **password [string]**：Cassandra 用户的密码。
- **datacenter [String]**：Cassandra 数据中心，默认为 `datacenter1`。
- **consistency_level [String]**：Cassandra 的写入一致性级别，默认为 `LOCAL_ONE`。

#### 示例
以下是一个配置示例，展示如何使用 Cassandra 源连接器：

```plaintext
source {
  Cassandra {
    host = "localhost:9042"
    username = "cassandra"
    password = "cassandra"
    datacenter = "datacenter1"
    keyspace = "test"
    cql = "select * from source_table"
    plugin_output = "source_table"
  }
}
```

在这个示例中，连接器配置为连接到本地 Cassandra 集群，使用默认的 `datacenter1` 数据中心，读取 `test` 键空间中的 `source_table` 表。

#### 变更日志
- 本文档基于 Apache SeaTunnel 2.3.11 版本。

#### 社区与支持
- Apache SeaTunnel 是一个在 Apache 软件基金会（ASF）孵化器中的项目，由 Apache Incubator 赞助。
- 社区资源包括版本信息、FAQ、GitHub 仓库、问题追踪器、拉取请求和邮件组订阅。

#### 版权信息
- 版权 © 2021-2022 The Apache Software Foundation.
- Apache SeaTunnel、SeaTunnel 及其羽毛标志是 The Apache Software Foundation 的商标。

以上是 Apache SeaTunnel Cassandra 源连接器的详细说明文档。希望这能帮助您更好地理解和使用该连接器。