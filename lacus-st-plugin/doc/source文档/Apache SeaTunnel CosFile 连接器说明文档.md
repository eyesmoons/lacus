### Apache SeaTunnel CosFile 连接器说明文档

#### 简介
CosFile 是 Apache SeaTunnel 提供的一个数据源连接器，用于从阿里云的 Cos 文件系统读取数据。该连接器支持批处理和流处理，能够处理多种文件格式，包括 text、csv、parquet、orc、json、excel、xml 和 binary。

#### 支持的引擎
- Spark
- Flink
- SeaTunnel Zeta

#### 关键特性
- **批处理和流处理**：支持批处理和流处理模式。
- **精确一次**：在 pollNext 调用中读取拆分的所有数据，读取的拆分内容将保存在快照中。
- **列映射**：支持用户自定义拆分和列映射。
- **文件格式类型**：支持多种文件格式，包括 text、csv、parquet、orc、json、excel、xml 和 binary。

#### 描述
CosFile 连接器允许用户从阿里云的 Cos 文件系统中读取数据。使用该连接器时，需要确保 Spark/Flink 集成了 Hadoop，并且 SeaTunnel Engine 已经集成了 Hadoop jar。

#### 选项
以下是 CosFile 连接器的配置选项：

| 名称                      | 类型    | 必需 | 默认值              | 描述                                                         |
| ------------------------- | ------- | ---- | ------------------- | ------------------------------------------------------------ |
| path                      | string  | 是   | -                   | 源文件路径。                                                 |
| file_format_type          | string  | 是   | -                   | 文件类型，支持 text、csv、parquet、orc、json、excel、xml 和 binary。 |
| bucket                    | string  | 是   | -                   | Cos 文件系统的 bucket 地址，例如: cos://tyrantlucifer-image-bed。 |
| secret_id                 | string  | 是   | -                   | Cos 文件系统的秘密 id。                                      |
| secret_key                | string  | 是   | -                   | Cos 文件系统的密钥。                                         |
| region                    | string  | 是   | -                   | Cos 文件系统的 region。                                      |
| read_columns              | list    | 是   | -                   | 读取数据源的列的列表，用于字段映射。                         |
| delimiter/field_delimiter | string  | 否   | \001                | 字段分隔符，用于告诉连接器如何对字段进行切片和切块。         |
| parse_partition_from_path | boolean | 否   | true                | 控制是否从文件路径解析分区键和值。                           |
| skip_header_row_number    | long    | 否   | 0                   | 跳过前几行，但仅限于 txt 和 csv。                            |
| date_format               | string  | 否   | yyyy-MM-dd          | 日期类型格式，用于告诉连接器如何将字符串转换为日期。         |
| datetime_format           | string  | 否   | yyyy-MM-dd HH:mm:ss | Datetime 类型格式，用于告诉连接器如何将字符串转换为日期时间。 |
| time_format               | string  | 否   | HH:mm:ss            | 时间类型格式，用于告诉连接器如何将字符串转换为时间。         |
| schema                    | config  | 否   | -                   | 仅当 file_format_type 为文本、json、excel、xml 或 csv 时需要配置。 |
| sheet_name                | string  | 否   | -                   | 仅当 file_format 为 excel 时才需要配置，阅读工作簿的纸张。   |
| xml_row_tag               | string  | 否   | -                   | 仅当 file_format 为 xml 时才需要配置，指定 XML 文件中数据行的标记名称。 |
| xml_use_attr_format       | boolean | 否   | -                   | 仅当 file_format 为 xml 时才需要配置，指定是否使用标记属性格式处理数据。 |
| csv_use_header_line       | boolean | 否   | false               | 仅在文件格式为 csv 时可以选择配置，是否使用标题行来解析文件。 |
| file_filter_pattern       | string  | 否   | -                   | 过滤模式，用于过滤文件，遵循标准正则表达式。                 |
| compress_codec            | string  | 否   | none                | 文件的压缩编解码器。                                         |
| archive_compress_codec    | string  | 否   | none                | 归档文件的压缩编解码器。                                     |
| encoding                  | string  | 否   | UTF-8               | 仅当 file_format_type 为 json、text、csv、xml 时使用，文件的编码。 |

#### 文件格式类型说明
- **text**：纯文本文件，可以指定分隔符。
- **csv**：逗号分隔值文件，可以指定分隔符和标题行。
- **parquet**：列式存储格式，自动识别压缩类型。
- **orc**：列式存储格式，自动识别压缩类型。
- **json**：JSON 格式文件，需要指定 schema。
- **excel**：Excel 文件，需要指定工作表名称。
- **xml**：XML 文件，需要指定行标记和是否使用属性格式。
- **binary**：二进制文件，可以同步任何格式的文件。

#### 示例
以下是一个使用 CosFile 连接器的示例配置：

```plaintext
CosFile {
    path = "/seatunnel/orc"
    bucket = "cosn://seatunnel-test-1259587829"
    secret_id = "xxxxxxxxxxxxxxxxxxx"
    secret_key = "xxxxxxxxxxxxxxxxxxx"
    region = "ap-chengdu"
    file_format_type = "orc"
}
```

#### 总结
CosFile 连接器是 Apache SeaTunnel 提供的一个功能强大的数据源连接器，能够从阿里云的 Cos 文件系统中高效地读取多种格式的数据。通过配置不同的选项，用户可以根据自己的需求灵活地读取和处理数据。