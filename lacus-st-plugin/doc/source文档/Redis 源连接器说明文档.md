根据您提供的链接内容，Apache SeaTunnel 的 Redis 数据连接器（版本 2.3.11）主要用于从 Redis 数据库中读取数据。以下是整理的详细说明和使用指南：

### 描述
Redis 源连接器用于从 Redis 读取数据，支持批处理和流处理，确保精确一次的数据处理，并支持列投影和用户自定义分片。

### 主要功能
- **批处理**：支持批量读取数据。
- **流处理**：支持流式读取数据。
- **精确一次**：确保数据只被处理一次。
- **列投影**：支持指定需要读取的列。
- **并行度**：支持并行读取数据。
- **支持用户自定义分片**：允许用户自定义分片规则。

### 配置选项
以下是连接器的主要配置选项：

| 名称                | 类型   | 是否必须            | 默认值 | 说明                                                         |
| ------------------- | ------ | ------------------- | ------ | ------------------------------------------------------------ |
| host                | string | mode=single时必须   | -      | Redis 主机地址                                               |
| port                | int    | 否                  | 6379   | Redis 端口号                                                 |
| keys                | string | 是                  | -      | keys 模式，用于模糊匹配键                                    |
| batch_size          | int    | 是                  | 10     | 每次迭代尝试返回的键的数量                                   |
| data_type           | string | 是                  | -      | Redis 数据类型，支持 key, hash, list, set, zset              |
| user                | string | 否                  | -      | Redis 认证身份用户                                           |
| auth                | string | 否                  | -      | Redis 认证密钥                                               |
| db_num              | int    | 否                  | 0      | Redis 数据库索引 ID，默认连接到 db 0                         |
| mode                | string | 否                  | single | Redis 模式，single 或 cluster，默认值为 single               |
| nodes               | list   | mode=cluster 时必须 | -      | Redis 节点信息，在 cluster 模式下使用，格式为 ["host1:port1", "host2:port2"] |
| schema              | config | format=json 时必须  | -      | schema 配置，用于定义数据格式                                |
| format              | string | 否                  | json   | 上游数据格式，目前仅支持 json 和 text                        |
| hash_key_parse_mode | string | 否                  | all    | 指定 hash key 解析模式，支持 all 和 kv 模式                  |

### 详细说明
- **host**：Redis 主机地址，连接器必须连接到 Redis 服务器。
- **port**：Redis 端口号，默认为 6379。
- **keys**：用于模糊匹配键，例如 "key_test*"。
- **batch_size**：每次迭代尝试返回的键的数量，默认为 10。
- **data_type**：Redis 数据类型，支持 key, hash, list, set, zset。
  - **key**：将每个 key 的值作为单行数据发送给下游。
  - **hash**：hash 键值对将被格式化为 json，并以单行数据的形式发送给下游。
  - **list**：list 中的每个元素都将作为单行数据向下游发送。
  - **set**：set 中的每个元素都将作为单行数据向下游发送。
  - **zset**：zset 中的每个元素都将作为单行数据向下游发送。
- **user**：Redis 认证身份用户，当连接到加密集群时需要使用。
- **auth**：Redis 认证密钥，当连接到加密集群时需要使用。
- **db_num**：Redis 数据库索引 ID，默认将连接到 db 0。
- **mode**：Redis 模式，single 或 cluster，默认值为 single。
- **nodes**：Redis 节点信息，在 cluster 模式下使用，必须设置为以下格式：["host1:port1", "host2:port2"]。
- **schema**：schema 配置，用于定义数据格式，当指定 format 为 json 时必须指定。
- **format**：上游数据格式，目前仅支持 json 和 text。
- **hash_key_parse_mode**：指定 hash key 解析模式，支持 all 和 kv 模式。
  - **all**：连接器会将 hash key 的值视为一行并根据 schema config 配置进行解析。
  - **kv**：连接器会将 hash key 的每个 kv 视为一行，并根据 schema config 进行解析。

### 示例
以下是一些简单的使用示例：

#### 简单使用示例
```plaintext
Redis {
  host = localhost
  port = 6379
  keys = "key_test*"
  data_type = key
  format = text
}

Redis {
  host = localhost
  port = 6379
  keys = "key_test*"
  data_type = key
  format = json
  schema {
    fields {
      name = string
      age = int
    }
  }
}
```

#### 读取 string 类型并附加到 list 示例
```plaintext
source {
  Redis {
    host = "redis-e2e"
    port = 6379
    auth = "U2VhVHVubmVs"
    keys = "string_test*"
    data_type = string
    batch_size = 33
  }
}

sink {
  Redis {
    host = "redis-e2e"
    port = 6379
    auth = "U2VhVHVubmVs"
    key = "string_test_list"
    data_type = list
    batch_size = 33
  }
}
```

### 变更日志
更多关于连接器的变更日志和详细信息，请参考官方文档。

### 总结
Apache SeaTunnel 的 Redis 数据连接器是一个功能强大的工具，支持多种配置选项和数据处理模式，适用于从 Redis 数据库中高效读取数据。通过合理配置和使用，可以满足各种数据集成需求。