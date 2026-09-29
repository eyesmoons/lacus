根据您提供的链接内容，以下是对 Apache SeaTunnel MongoDB 连接器的说明文档整理：

### MongoDB 连接器说明文档

#### 简介
MongoDB 连接器提供了从 MongoDB 读取数据和向 MongoDB 写入数据的能力。本文档描述了如何设置 MongoDB 连接器以对 MongoDB 运行数据读取。

#### 支持的引擎
- Spark
- Flink
- SeaTunnel Zeta

#### 关键特性
- 批处理和流处理
- 精确一次
- 列投影
- 并行性
- 支持用户自定义 split 描述

#### 支持的数据源信息
为了使用 MongoDB 连接器，需要以下依赖关系：
- 可以通过 install-plugin.sh 或 Maven 中央存储库下载。

#### 数据类型映射
下表列出了从 MongoDB BSON 类型到 SeaTunnel 数据类型的字段数据类型映射：

| MongoDB BSON type | SeaTunnel 数据类型 |
| ----------------- | ------------------ |
| ObjectId          | STRING             |
| String            | STRING             |
| Boolean           | BOOLEAN            |
| Binary            | BINARY             |
| Int32             | INTEGER            |
| Int64             | BIGINT             |
| Double            | DOUBLE             |
| Decimal128        | DECIMAL            |
| Date              | DATE               |
| Timestamp         | TIMESTAMP          |
| Object            | ROW                |
| Array             | ARRAY              |

对于 MongoDB 中的特定类型，我们使用扩展 JSON 格式将其映射到 SeaTunnel STRING 类型。

#### 源配置项
- **uri** (String, 必须): MongoDB 标准连接 URI。例如：`mongodb://user:password@hosts:27017/database?readPreference=secondary&slaveOk=true`
- **database** (String, 必须): 要读取或写入的 MongoDB 数据库的名称。
- **collection** (String, 必须): 要读取或写入的 MongoDB 集合的名称。
- **schema** (String, 必须): MongoDB 的 BSON 和 SeaTunnel 数据结构映射。
- **match.query** (String, 否): 在 MongoDB 中，过滤器用于过滤查询操作的文档。
- **match.projection** (String, 否): 在 MongoDB 中，投影用于控制查询结果中包含的字段。
- **partition.split-key** (String, 否): _id 分片字段。
- **partition.split-size** (Long, 否): 分片大小，默认值为 64, 1024, 1024。
- **cursor.no-timeout** (Boolean, 否): MongoDB 服务器通常在非活动期（10分钟）后超时空闲游标，以防止过度使用内存。将此选项设置为 true 以防止这种情况发生。
- **fetch.size** (Int, 否): 设置每批从服务器获取的文档数量。
- **max.time-min** (Long, 否): 此参数是一个 MongoDB 查询选项，用于限制查询操作的最大执行时间。
- **flat.sync-string** (Boolean, 否): 通过使用 flatSyncString，只能设置一个字段属性值，字段类型必须是 String。
- **common-options** (否): 源插件常用参数，请参考源通用选项。

#### 如何创建 MongoDB 数据同步作业
以下示例演示了如何创建数据同步作业，该作业从 MongoDB 读取数据并将其打印到本地客户端：

```plaintext
# 设置要执行的任务的基本配置
env {
  parallelism = 1
  job.mode = "BATCH"
}

# 创建 MongoDB 源
source {
  MongoDB {
    uri = "mongodb://user:password@127.0.0.1:27017"
    database = "test_db"
    collection = "source_table"
    schema = {
      fields {
        c_map = "map<string, string>"
        c_array = "array<int>"
        c_string = string
        c_boolean = boolean
        c_int = int
        c_bigint = bigint
        c_double = double
        c_bytes = bytes
        c_date = date
        c_decimal = "decimal(38, 18)"
        c_timestamp = timestamp
        c_row = {
          c_map = "map<string, string>"
          c_array = "array<int>"
          c_string = string
          c_boolean = boolean
          c_int = int
          c_bigint = bigint
          c_double = double
          c_bytes = bytes
          c_date = date
          c_decimal = "decimal(38, 18)"
          c_timestamp = timestamp
        }
      }
    }
  }
}

# 控制台打印读取的 MongoDB 数据
sink {
  Console {
    parallelism = 1
  }
}
```

#### 参数说明
- **MongoDB 数据库连接 URI 示例**:
  - 未经身份验证的单节点连接：`mongodb://192.168.0.100:27017/mydb`
  - 副集连接：`mongodb://192.168.0.100:27017/mydb?replicaSet=xxx`
  - 经过身份验证的副集连接：`mongodb://admin:password@192.168.0.100:27017/mydb?replicaSet=xxx&authSource=admin`
  - 多节点副集连接：`mongodb://192.168.0.1:27017,192.168.0.2:27017,192.168.0.3:27017/mydb?replicaSet=xxx`
  - 分片集群连接：`mongodb://192.168.0.100:27017/mydb`
  - 多个 mongos 连接：`mongodb://192.168.0.1:27017,192.168.0.2:27017,192.168.0.3:27017/mydb`

#### 匹配查询扫描
在数据同步场景中，需要尽早使用 matchQuery 方法来减少后续操作员需要处理的文档数量，从而提高性能。

#### 投影扫描
在 MongoDB 中，Projection 用于控制查询结果中包含哪些字段。这可以通过指定哪些字段需要返回，哪些字段不需要返回来实现。

#### 分区扫描
为了加快并行源任务实例中的数据读取速度，SeaTunnel 为 MongoDB 集合提供了分区扫描功能。提供了以下分区策略：
- 用户可以通过设置用于分片字段的 `partition.split-key` 和用于分片大小的 `partition.split-size` 来控制数据分片。

#### Flat Sync String
通过使用 `flat.sync string`，只能设置一个字段属性值，并且字段类型必须是 string。此操作将对单个 MongoDB 数据条目执行字符串映射。

#### 总结
Apache SeaTunnel 的 MongoDB 连接器提供了强大的数据读取和写入功能，支持多种数据类型映射和配置选项，适用于批处理和流处理场景。通过合理配置和使用这些功能，可以高效地进行数据同步和转换任务。