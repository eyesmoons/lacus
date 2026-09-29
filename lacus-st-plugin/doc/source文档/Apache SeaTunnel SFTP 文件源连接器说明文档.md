### Apache SeaTunnel SFTP 文件连接器说明文档

#### 简介
Apache SeaTunnel 的 SFTP 文件连接器（SftpFile）是一个用于从 SFTP 文件服务器读取数据的源连接器。它支持多种数据引擎，包括 Spark、Flink 和 SeaTunnel Zeta。

#### 支持的引擎
- Spark
- Flink
- SeaTunnel Zeta

#### 主要功能
- 支持批处理和流处理
- 精确一次（exactly-once）数据处理
- 列投影（column projection）
- 并行处理（parallelism）
- 支持用户自定义分割
- 支持多种文件格式：文本、CSV、JSON、Excel、XML、二进制

#### 文件格式类型
- 文本（text）
- CSV
- JSON
- Excel
- XML
- 二进制（binary）

#### 依赖项
为了使用 SftpFile 连接器，需要以下依赖项：
- Hadoop 2.9.X+
- 相关的 Hadoop 依赖项

#### 数据类型映射
文件中的数据类型可以映射到 SeaTunnel 数据类型，例如：
- STRING
- SHORT
- INT
- BIGINT
- BOOLEAN
- DOUBLE
- DECIMAL
- FLOAT
- DATE
- TIME
- TIMESTAMP
- BYTES
- ARRAY
- MAP

#### 源选项
- **host**：目标 SFTP 主机（必填）
- **port**：目标 SFTP 端口（必填）
- **user**：目标 SFTP 用户名（必填）
- **password**：目标 SFTP 密码（必填）
- **path**：源文件路径（必填）
- **file_format_type**：文件类型（必填）
- **file_filter_pattern**：文件过滤模式（可选）
- **filename_extension**：文件名扩展名过滤（可选）
- **delimiter/field_delimiter**：字段分隔符（可选）
- **parse_partition_from_path**：是否从文件路径解析分区键值（可选）
- **date_format**：日期格式（可选）
- **datetime_format**：日期时间格式（可选）
- **time_format**：时间格式（可选）
- **skip_header_row_number**：跳过头部行数（可选）
- **read_columns**：读取的列列表（可选）
- **sheet_name**：工作表名称（仅当文件格式为 Excel 时使用）
- **xml_row_tag**：XML 文件中的数据行标签名称（仅当文件格式为 XML 时使用）
- **xml_use_attr_format**：是否使用标签属性格式（仅当文件格式为 XML 时使用）
- **csv_use_header_line**：是否使用头部行解析文件（仅当文件格式为 CSV 且文件包含匹配 RFC 4180 的头部行时使用）
- **schema**：上游数据的模式
- **compress_codec**：文件的压缩编解码器（可选）
- **archive_compress_codec**：归档文件的压缩编解码器（可选）
- **encoding**：文件读取的编码（可选）
- **null_format**：定义哪些字符串可以表示为空（仅当文件格式为文本时使用）
- **common-options**：源插件通用参数

#### 文件过滤模式
文件过滤模式遵循标准的正则表达式。例如：
- 匹配所有 .txt 文件：`/data/seatunnel/20241001/.*\.txt`
- 匹配所有以 abc 开头的文件：`/data/seatunnel/20241002/abc.*`

#### 文件格式类型
- **text**：文本文件
- **csv**：CSV 文件
- **json**：JSON 文件
- **excel**：Excel 文件
- **xml**：XML 文件
- **binary**：二进制文件

#### 示例配置
以下是一个示例配置，展示如何从 SFTP 读取数据并打印到本地客户端：

```plaintext
# 设置任务的基本配置
env {
  parallelism = 1
  job.mode = "BATCH"
}

# 创建连接到 SFTP 的源
source {
  SftpFile {
    host = "sftp"
    port = 22
    user = "seatunnel"
    password = "pass"
    path = "tmp/seatunnel/read/json"
    file_format_type = "json"
    plugin_output = "sftp"
    schema = {
      fields {
        c_map = "map<string, string>"
        c_array = "array<int>"
        c_string = "string"
        c_boolean = "boolean"
        c_tinyint = "tinyint"
        c_smallint = "smallint"
        c_int = "int"
        c_bigint = "bigint"
        c_float = "float"
        c_double = "double"
        c_bytes = "bytes"
        c_date = "date"
        c_decimal = "decimal(38, 18)"
        c_timestamp = "timestamp"
        c_row = {
          C_MAP = "map<string, string>"
          C_ARRAY = "array<int>"
          C_STRING = "string"
          C_BOOLEAN = "boolean"
          C_TINYINT = "tinyint"
          C_SMALLINT = "smallint"
          C_INT = "int"
          C_BIGINT = "bigint"
          C_FLOAT = "float"
          C_DOUBLE = "double"
          C_BYTES = "bytes"
          C_DATE = "date"
          C_DECIMAL = "decimal(38, 18)"
          C_TIMESTAMP = "timestamp"
        }
      }
    }
  }
}

# 控制台打印读取的 SFTP 数据
sink {
  Console {
    parallelism = 1
  }
}
```

#### 多表配置
SftpFile 连接器支持多表配置，可以同步多个表的数据：

```plaintext
SftpFile {
  tables_configs = [
    {
      schema {
        table = "student"
        fields {
          name = "string"
          age = "int"
        }
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
        fields {
          name = "string"
          age = "int"
        }
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

#### 过滤文件
可以使用 `file_filter_pattern` 选项过滤文件：

```plaintext
env {
  parallelism = 1
  job.mode = "BATCH"
}

source {
  SftpFile {
    host = "sftp"
    port = 22
    user = "seatunnel"
    password = "pass"
    path = "tmp/seatunnel/read/json"
    file_format_type = "json"
    plugin_output = "sftp"
    file_filter_pattern = "abc[DX]*.*"
  }
}

sink {
  Console {
  }
}
```

#### Changelog
- 版本更新和功能变更记录

#### 社区与支持
- Apache SeaTunnel 是一个在 Apache 软件基金会（ASF）孵化器中的项目，由 Apache Incubator 赞助。

#### 版权信息
- 版权 © 2021-2022 The Apache Software Foundation. Apache SeaTunnel, SeaTunnel, 和其羽毛标志是 The Apache Software Foundation 的商标。

通过以上说明文档，您可以对 Apache SeaTunnel 的 SFTP 文件连接器有一个全面的了解，并能够有效地使用它进行数据同步和管理。