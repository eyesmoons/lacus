Apache SeaTunnel OBS文件连接器是一个用于从华为云对象存储服务（OBS）读取数据的源连接器。它支持多种数据引擎，包括Spark、Flink和SeaTunnel Zeta，并具有批量处理、流处理、精确一次处理等特性。以下是该连接器的详细说明：

### 支持的引擎
- Spark
- Flink
- Seatunnel Zeta

### 主要特性
- **批量处理和流处理**：支持批量读取和流式读取数据。
- **精确一次处理**：确保数据在处理过程中只被处理一次。
- **读取所有数据**：在一个`pollNext`调用中读取一个split中的所有数据，读取的split将被保存到快照中。
- **列投影**：允许用户指定读取的数据列，实现字段投影。
- **并行性**：支持并行处理数据。
- **用户定义的split**：允许用户自定义split的读取方式。
- **文件格式类型**：支持多种文件格式，包括文本、CSV、Parquet、ORC、JSON和Excel。

### 描述
该连接器用于从华为云OBS文件系统读取数据。如果使用Spark或Flink，必须确保你的Spark或Flink集群已经集成了Hadoop，并且测试的Hadoop版本是2.x。如果使用SeaTunnel Engine，则在下载和安装SeaTunnel Engine时会自动集成Hadoop jar，你可以在`${SEATUNNEL_HOME}/lib`下检查jar包以确认这一点。

为了支持更多的文件类型，我们进行了一些权衡，因此我们使用HDFS协议对OBS进行内部访问，而这个连接器需要一些Hadoop依赖。它只支持Hadoop版本2.9.X+。

### 必要的Jar列表
- **hadoop-huaweicloud**：支持版本 >= 3.1.1.29
- **esdk-obs-java**：支持版本 >= 3.19.7.3
- **okhttp**：支持版本 >= 3.11.0
- **okio**：支持版本 >= 1.14.0

请下载支持列表中对应的'Maven'版本，并将它们复制到`$SEATUNNEL_HOME/plugins/jdbc/lib/`工作目录。同时，将所有jar复制到`$SEATUNNEL_HOME/lib/`。

### 选项
| 名称                      | 类型    | 是否必需 | 默认值              | 描述                                                         |
| ------------------------- | ------- | -------- | ------------------- | ------------------------------------------------------------ |
| path                      | string  | 是       | -                   | 目标目录路径                                                 |
| file_format_type          | string  | 是       | -                   | 文件类型。提示：支持text、csv、parquet、orc、json、excel。   |
| bucket                    | string  | 是       | -                   | OBS文件系统的bucket地址，例如：obs://obs-bucket-name         |
| access_key                | string  | 是       | -                   | OBS文件系统的访问密钥                                        |
| access_secret             | string  | 是       | -                   | OBS文件系统的访问密钥                                        |
| endpoint                  | string  | 是       | -                   | OBS文件系统的端点                                            |
| read_columns              | list    | 是       | -                   | 数据源的可读列列表，用户可以使用它来实现字段投影。           |
| delimiter                 | string  | 否       | \001                | 字段分隔符，用于告诉连接器如何在读取文本文件时如何分割字段。 |
| parse_partition_from_path | boolean | 否       | true                | 控制是否从文件路径解析分区键和值。                           |
| skip_header_row_number    | long    | 否       | 0                   | 跳过前几行，但仅适用于txt和csv。                             |
| date_format               | string  | 否       | yyyy-MM-dd          | 日期格式，用于告诉连接器如何将字符串转换为日期。             |
| datetime_format           | string  | 否       | yyyy-MM-dd HH:mm:ss | 日期时间格式，用于告诉连接器如何将字符串转换为日期时间。     |
| time_format               | string  | 否       | HH:mm:ss            | 时间格式，用于告诉连接器如何将字符串转换为时间。             |
| filename_extension        | string  | 否       | -                   | 过滤文件名扩展名，用于过滤具有特定扩展名的文件。             |
| schema                    | config  | 否       | -                   | 提示                                                         |
| common-options            | no      | -        | -                   | 提示                                                         |
| sheet_name                | string  | 否       | -                   | 读取工作簿的表，仅当文件格式为excel时使用。                  |

### 提示
- **parse_partition_from_path**：控制是否从文件路径解析分区键和值。
- **date_format**：日期格式，用于告诉连接器如何将字符串转换为日期。
- **datetime_format**：日期时间格式，用于告诉连接器如何将字符串转换为日期时间。
- **time_format**：时间格式，用于告诉连接器如何将字符串转换为时间。
- **skip_header_row_number**：跳过前几行，但仅适用于txt和csv。
- **file_format_type**：文件类型，支持text、csv、parquet、orc、json、excel。
- **schema**：上游数据的模式。
- **read_columns**：数据源的可读列列表，用户可以使用它来实现字段投影。
- **common options**：源插件通用参数，请参考源通用选项的详细信息。

### 任务示例
以下是一些简单配置的示例：

#### 文本文件
```plaintext
ObsFile {
    path = "/seatunnel/text"
    bucket = "obs://obs-bucket-name"
    access_key = "xxxxxxxxxxxxxxxxx"
    access_secret = "xxxxxxxxxxxxxxxxxxxxxx"
    endpoint = "obs.xxxxxx.myhuaweicloud.com"
    file_format_type = "text"
}
```

#### Parquet文件
```plaintext
ObsFile {
    path = "/seatunnel/parquet"
    bucket = "obs://obs-bucket-name"
    access_key = "xxxxxxxxxxxxxxxxx"
    access_secret = "xxxxxxxxxxxxxxxxxxxxxx"
    endpoint = "obs.xxxxxx.myhuaweicloud.com"
    file_format_type = "parquet"
}
```

#### ORC文件
```plaintext
ObsFile {
    path = "/seatunnel/orc"
    bucket = "obs://obs-bucket-name"
    access_key = "xxxxxxxxxxxxxxxxx"
    access_secret = "xxxxxxxxxxxxxxxxxxxxxx"
    endpoint = "obs.xxxxxx.myhuaweicloud.com"
    file_format_type = "orc"
}
```

#### JSON文件
```plaintext
ObsFile {
    path = "/seatunnel/json"
    bucket = "obs://obs-bucket-name"
    access_key = "xxxxxxxxxxxxxxxxx"
    access_secret = "xxxxxxxxxxxxxxxxxxxxxx"
    endpoint = "obs.xxxxxx.myhuaweicloud.com"
    file_format_type = "json"
}
```

#### Excel文件
```plaintext
ObsFile {
    path = "/seatunnel/excel"
    bucket = "obs://obs-bucket-name"
    access_key = "xxxxxxxxxxxxxxxxx"
    access_secret = "xxxxxxxxxxxxxxxxxxxxxx"
    endpoint = "obs.xxxxxx.myhuaweicloud.com"
    file_format_type = "excel"
}
```

#### CSV文件
```plaintext
ObsFile {
    path = "/seatunnel/csv"
    bucket = "obs://obs-bucket-name"
    access_key = "xxxxxxxxxxxxxxxxx"
    access_secret = "xxxxxxxxxxxxxxxxxxxxxx"
    endpoint = "obs.xxxxxx.myhuaweicloud.com"
    file_format_type = "csv"
    delimiter = ","
}
```

### 更改日志
- 本文档为Apache SeaTunnel OBS文件连接器的最新版本（2.3.11）的说明。

### 版权信息
Apache SeaTunnel是一个在Apache软件基金会（ASF）孵化器中的项目，由Apache Incubator赞助。孵化期是所有新接受的项目必须经历的，直到进一步的审查表明基础设施、通信和决策过程已经稳定，与其他成功的ASF项目一致。孵化状态并不一定反映代码的完整性或稳定性，但它确实表明该项目尚未得到ASF的完全认可。

版权 © 2021-2022 The Apache Software Foundation. Apache SeaTunnel、SeaTunnel及其羽毛标志是The Apache Software Foundation的商标。