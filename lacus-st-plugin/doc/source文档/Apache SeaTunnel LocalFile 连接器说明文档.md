### Apache SeaTunnel LocalFile 连接器说明文档

#### 简介
LocalFile 连接器是 Apache SeaTunnel 的一部分，用于从本地文件系统读取数据。它支持多种文件格式，包括文本、CSV、Parquet、ORC、JSON、Excel 和 XML。此连接器适用于 Spark、Flink 和 SeaTunnel Zeta 等多种引擎。

#### 支持的引擎
- Spark
- Flink
- SeaTunnel Zeta

#### 主要特性
- **批处理和流处理**：支持批处理和流处理模式。
- **精确一次**：确保数据处理的精确一次特性。
- **列投影**：允许用户指定需要读取的列，实现字段投影。
- **并行性**：支持并行处理，提高数据处理效率。
- **用户定义的分割**：允许用户自定义数据分割方式。
- **文件格式支持**：支持多种文件格式，包括文本、CSV、Parquet、ORC、JSON、Excel、XML 和二进制。

#### 描述
LocalFile 连接器用于从本地文件系统读取数据。使用此连接器，用户可以轻松地将本地文件中的数据导入到数据处理流程中。

#### 提示
- 如果使用 Spark/Flink，必须确保 Spark/Flink 集群已经集成了 Hadoop。已测试的 Hadoop 版本是 2.x。
- 如果使用 SeaTunnel Engine，它会在下载和安装时自动集成 Hadoop jar。用户可以在 `${SEATUNNEL_HOME}/lib` 下查看 jar 包以确认。

#### 选项
以下是 LocalFile 连接器的配置选项：

| 配置项                    | 类型    | 是否必需 | 默认值              | 描述                                                         |
| ------------------------- | ------- | -------- | ------------------- | ------------------------------------------------------------ |
| path                      | string  | 是       | -                   | 源文件路径                                                   |
| file_format_type          | string  | 是       | -                   | 文件类型，支持 text、csv、parquet、orc、json、excel、xml、binary |
| read_columns              | list    | 否       | -                   | 读取的数据列列表，用于字段投影                               |
| delimiter/field_delimiter | string  | 否       | \001                | 字段分隔符，仅在使用 text 文件格式时需要配置                 |
| parse_partition_from_path | boolean | 否       | true                | 是否从文件路径解析分区键和值                                 |
| date_format               | string  | 否       | yyyy-MM-dd          | 日期格式，用于将字符串转换为日期                             |
| datetime_format           | string  | 否       | yyyy-MM-dd HH:mm:ss | 日期时间格式，用于将字符串转换为日期时间                     |
| time_format               | string  | 否       | HH:mm:ss            | 时间格式，用于将字符串转换为时间                             |
| skip_header_row_number    | long    | 否       | 0                   | 跳过源文件的前几行，仅适用于 txt 和 csv 文件                 |
| schema                    | config  | 否       | -                   | 上游数据的模式信息，仅适用于 text、json、excel、xml 或 csv 文件 |
| sheet_name                | string  | 否       | -                   | 仅适用于 excel 文件格式，指定工作簿的表名                    |
| excel_engine              | string  | 否       | POI                 | 仅适用于 excel 文件格式，支持的引擎有 POI 和 EasyExcel       |
| xml_row_tag               | string  | 否       | -                   | 仅适用于 xml 文件格式，指定 xml 文件中数据行的标签名         |
| xml_use_attr_format       | boolean | 否       | -                   | 仅适用于 xml 文件格式，指定是否使用标签属性格式处理数据      |
| csv_use_header_line       | boolean | 否       | false               | 仅适用于 csv 文件格式，指定是否使用头部行解析文件            |
| file_filter_pattern       | string  | 否       | -                   | 文件过滤模式，用于过滤文件                                   |
| filename_extension        | string  | 否       | -                   | 过滤文件名扩展名，用于过滤具有特定扩展名的文件               |
| compress_codec            | string  | 否       | none                | 文件的压缩编码，支持的编码有 lzo 和 none                     |
| archive_compress_codec    | string  | 否       | none                | 归档文件的压缩编码，支持的编码有 ZIP、TAR、TAR_GZ、GZ 和 NONE |
| encoding                  | string  | 否       | UTF-8               | 仅适用于 json、text、csv、xml 文件格式，指定文件的编码       |
| null_format               | string  | 否       | -                   | 仅适用于 text 文件格式，定义哪些字符串可以表示为 null        |
| common-options            | no      | no       | -                   | 源插件公共参数，请参考源公共选项获取详细信息                 |
| tables_configs            | list    | no       | -                   | 用于定义多表任务，当有多个表需要读取时，可以使用此选项定义多个表 |

#### 配置项详细说明
- **path**：源文件路径，必须配置。
- **file_format_type**：文件类型，支持的类型有 text、csv、parquet、orc、json、excel、xml、binary。如果指定文件类型为 json，需要同时指定 schema 选项以告诉连接器如何解析数据到所需的行。例如：
  ```json
  {
    "code": 200,
    "data": "get success",
    "success": true
  }
  ```
  可以将多个数据片段保存到一个文件中，并使用换行符分割：
  ```json
  {
    "code": 200,
    "data": "get success",
    "success": true
  }
  {
    "code": 300,
    "data": "get failed",
    "success": false
  }
  ```
  在这种情况下，需要指定 schema：
  ```json
  schema {
    fields {
      code = int
      data = string
      success = boolean
    }
  }
  ```
  连接器将生成以下数据：
  ```
  code        data        success
  200        get success        true
  300        get failed        false
  ```
  如果指定文件类型为 parquet 或 orc，则不需要 schema 选项，连接器可以自动找到上游数据的模式。如果指定文件类型为 text 或 csv，可以选择是否指定 schema 信息。例如，上游数据如下：
  ```
  tyrantlucifer#26#male
  ```
  如果不指定数据 schema，连接器将把上游数据视为以下格式：
  ```
  content
  tyrantlucifer#26#male
  ```
  如果指定数据 schema，需要同时指定 field_delimiter 选项（除了 CSV 文件类型）：
  ```json
  field_delimiter = "#"
  schema {
    fields {
      name = string
      age = int
      gender = string
    }
  }
  ```
  连接器将生成以下数据：
  ```
  name        age        gender
  tyrantlucifer        26        male
  ```
  如果指定文件类型为 binary，SeaTunnel 可以同步任何格式的文件，例如压缩包、图片等。简而言之，任何文件都可以同步到目标位置。在这种情况下，需要确保源和目标使用相同的二进制格式进行文件同步。具体用法可以在示例中找到。

- **read_columns**：读取的数据列列表，用于字段投影。
- **delimiter/field_delimiter**：字段分隔符，仅在使用 text 文件格式时需要配置。默认值为 \001，与 Hive 的默认分隔符相同。
- **parse_partition_from_path**：控制是否从文件路径解析分区键和值。
- **date_format**：日期格式，用于将字符串转换为日期。支持的格式有 yyyy-MM-dd、yyyy.MM.dd、yyyy/MM/dd。默认值为 yyyy-MM-dd。
- **datetime_format**：日期时间格式，用于将字符串转换为日期时间。支持的格式有 yyyy-MM-dd HH:mm:ss、yyyy.MM.dd HH:mm:ss、yyyy/MM/dd HH:mm:ss、yyyyMMddHHmmss。默认值为 yyyy-MM-dd HH:mm:ss。
- **time_format**：时间格式，用于将字符串转换为时间。支持的格式有 HH:mm:ss、HH:mm:ss.SSS。默认值为 HH:mm:ss。
- **skip_header_row_number**：跳过源文件的前几行，仅适用于 txt 和 csv 文件。
- **schema**：仅适用于 text、json、excel、xml 或 csv 文件格式，指定上游数据的模式信息。
- **sheet_name**：仅适用于 excel 文件格式，指定工作簿的表名。
- **excel_engine**：仅适用于 excel 文件格式，支持的引擎有 POI 和 EasyExcel。默认值为 POI，但 POI 在读取超过 65,000 行的 Excel 文件时容易导致内存溢出，因此可以切换到 EasyExcel 作为读取引擎。
- **xml_row_tag**：仅适用于 xml 文件格式，指定 xml 文件中数据行的标签名。
- **xml_use_attr_format**：仅适用于 xml 文件格式，指定是否使用标签属性格式处理数据。
- **csv_use_header_line**：仅适用于 csv 文件格式，指定是否使用头部行解析文件。
- **file_filter_pattern**：文件过滤模式，用于过滤文件。模式遵循标准的正则表达式。详细信息请参考 https://en.wikipedia.org/wiki/Regular_expression。
- **filename_extension**：过滤文件名扩展名，用于过滤具有特定扩展名的文件。
- **compress_codec**：文件的压缩编码，支持的编码有 lzo 和 none。
- **archive_compress_codec**：归档文件的压缩编码，支持的编码有 ZIP、TAR、TAR_GZ、GZ 和 NONE。
- **encoding**：仅适用于 json、text、csv、xml 文件格式，指定文件的编码。
- **null_format**：仅适用于 text 文件格式，定义哪些字符串可以表示为 null。
- **common-options**：源插件公共参数，请参考源公共选项获取详细信息。
- **tables_configs**：用于定义多表任务，当有多个表需要读取时，可以使用此选项定义多个表。

#### 示例
##### 单表示例
```json
LocalFile {
  path = "/apps/hive/demo/student"
  file_format_type = "parquet"
}

LocalFile {
  schema {
    fields {
      name = string
      age = int
    }
  }
  path = "/apps/hive/demo/student"
  file_format_type = "json"
}

LocalFile {
  path = "/tmp/hive/warehouse/test2"
  file_format_type = "text"
  encoding = "gbk"
}
```

##### 多表示例
```json
LocalFile {
  tables_configs = [
    {
      schema {
        table = "student"
      }
      path = "/apps/hive/demo/student"
      file_format_type = "parquet"
    },
    {
      schema {
        table = "teacher"
      }
      path = "/apps/hive/demo/teacher"
      file_format_type = "parquet"
    }
  ]
}

LocalFile {
  tables_configs = [
    {
      schema {
        fields {
          name = string
          age = int
        }
      }
      path = "/apps/hive/demo/student"
      file_format_type = "json"
    },
    {
      schema {
        fields {
          name = string
          age = int
        }
      }
      path = "/apps/hive/demo/teacher"
      file_format_type = "json"
    }
  ]
}
```

##### 转移二进制文件示例
```json
env {
  parallelism = 1
  job.mode = "BATCH"
}

source {
  LocalFile {
    path = "/seatunnel/read/binary/"
    file_format_type = "binary"
  }
}

sink {
  // 你可以转移本地文件到 s3/hdfs/oss 等。
  LocalFile {
    path = "/seatunnel/read/binary2/"
    file_format_type = "binary"
  }
}
```

##### 过滤文件示例
```json
env {
  parallelism = 1
  job.mode = "BATCH"
}

source {
  LocalFile {
    path = "/data/seatunnel/"
    file_format_type = "csv"
    skip_header_row_number = 1
    // 文件示例 abcD2024.csv
    file_filter_pattern = "abc[DX]*.*"
  }
}

sink {
  Console {
  }
}
```

#### Changelog
Change Log

编辑此页

上一页

Lemlist

下一页

Maxcompute

SeaTunnel

FAQ

版本

社区

GitHub

Issue Tracker

Pull Requests

订阅邮件组

How to Subscribe

订阅邮件

邮件归档

Apache SeaTunnel 是一个在 Apache 软件基金会（ASF）孵化器中的项目，由 Apache Incubator 赞助。孵化期要求所有新接受的项目，直到进一步审查表明其基础设施、通信和决策过程与其他成功的 ASF 项目一致之前，都必须进行孵化。虽然孵化状态并不一定反映代码的完整性或稳定性，但它确实表明该项目尚未得到 ASF 的完全认可。

版权所有 © 2021-2022 The Apache Software Foundation。Apache SeaTunnel、SeaTunnel 及其羽毛标志是 The Apache Software Foundation 的商标。