### Apache SeaTunnel FtpFile 连接器说明文档

#### 简介
FtpFile 是 Apache SeaTunnel 中的一个源连接器，用于从 FTP 文件服务器读取数据。它支持批处理和流处理，适用于 Spark、Flink 和 SeaTunnel Zeta 等引擎。

#### 支持的引擎
- Spark
- Flink
- SeaTunnel Zeta

#### 关键特性
- 批处理
- 流处理
- 精确一次处理
- 列投影
- 并行度
- 支持用户自定义分片

#### 文件格式类型
- 文本
- CSV
- JSON
- Excel
- XML
- 二进制

#### 描述
FtpFile 连接器可以从 FTP 文件服务器读取数据，支持多种文件格式。使用此连接器时，需要确保 Spark/Flink 集群已经集成了 Hadoop（测试的 Hadoop 版本为 2.x）。如果使用 SeaTunnel Engine，下载并安装时会自动集成 Hadoop 的 jar 包。

#### 配置项
| 名称                        | 类型    | 是否必填 | 默认值              |
| --------------------------- | ------- | -------- | ------------------- |
| host                        | string  | 是       | -                   |
| port                        | int     | 是       | -                   |
| user                        | string  | 是       | -                   |
| password                    | string  | 是       | -                   |
| path                        | string  | 是       | -                   |
| file_format_type            | string  | 是       | -                   |
| connection_mode             | string  | 否       | active_local        |
| remote_verification_enabled | boolean | 否       | true                |
| delimiter/field_delimiter   | string  | 否       | \001                |
| read_columns                | list    | 否       | -                   |
| parse_partition_from_path   | boolean | 否       | true                |
| date_format                 | string  | 否       | yyyy-MM-dd          |
| datetime_format             | string  | 否       | yyyy-MM-dd HH:mm:ss |
| time_format                 | string  | 否       | HH:mm:ss            |
| skip_header_row_number      | long    | 否       | 0                   |
| schema                      | config  | 否       | -                   |
| sheet_name                  | string  | 否       | -                   |
| xml_row_tag                 | string  | 否       | -                   |
| xml_use_attr_format         | boolean | 否       | -                   |
| csv_use_header_line         | boolean | 否       | false               |
| file_filter_pattern         | string  | 否       | -                   |
| compress_codec              | string  | 否       | none                |
| archive_compress_codec      | string  | 否       | none                |
| encoding                    | string  | 否       | UTF-8               |
| null_format                 | string  | 否       | -                   |

#### 详细配置说明
- **host**: 目标 FTP 主机地址，必填项。
- **port**: 目标 FTP 端口，必填项。
- **user**: 目标 FTP 用户名，必填项。
- **password**: 目标 FTP 密码，必填项。
- **path**: 源文件路径，必填项。
- **remote_verification_enabled**: 是否启用 FTP 数据通道的远程主机验证，默认值为 true。
- **file_filter_pattern**: 文件过滤模式，用于过滤文件。该模式遵循标准正则表达式。
- **file_format_type**: 文件类型，支持以下文件类型：text、csv、parquet、orc、json、excel、xml、binary。
- **connection_mode**: 目标 FTP 连接模式，默认为主动模式，支持以下模式：active_local、passive_local。
- **delimiter/field_delimiter**: 字段分隔符，仅在文件格式为 text 时需要配置。
- **parse_partition_from_path**: 控制是否从文件路径中解析分区键和值。
- **date_format**: 日期类型格式，用于告诉连接器如何将字符串转换为日期。
- **datetime_format**: 日期时间类型格式，用于告诉连接器如何将字符串转换为日期时间。
- **time_format**: 时间类型格式，用于告诉连接器如何将字符串转换为时间。
- **skip_header_row_number**: 跳过前几行，仅适用于 txt 和 csv 文件。
- **schema**: 上游数据的 schema 信息，仅在文件格式类型为 text、json、excel、xml 或 csv 时需要配置。
- **read_columns**: 数据源的读取列列表，用户可以使用它来实现字段投影。
- **sheet_name**: 读取工作簿中的工作表，仅在文件格式类型为 excel 时使用。
- **xml_row_tag**: 指定 XML 文件中数据行的标签名称，仅在文件格式为 xml 时需要配置。
- **xml_use_attr_format**: 指定是否使用标签属性格式处理数据，仅在文件格式为 xml 时需要配置。
- **csv_use_header_line**: 是否使用标题行来解析文件，仅在文件格式为 csv 时可以选择配置。
- **compress_codec**: 文件的压缩编解码器，支持的详细信息如下：
  - txt: lzo、none
  - json: lzo、none
  - csv: lzo、none
  - orc/parquet: 自动识别压缩类型，无需额外设置。
- **archive_compress_codec**: 归档文件的压缩编解码器，支持的详细信息如下：
  - ZIP: txt、json、excel、xml
  - TAR: txt、json、excel、xml
  - TAR_GZ: txt、json、excel、xml
  - GZ: txt、json、excel、xml
  - NONE: all
- **encoding**: 读取文件的编码，仅在文件格式类型为 json、text、csv、xml 时使用。
- **null_format**: 用于定义哪些字符串可以表示为 null，仅在文件格式类型为 text 时使用。

#### 示例
```json
FtpFile {
  path = "/tmp/seatunnel/sink/text"
  host = "192.168.31.48"
  port = 21
  user = "tyrantlucifer"
  password = "tianchao"
  file_format_type = "text"
  schema = {
    name = string
    age = int
  }
  field_delimiter = "#"
}
```

#### 多表配置
```json
FtpFile {
  tables_configs = [
    {
      schema {
        table = "student"
      }
      path = "/tmp/seatunnel/sink/text"
      host = "192.168.31.48"
      port = 21
      user = "tyrantlucifer"
      password = "tianchao"
      file_format_type = "parquet"
    },
    {
      schema {
        table = "teacher"
      }
      path = "/tmp/seatunnel/sink/text"
      host = "192.168.31.48"
      port = 21
      user = "tyrantlucifer"
      password = "tianchao"
      file_format_type = "parquet"
    }
  ]
}
```

#### 传输二进制文件
```json
env {
  parallelism = 1
  job.mode = "BATCH"
}

source {
  FtpFile {
    host = "192.168.31.48"
    port = 21
    user = "tyrantlucifer"
    password = "tianchao"
    path = "/seatunnel/read/binary/"
    file_format_type = "binary"
  }
}

sink {
  // 您可以将本地文件传输到 s3/hdfs/oss 等。
  FtpFile {
    host = "192.168.31.48"
    port = 21
    user = "tyrantlucifer"
    password = "tianchao"
    path = "/seatunnel/read/binary2/"
    file_format_type = "binary"
  }
}
```

#### 过滤文件
```json
env {
  parallelism = 1
  job.mode = "BATCH"
}

source {
  FtpFile {
    host = "192.168.31.48"
    port = 21
    user = "tyrantlucifer"
    password = "tianchao"
    path = "/seatunnel/read/binary/"
    file_format_type = "binary"
    // 文件示例 abcD2024.csv
    file_filter_pattern = "abc[DX]*.*"
  }
}

sink {
  Console {
  }
}
```

#### 变更日志
- 本文档基于 Apache SeaTunnel 2.3.11 版本。

#### 版权信息
- Copyright © 2021-2022 The Apache Software Foundation.
- Apache SeaTunnel, SeaTunnel, and its feather logo are trademarks of The Apache Software Foundation.

以上是 Apache SeaTunnel FtpFile 连接器的详细说明文档，希望对您有所帮助。