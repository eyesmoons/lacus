根据您提供的链接内容，以下是对 Apache SeaTunnel 与 RocketMQ 集成使用说明和示例的整理：

### Apache SeaTunnel RocketMQ 连接器使用说明

#### 简介
Apache SeaTunnel 的 RocketMQ 连接器是一个数据源连接器，用于从 Apache RocketMQ 消息队列中读取数据。它支持批处理和流处理模式，并提供了精确一次（exactly-once）的数据处理保证。

#### 支持的引擎
- Spark
- Flink
- SeaTunnel Zeta

#### 关键特性
- 批处理（batch）
- 流处理（stream）
- 精确一次（exactly-once）
- 列投影（column projection）
- 并行处理（parallelism）
- 支持用户自定义分区（support user-defined split）

#### 连接器配置选项
- **topics**: RocketMQ 主题名称，多个主题用逗号分隔。
- **name.srv.addr**: RocketMQ 名字服务器集群地址。
- **tags**: RocketMQ 标签名称，多个标签用逗号分隔。
- **acl.enabled**: 是否启用访问控制。
- **access.key**: 访问密钥。
- **secret.key**: 密钥。
- **batch.size**: RocketMQ 消费者拉取批次大小。
- **consumer.group**: RocketMQ 消费者组ID。
- **commit.on.checkpoint**: 是否在检查点时提交消费者偏移量。
- **schema**: 数据结构，包括字段名称和字段类型。
- **format**: 数据格式，默认为 JSON，可选 TEXT。
- **field.delimiter**: 自定义字段分隔符。
- **start.mode**: 消费者的初始消费模式，支持从最后偏移量、第一个偏移量、消费者组偏移量、时间戳和特定偏移量开始消费。
- **start.mode.offsets**: 指定偏移量。
- **start.mode.timestamp**: 消费模式为时间戳时的时间。
- **partition.discovery.interval.millis**: 动态发现主题和分区的间隔。
- **ignore_parse_errors**: 是否忽略解析错误。

#### 任务示例

##### 简单示例
```plaintext
env {
  parallelism = 1
  job.mode = "BATCH"
}

source {
  Rocketmq {
    name.srv.addr = "rocketmq-e2e:9876"
    topics = "test_topic_json"
    plugin_output = "rocketmq_table"
    schema = {
      fields {
        id = bigint
        c_map = "map<string, smallint>"
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
  }
}

transform {
  # 更多信息请参考 https://seatunnel.apache.org/docs/category/transform
}

sink {
  Console {
  }
}
```

##### 指定格式消费简单示例
```plaintext
env {
  parallelism = 1
  job.mode = "BATCH"
}

source {
  Rocketmq {
    name.srv.addr = "localhost:9876"
    topics = "test_topic"
    plugin_output = "rocketmq_table"
    start.mode = "CONSUME_FROM_FIRST_OFFSET"
    batch.size = "400"
    consumer.group = "test_topic_group"
    format = "json"
    schema = {
      fields {
        c_map = "map<string, string>"
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
        c_bytes = bytes
        c_date = date
        c_timestamp = timestamp
      }
    }
  }
}

transform {
  # 更多信息请参考 https://seatunnel.apache.org/docs/category/transform
}

sink {
  Console {
  }
}
```

##### 指定时间戳简单示例
```plaintext
env {
  parallelism = 1
  spark.app.name = "SeaTunnel"
  spark.executor.instances = 2
  spark.executor.cores = 1
  spark.executor.memory = "1g"
  spark.master = local
  job.mode = "BATCH"
}

source {
  Rocketmq {
    name.srv.addr = "localhost:9876"
    topics = "test_topic"
    partition.discovery.interval.millis = "1000"
    start.mode.timestamp = "1694508382000"
    consumer.group = "test_topic_group"
    format = "json"
    schema = {
      fields {
        c_map = "map<string, string>"
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
        c_bytes = bytes
        c_date = date
        c_timestamp = timestamp
      }
    }
  }
}

transform {
  # 更多信息请参考 https://seatunnel.apache.org/docs/category/transform
}

sink {
  Console {
  }
}
```

##### 指定标签示例
```plaintext
env {
  parallelism = 1
  job.mode = "BATCH"
}

source {
  Rocketmq {
    plugin_output = "rocketmq_table"
    name.srv.addr = "localhost:9876"
    topics = "test_topic"
    format = "text"
    field_delimiter = ","
    tags = "test_tag"
    schema = {
      fields {
        id = bigint
        c_map = "map<string, smallint>"
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
  }
}

transform {
  # 更多信息请参考 https://seatunnel.apache.org/docs/category/transform
}

sink {
  Console {
    plugin_input = "rocketmq_table"
  }
}
```

### 总结
Apache SeaTunnel 的 RocketMQ 连接器提供了丰富的配置选项和多种使用示例，方便用户从 RocketMQ 中读取数据并进行进一步处理。通过合理配置和使用这些示例，可以有效地实现数据的实时同步和流处理。