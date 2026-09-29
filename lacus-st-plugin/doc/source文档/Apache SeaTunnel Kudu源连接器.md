Apache SeaTunnel Kudu连接器是一个用于从Kudu数据库中读取数据的数据源连接器。以下是对该连接器的配置和使用说明的整理：

### 支持的Kudu版本
- 1.11.1
- 1.12.0
- 1.13.0
- 1.14.0
- 1.15.0

### 支持的引擎
- Spark
- Flink
- SeaTunnel Zeta

### 关键特性
- 支持批量处理（batch）
- 支持精确一次（exactly-once）处理
- 支持列投影（column projection）
- 支持并行处理（parallelism）
- 支持用户自定义分割（support user-defined split）

### 描述
Kudu数据源连接器用于从Kudu数据库中读取数据。测试的Kudu版本为1.11.1。

### 数据类型映射
以下是从Kudu数据类型到SeaTunnel数据类型的映射：

| Kudu数据类型    | SeaTunnel数据类型 |
| --------------- | ----------------- |
| BOOL            | BOOLEAN           |
| INT8            | -                 |
| INT16           | -                 |
| INT32           | INT               |
| INT64           | BIGINT            |
| DECIMAL         | DECIMAL           |
| FLOAT           | FLOAT             |
| DOUBLE          | DOUBLE            |
| STRING          | STRING            |
| UNIXTIME_MICROS | TIMESTAMP         |
| BINARY          | BYTES             |

### 源选项
以下是Kudu数据源连接器的配置选项：

| 名称                                      | 类型   | 是否必填 | 默认值                                         | 描述                                                         |
| ----------------------------------------- | ------ | -------- | ---------------------------------------------- | ------------------------------------------------------------ |
| kudu_masters                              | String | 是       | -                                              | Kudu主地址。用“，”分隔，例如“192.168.88.110:7051”。          |
| table_name                                | String | 是       | -                                              | Kudu表的名字。                                               |
| client_worker_count                       | Int    | 否       | 2 * Runtime.getRuntime().availableProcessors() | Kudu worker数量。默认值是当前CPU核心数的两倍。               |
| client_default_operation_timeout_ms       | Long   | 否       | 30000                                          | Kudu正常操作超时时间。                                       |
| client_default_admin_operation_timeout_ms | Long   | 否       | 30000                                          | Kudu管理员操作超时时间。                                     |
| enable_kerberos                           | Bool   | 否       | false                                          | 是否启用Kerberos principal。                                 |
| kerberos_principal                        | String | 否       | -                                              | Kerberos principal。注意所有zeta节点都需要这个文件。         |
| kerberos_keytab                           | String | 否       | -                                              | Kerberos keytab。注意所有zeta节点都需要这个文件。            |
| kerberos_krb5conf                         | String | 否       | -                                              | Kerberos krb5配置。注意所有zeta节点都需要这个文件。          |
| scan_token_query_timeout                  | Long   | 否       | 30000                                          | 连接扫描令牌的超时时间。如果未设置，将与operationTimeout相同。 |
| scan_token_batch_size_bytes               | Int    | 否       | 1024 * 1024                                    | Kudu扫描字节数。每次读取的最大字节数，默认为1MB。            |
| filter                                    | Int    | 否       | 1024 * 1024                                    | Kudu扫描过滤表达式，目前不支持。                             |
| schema                                    | Map    | 否       | 1024 * 1024                                    | SeaTunnel Schema。                                           |
| table_list                                | Array  | 否       | -                                              | 要读取的表列表。可以使用此配置代替table_path，例如：table_list = [{ table_name = "kudu_source_table_1"},{ table_name = "kudu_source_table_2"}] |
| common-options                            | -      | -        | -                                              | 源插件公共参数，请参考源公共选项的详细信息。                 |

### 任务示例
以下是一些使用Kudu数据源连接器的任务示例：

#### 简单示例
以下示例展示了如何从名为"kudu_source_table"的Kudu表中读取数据，并将数据打印到控制台，同时写入到名为"kudu_sink_table"的Kudu表中。

```plaintext
# 定义运行时环境
env {
 parallelism = 2
 job.mode = "BATCH"
}
source {
 # 这是一个示例源插件，仅用于测试和演示功能
 kudu {
 kudu_masters = "kudu-master:7051"
 table_name = "kudu_source_table"
 plugin_output = "kudu"
 enable_kerberos = true
 kerberos_principal = "xx@xx.COM"
 kerberos_keytab = "xx.keytab"
 }
}
transform {
}
sink {
 console {
 plugin_input = "kudu"
 }
 kudu {
 plugin_input = "kudu"
 kudu_masters = "kudu-master:7051"
 table_name = "kudu_sink_table"
 enable_kerberos = true
 kerberos_principal = "xx@xx.COM"
 kerberos_keytab = "xx.keytab"
 }
}
```

#### 多表示例
以下示例展示了如何从多个Kudu表中读取数据。

```plaintext
env {
 # 你可以在这里设置引擎配置
 parallelism = 1
 job.mode = "STREAMING"
 checkpoint.interval = 5000
}
source {
 # 这是一个示例源插件，仅用于测试和演示功能
 kudu{
 kudu_masters = "kudu-master:7051"
 table_list = [
 { table_name = "kudu_source_table_1" },
 { table_name = "kudu_source_table_2" }
 ]
 plugin_output = "kudu"
}
}
transform {
}
sink {
 Assert {
 rules {
 table-names = ["kudu_source_table_1", "kudu_source_table_2"]
 }
 }
}
```

以上是Apache SeaTunnel Kudu连接器的配置和使用说明。希望这些信息能帮助你更好地使用该连接器。