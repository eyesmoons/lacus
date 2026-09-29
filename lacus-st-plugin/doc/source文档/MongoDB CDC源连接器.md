Apache SeaTunnel 是一个开源的数据集成和数据管道工具，它支持多种数据源和目标，包括 MongoDB。MongoDB CDC（Change Data Capture）连接器允许应用程序订阅 MongoDB 数据库中的单个集合、数据库或整个部署上的所有数据更改，并立即对其做出反应。以下是对 Apache SeaTunnel MongoDB CDC 连接器的说明文档：

### 简介

MongoDB CDC 连接器提供了从 MongoDB 读取快照数据和增量数据的能力。它允许应用程序订阅 MongoDB 中的数据更改，并实时处理这些更改。

### 支持的数据源信息

为了使用 MongoDB CDC 连接器，需要以下依赖关系：

- MongoDB 客户端库

这些依赖关系可以通过 `install-plugin.sh` 或 Maven 中央存储库下载。

### 关键特性

- **批处理和流处理**：支持批处理和流处理模式，适用于不同的数据处理需求。
- **精确一次**：确保数据更改只被处理一次，避免重复处理。
- **列投影**：允许指定需要返回的字段，提高查询性能。
- **并行性**：支持并行处理，提高数据处理效率。
- **支持用户自定义 split**：允许用户自定义数据分片策略，优化数据读取性能。

### 配置选项

以下是一些主要的配置选项：

- **uri**：MongoDB 标准连接 URI，例如 `mongodb://user:password@hosts:27017/database?readPreference=secondary&slaveOk=true`。
- **database**：要读取或写入的 MongoDB 数据库的名称。
- **collection**：要读取或写入的 MongoDB 集合的名称。
- **schema**：MongoDB 的 BSON 和 SeaTunnel 数据结构映射。
- **match.query**：在 MongoDB 中，过滤器用于过滤查询操作的文档。
- **match.projection**：在 MongoDB 中，投影用于控制查询结果中包含的字段。
- **partition.split-key**：分片字段。
- **partition.split-size**：分片大小。
- **cursor.no-timeout**：MongoDB 服务器通常在非活动期（10 分钟）后超时空闲游标，以防止过度使用内存。将此选项设置为 `true` 以防止这种情况发生。
- **fetch.size**：设置每批从服务器获取的文档数量。
- **max.time-min**：此参数是一个 MongoDB 查询选项，用于限制查询操作的最大执行时间。
- **flat.sync-string**：通过使用 `flat.sync string`，只能设置一个字段属性值，并且字段类型必须是 `string`。

### 如何创建 MongoDB 数据同步作业

以下是一个示例，演示了如何创建一个数据同步作业，该作业从 MongoDB 读取数据并将其打印到本地客户端：

```sql
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

### 匹配查询扫描

在数据同步场景中，使用 `match.query` 方法可以减少后续操作员需要处理的文档数量，从而提高性能。以下是一个使用 `match.query` 的 SeaTunnel 示例：

```sql
source {
  MongoDB {
    uri = "mongodb://user:password@127.0.0.1:27017"
    database = "test_db"
    collection = "orders"
    match.query = "{status: \"A\"}"
    schema = {
      fields {
        id = bigint
        status = string
      }
    }
  }
}
```

### 投影扫描

在 MongoDB 中，投影用于控制查询结果中包含哪些字段。以下是一个使用投影的 SeaTunnel 示例：

```sql
source {
  MongoDB {
    uri = "mongodb://user:password@127.0.0.1:27017"
    database = "test_db"
    collection = "users"
    match.projection = "{ name: 1, email: 0 }"
    schema = {
      fields {
        name = string
      }
    }
  }
}
```

### 分区扫描

SeaTunnel 为 MongoDB 集合提供了分区扫描功能，提供了以下分区策略。用户可以通过设置用于分片字段的 `partition.split-key` 和用于分片大小的 `partition.split-size` 来控制数据分片。以下是一个使用分区扫描的 SeaTunnel 示例：

```sql
source {
  MongoDB {
    uri = "mongodb://user:password@127.0.0.1:27017"
    database = "test_db"
    collection = "users"
    partition.split-key = "id"
    partition.split-size = 1024
    schema = {
      fields {
        id = bigint
        status = string
      }
    }
  }
}
```

### Flat Sync String

通过使用 `flat.sync string`，只能设置一个字段属性值，并且字段类型必须是 `string`。以下是一个使用 `flat.sync string` 的 SeaTunnel 示例：

```sql
env {
  parallelism = 10
  job.mode = "BATCH"
}

source {
  MongoDB {
    uri = "mongodb://user:password@127.0.0.1:27017"
    database = "test_db"
    collection = "users"
    flat.sync-string = true
    schema = {
      fields {
        data = string
      }
    }
  }
}

sink {
  Console {}
}
```

### 总结

Apache SeaTunnel MongoDB CDC 连接器是一个功能强大的工具，它允许应用程序实时订阅和处理 MongoDB 中的数据更改。通过配置不同的选项和策略，用户可以实现高效的数据同步和处理。