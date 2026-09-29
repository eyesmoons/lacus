根据您提供的链接内容，以下是 Apache SeaTunnel OssJindoFile 连接器的说明文档：

### 连接器概述

**OssJindoFile** 是 Apache SeaTunnel 的一个数据源连接器，用于从阿里云 OSS 文件系统读取数据。它支持 Spark、Flink 和 SeaTunnel Zeta 等多种计算引擎。

### 主要特性

- **批处理和流处理**：支持批处理和流处理模式。
- **精确一次**：确保数据精确一次处理。
- **列投影**：支持读取指定列，实现字段投影。
- **并行处理**：支持并行处理，提高数据处理效率。
- **用户自定义分区**：支持用户自定义分区读取。
- **多种文件格式**：支持多种文件格式，包括文本、CSV、Parquet、ORC、JSON、Excel、XML 和二进制。

### 描述

OssJindoFile 连接器通过 Jindo API 读取数据，支持从阿里云 OSS 文件系统中读取数据。

### 注意事项

- **依赖**：使用 Spark/Flink 时，必须确保集群已经集成了 Hadoop，测试的 Hadoop 版本是 2.x。
- **Hadoop 集成**：如果使用 SeaTunnel 引擎，下载并安装时会自动集成 Hadoop jar，可以在 `${SEATUNNEL_HOME}/lib` 下查看 jar 包确认。
- **文件类型支持**：为了支持更多文件类型，内部使用 HDFS 协议访问 OSS，因此该连接器需要一些 Hadoop 依赖，仅支持 Hadoop 版本 2.9.X+。

### 选项

| 选项名                    | 类型    | 是否必需 | 默认值              | 描述                                                         |
| ------------------------- | ------- | -------- | ------------------- | ------------------------------------------------------------ |
| path                      | string  | 是       | -                   | 源文件路径                                                   |
| file_format_type          | string  | 是       | -                   | 文件类型，支持 text、csv、parquet、orc、json、excel、xml、binary |
| bucket                    | string  | 是       | -                   | OSS 文件系统的桶地址                                         |
| access_key                | string  | 是       | -                   | OSS 文件系统的访问密钥                                       |
| access_secret             | string  | 是       | -                   | OSS 文件系统的访问密钥                                       |
| endpoint                  | string  | 是       | -                   | OSS 文件系统的端点                                           |
| read_columns              | list    | 否       | -                   | 数据源的读取列列表，用于字段投影                             |
| delimiter/field_delimiter | string  | 否       | \001                | 字段分隔符，仅当 file_format_type 为 text 时需要配置         |
| parse_partition_from_path | boolean | 否       | true                | 是否从文件路径解析分区键和值                                 |
| date_format               | string  | 否       | yyyy-MM-dd          | 日期格式，用于将字符串转换为日期                             |
| datetime_format           | string  | 否       | yyyy-MM-dd HH:mm:ss | 日期时间格式，用于将字符串转换为日期时间                     |
| time_format               | string  | 否       | HH:mm:ss            | 时间格式，用于将字符串转换为时间                             |
| skip_header_row_number    | long    | 否       | 0                   | 跳过源文件的前几行，仅对 txt 和 csv 文件有效                 |
| schema                    | config  | 否       | -                   | 上游数据的模式，仅当 file_format_type 为 text、json、excel、xml 或 csv 时需要配置 |
| sheet_name                | string  | 否       | -                   | 仅当 file_format_type 为 excel 时需要配置，读取工作簿的表名  |
| xml_row_tag               | string  | 否       | -                   | 仅当 file_format_type 为 xml 时需要配置，XML 行标签          |
| xml_use_attr_format       | boolean | 否       | -                   | 仅当 file_format_type 为 xml 时需要配置，是否使用属性格式    |
| csv_use_header_line       | boolean | 否       | false               | 仅当 file_format_type 为 csv 时需要配置，是否使用头部行      |
| file_filter_pattern       | string  | 否       | -                   | 文件过滤模式，用于过滤文件                                   |
| compress_codec            | string  | 否       | none                | 文件的压缩编码，支持的编码类型请参考文档                     |
| archive_compress_codec    | string  | 否       | none                | 归档文件的压缩编码，支持的编码类型请参考文档                 |
| encoding                  | string  | 否       | UTF-8               | 文件的编码，仅当 file_format_type 为 json、text、csv、xml 时需要配置 |
| null_format               | string  | 否       | -                   | 仅当 file_format_type 为 text 时需要配置，定义哪些字符串可以表示为 null |

### 文件路径示例

```
/data/seatunnel/20241001/report.txt
/data/seatunnel/20241007/abch202410.csv
/data/seatunnel/20241002/abcg202410.csv
/data/seatunnel/20241005/old_data.csv
/data/seatunnel/20241012/logo.png
```

### 过滤文件示例

- 匹配所有 .txt 文件：
  ```
  /data/seatunnel/20241001/.*\.txt
  ```
- 匹配所有以 abc 开头的文件：
  ```
  /data/seatunnel/20241002/abc.*
  ```
- 匹配所有以 abc 开头，第四个字符是 h 或 g 的文件：
  ```
  /data/seatunnel/20241007/abc[h,g].*
  ```
- 匹配第三级文件夹以 202410 开头，文件以 .csv 结尾：
  ```
  /data/seatunnel/202410\d*/.*\.csv
  ```

### 二进制文件传输示例

```json
{
  "env": {
    "parallelism": 1,
    "job.mode": "BATCH"
  },
  "source": {
    "OssJindoFile": {
      "bucket": "oss://tyrantlucifer-image-bed",
      "access_key": "xxxxxxxxxxxxxxxxx",
      "access_secret": "xxxxxxxxxxxxxxxxxxxxxx",
      "endpoint": "oss-cn-beijing.aliyuncs.com",
      "path": "/seatunnel/read/binary/",
      "file_format_type": "binary"
    }
  },
  "sink": {
    "OssJindoFile": {
      "bucket": "oss://tyrantlucifer-image-bed",
      "access_key": "xxxxxxxxxxxxxxxxx",
      "access_secret": "xxxxxxxxxxxxxxxxxxxxxx",
      "endpoint": "oss-cn-beijing.aliyuncs.com",
      "path": "/seatunnel/read/binary2/",
      "file_format_type": "binary"
    }
  }
}
```

### 过滤文件示例

```json
{
  "env": {
    "parallelism": 1,
    "job.mode": "BATCH"
  },
  "source": {
    "OssJindoFile": {
      "bucket": "oss://tyrantlucifer-image-bed",
      "access_key": "xxxxxxxxxxxxxxxxx",
      "access_secret": "xxxxxxxxxxxxxxxxxxxxxx",
      "endpoint": "oss-cn-beijing.aliyuncs.com",
      "path": "/seatunnel/read/binary/",
      "file_format_type": "binary",
      "file_filter_pattern": "abc[DX]*.*"
    }
  },
  "sink": {
    "Console": {}
  }
}
```

### 总结

OssJindoFile 连接器是一个功能强大的数据源连接器，支持从阿里云 OSS 文件系统中高效读取数据。通过配置不同的选项，可以实现灵活的数据读取和处理。希望这份说明文档能帮助您更好地使用 OssJindoFile 连接器。