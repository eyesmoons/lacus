Apache SeaTunnel 的 OssFile 连接器是一个用于从对象存储服务（如阿里云OSS）中读取数据的源连接器。以下是对该连接器的功能和用法的整理说明：

### 支持的引擎
- Spark
- Flink
- SeaTunnel Zeta

### 使用依赖
- **Spark/Flink Engine**:
  - 确保你的 spark/flink 集群已经集成了 Hadoop。
  - 确保 `${SEATUNNEL_HOME}/plugins/` 目录下存在 `hadoop-aliyun-xx.jar`、`aliyun-sdk-oss-xx.jar` 和 `jdom-xx.jar`，并且这些 jar 的版本需要与你在 spark/flink 中使用的 Hadoop 版本相匹配。
- **SeaTunnel Zeta Engine**:
  - 确保 `${SEATUNNEL_HOME}/lib/` 目录下存在 `seatunnel-hadoop3-3.1.4-uber.jar`、`aliyun-sdk-oss-3.4.1.jar`、`hadoop-aliyun-3.1.4.jar` 和 `jdom-1.1.jar`。

### 关键特性
- **批处理和流处理**: 支持批处理和流处理模式。
- **精确一次**: 保证数据处理的精确一次。
- **列投影**: 支持读取特定列，通过配置 `read_columns` 实现。
- **并行处理**: 支持并行处理，提高读取效率。
- **用户自定义分割**: 支持用户自定义分割逻辑。
- **文件格式支持**: 支持多种文件格式，包括文本、CSV、Parquet、ORC、JSON、Excel、XML 和二进制。

### 数据类型映射
- **JSON 文件类型**: 需要配置 `schema` 选项来指定如何解析数据到行。
- **文本或 CSV 文件类型**: 需要配置 `schema` 和 `field_delimiter` 选项来解析数据到行。
- **ORC 文件类型**: 连接器可以自动识别 schema，无需额外配置。
- **Parquet 文件类型**: 连接器可以自动识别 schema，无需额外配置。

### 选项配置
- **path**: Oss 路径，可以包含子路径。
- **file_format_type**: 文件类型，支持 text、csv、parquet、orc、json、excel、xml、binary。
- **bucket**: OSS 文件系统的桶地址。
- **endpoint**: OSS 端点。
- **read_columns**: 读取的数据源列列表。
- **access_key**: 访问密钥。
- **access_secret**: 访问密钥。
- **delimiter**: 字段分隔符。
- **parse_partition_from_path**: 是否从文件路径解析分区键值。
- **date_format**: 日期格式。
- **datetime_format**: 日期时间格式。
- **time_format**: 时间格式。
- **filename_extension**: 过滤文件扩展名。
- **skip_header_row_number**: 跳过头部行数。
- **csv_use_header_line**: 是否使用头部行解析 CSV 文件。
- **schema**: 上游数据的 schema。
- **sheet_name**: 读取的 Excel 工作表名称。
- **xml_row_tag**: XML 文件中数据行的标签名。
- **xml_use_attr_format**: 是否使用标签属性格式处理数据。
- **csv_use_header_line**: 是否使用头部行解析 CSV 文件。
- **compress_codec**: 文件的压缩编码。
- **encoding**: 文件的编码格式。
- **null_format**: 定义哪些字符串可以表示为 null。
- **file_filter_pattern**: 过滤文件的模式。
- **common-options**: 源插件公共参数。

### 文件结构示例
```
/data/seatunnel/20241001/report.txt
/data/seatunnel/20241007/abch202410.csv
/data/seatunnel/20241002/abcg202410.csv
/data/seatunnel/20241005/old_data.csv
/data/seatunnel/20241012/logo.png
```

### 匹配规则示例
- **匹配所有 .txt 文件**:
  ```
  /data/seatunnel/20241001/.*\.txt
  ```
- **匹配所有以 abc 开头的文件**:
  ```
  /data/seatunnel/20241002/abc.*
  ```
- **匹配以 abc 开头，第四个字符为 h 或 g 的文件**:
  ```
  /data/seatunnel/20241007/abc[h,g].*
  ```
- **匹配第三级文件夹以 202410 开头，文件以 .csv 结尾**:
  ```
  /data/seatunnel/202410\d*/.*\.csv
  ```

### 示例：创建 Oss 数据同步任务
```sql
# 设置任务的基本配置
env {
  parallelism = 1
  job.mode = "BATCH"
}

# 创建连接到 Oss 的源
source {
  OssFile {
    path = "/seatunnel/orc"
    bucket = "oss://tyrantlucifer-image-bed"
    access_key = "xxxxxxxxxxxxxxxxx"
    access_secret = "xxxxxxxxxxxxxxxxxxxxxx"
    endpoint = "oss-cn-beijing.aliyuncs.com"
    file_format_type = "orc"
  }
}

# 控制台打印读取的 Oss 数据
sink {
  Console {
  }
}
```

### 多表读取示例
```sql
# 设置任务的基本配置
env {
  parallelism = 1
  spark.app.name = "SeaTunnel"
  spark.executor.instances = 2
  spark.executor.cores = 1
  spark.executor.memory = "1g"
  spark.master = local
  job.mode = "BATCH"
}

# 创建连接到 Oss 的源
source {
  OssFile {
    tables_configs = [
      {
        schema = {
          table = "fake01"
        }
        bucket = "oss://whale-ops"
        access_key = "xxxxxxxxxxxxxxxxxxx"
        access_secret = "xxxxxxxxxxxxxxxxxxx"
        endpoint = "https://oss-accelerate.aliyuncs.com"
        path = "/test/seatunnel/read/orc"
        file_format_type = "orc"
      },
      {
        schema = {
          table = "fake02"
        }
        bucket = "oss://whale-ops"
        access_key = "xxxxxxxxxxxxxxxxxxx"
        access_secret = "xxxxxxxxxxxxxxxxxxx"
        endpoint = "https://oss-accelerate.aliyuncs.com"
        path = "/test/seatunnel/read/orc"
        file_format_type = "orc"
      }
    ]
    plugin_output = "fake"
  }
}

# 断言读取的表名
sink {
  Assert {
    rules {
      table-names = ["fake01", "fake02"]
    }
  }
}
```

### 过滤文件示例
```sql
# 设置任务的基本配置
env {
  parallelism = 1
  job.mode = "BATCH"
}

# 创建连接到 Oss 的源
source {
  OssFile {
    path = "/seatunnel/orc"
    bucket = "oss://tyrantlucifer-image-bed"
    access_key = "xxxxxxxxxxxxxxxxx"
    access_secret = "xxxxxxxxxxxxxxxxxxxxxx"
    endpoint = "oss-cn-beijing.aliyuncs.com"
    file_format_type = "orc"
    file_filter_pattern = "abc[DX]*.*"
  }
}

# 控制台打印读取的 Oss 数据
sink {
  Console {
  }
}
```

以上是 Apache SeaTunnel OssFile 连接器的功能和用法说明，希望对您有所帮助。