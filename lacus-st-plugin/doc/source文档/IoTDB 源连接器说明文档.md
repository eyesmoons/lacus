根据您提供的链接内容，以下是对 Apache SeaTunnel 中 IoTDB 源连接器的说明文档整理：

---

### IoTDB 源连接器说明文档

#### 简介
Apache SeaTunnel 的 IoTDB 源连接器允许用户通过 IoTDB 读取外部数据源的数据。该连接器支持 Spark、Flink 和 SeaTunnel Zeta 等多种引擎。

#### 支持的引擎
- Spark
- Flink
- SeaTunnel Zeta

#### 描述
IoTDB 源连接器通过 IoTDB 读取外部数据源的数据，支持批处理和流处理模式，并具备精确一次（exactly-once）的数据处理能力。

#### 使用依赖
- **Spark/Flink 引擎**：需要确保 jdbc 驱动 jar 包已放置在 `${SEATUNNEL_HOME}/plugins/` 目录下。
- **SeaTunnel Zeta 引擎**：需要确保 jdbc 驱动 jar 包已放置在 `${SEATUNNEL_HOME}/lib/` 目录下。

#### 关键特性
- **批处理**：支持批量数据读取。
- **流处理**：支持实时数据流读取。
- **精确一次**：确保数据处理的精确性，避免数据丢失或重复。
- **列投影**：支持查询 SQL 并实现投影效果。
- **并行性**：支持用户自定义分区，提高数据处理效率。

#### 支持的数据源信息
- **数据源**：IoTDB
- **支持的版本**：>= 0.13.0
- **URL**：`localhost:6667`

#### 数据类型映射
IoTDB 数据类型与 SeaTunnel 数据类型的映射关系如下：
- BOOLEAN -> BOOLEAN
- INT32 -> TINYINT
- INT32 -> SMALLINT
- INT32 -> INT
- INT64 -> BIGINT
- FLOAT -> FLOAT
- DOUBLE -> DOUBLE
- TEXT -> STRING

#### 源选项
- **node_urls**：IoTDB 集群地址，格式为 "host1:port" 或 "host1:port,host2:port"。
- **username**：IoTDB 用户名。
- **password**：IoTDB 用户密码。
- **sql**：执行的 SQL 语句。
- **schema**：数据模式配置。
- **fetch_size**：IoTDB 选择的 fetch_size。
- **lower_bound**：IoTDB 选择的下界。
- **upper_bound**：IoTDB 选择的上界。
- **num_partitions**：IoTDB 选择的分区数。
- **thrift_default_buffer_size**：IoTDB 的 thrift 默认缓冲区大小。
- **thrift_max_frame_size**：IoTDB 的 thrift 最大帧大小。
- **enable_cache_leader**：IoTDB 的 enable_cache_leader 设置。
- **version**：客户端使用的 SQL 语义版本，可能的值为：V_0_12, V_0_13。

#### 分区处理
- **split partitions**：可以分割 IoTDB 的分区，通常使用时间列进行分割。
- **num_partitions**：分区数。
- **upper_bound**：时间列的上界。
- **lower_bound**：时间列的下界。

#### 示例
以下是一个使用 IoTDB 源连接器的示例配置：

```json
env {
  parallelism = 2
  job.mode = "BATCH"
}

source {
  IoTDB {
    node_urls = "localhost:6667"
    username = "root"
    password = "root"
    sql = "SELECT temperature, moisture, c_int, c_bigint, c_float, c_double, c_string, c_boolean FROM root.test_group.* WHERE time < 4102329600000 align by device"
    schema {
      fields {
        ts = timestamp
        device_name = string
        temperature = float
        moisture = bigint
        c_int = int
        c_bigint = bigint
        c_float = float
        c_double = double
        c_string = string
        c_boolean = boolean
      }
    }
  }
}

sink {
  Console {
  }
}
```

#### 上游 IoTDB 数据格式
IoTDB 数据格式如下：

```sql
SELECT temperature, moisture, c_int, c_bigint, c_float, c_double, c_string, c_boolean FROM root.test_group.* WHERE time < 4102329600000 align by device;
```

#### 加载到 SeaTunnelRow 数据格式
加载到 SeaTunnelRow 数据格式如下：

```
ts        device_name        temperature        moisture        c_int        c_bigint        c_float        c_double        c_string        c_boolean
1664035200001        root.test_group.device_a        36.1        100        1        21474836470        1.0f        1.0d        abc        true
1664035200001        root.test_group.device_b        36.2        101        2        21474836470        2.0f        2.0d        abc        true
1664035200001        root.test_group.device_c        36.3        102        3        21474836470        3.0f        3.0d        abc        true
```

---

以上文档详细介绍了 Apache SeaTunnel 中 IoTDB 源连接器的配置和使用方法，希望能帮助用户更好地使用该连接器进行数据集成和处理。