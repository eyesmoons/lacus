Apache SeaTunnel HBase 连接器的说明文档整理：

### HBase 源连接器

#### 描述
Apache SeaTunnel 的 HBase 源连接器用于从 Apache HBase 数据库中读取数据。它支持批处理和流处理模式，能够确保数据的精确一次处理，并支持用户定义的拆分策略。

#### 主要功能
- **批处理**：支持批量读取 HBase 中的数据。
- **流处理**：支持实时流式读取 HBase 中的数据。
- **精确一次**：确保数据在读取过程中不会丢失或重复。
- **Schema**：支持配置数据模式，定义如何映射 HBase 中的列族和列。
- **并行度**：支持并行读取，提高数据处理效率。
- **支持用户定义的拆分**：允许用户自定义数据拆分策略。

#### 选项
以下是 HBase 源连接器的主要配置选项：

| 名称               | 类型    | 必填 | 默认值 | 描述                                                         |
| ------------------ | ------- | ---- | ------ | ------------------------------------------------------------ |
| zookeeper_quorum   | string  | 是   | -      | HBase 的 Zookeeper 集群主机地址，例如：“hadoop001:2181,hadoop002:2181,hadoop003:2181” |
| table              | string  | 是   | -      | 要读取的 HBase 表名，例如：“seatunnel”                       |
| schema             | config  | 是   | -      | 定义数据模式，包括列族和列的数据类型。HBase 使用字节数组进行存储，因此需要为每一列配置数据类型。 |
| hbase_extra_config | string  | 否   | -      | HBase 的额外配置项，可以在此处配置 HBase 的特定参数。        |
| caching            | int     | 否   | -1     | 设置在扫描过程中一次从服务器端获取的行数，以减少客户端与服务器之间的往返次数。 |
| batch              | int     | 否   | -1     | 设置在扫描过程中每次返回的最大列数，特别适用于处理有很多列的行。 |
| cache_blocks       | boolean | 否   | false  | 设置在扫描过程中是否缓存数据块。如果设置为 false，则在扫描过程中不会缓存数据块，从而减少内存的使用。 |

#### 常用选项
- **Source 插件常用参数**：具体请参考 SeaTunnel Source 插件的常用选项文档。

#### 示例
以下是一个使用 HBase 源连接器的配置示例：

```yaml
source {
  Hbase {
    zookeeper_quorum = "hadoop001:2181,hadoop002:2181,hadoop003:2181"
    table = "seatunnel_test"
    caching = 1000
    batch = 100
    cache_blocks = false
    schema = {
      columns = [
        { name = "rowkey", type = "string" },
        { name = "columnFamily1:column1", type = "boolean" },
        { name = "columnFamily1:column2", type = "double" },
        { name = "columnFamily2:column1", type = "bigint" }
      ]
    }
  }
}
```

#### 变更日志
- **Change Log**：具体变更日志请参考 SeaTunnel 的官方文档。

#### 版本信息
- **版本**：2.3.11

#### 社区与支持
- **社区**：Apache SeaTunnel 是一个在 Apache 软件基金会（ASF）孵化中的项目，由 Apache Incubator 赞助。
- **GitHub**：[Apache SeaTunnel GitHub 仓库](https://github.com/apache/seatunnel)
- **Issue Tracker**：[Apache SeaTunnel Issue Tracker](https://issues.apache.org/jira/browse/SEATUNNEL)
- **Pull Requests**：[Apache SeaTunnel Pull Requests](https://github.com/apache/seatunnel/pulls)
- **订阅邮件组**：[Apache SeaTunnel 邮件组订阅](https://mailchimp.com/forms/subscribe/sea-tunnel)

#### 版权信息
- **Copyright © 2021-2022 The Apache Software Foundation**。Apache SeaTunnel, SeaTunnel, 和其羽毛标志是 The Apache Software Foundation 的商标。

通过以上整理，您可以对 Apache SeaTunnel 的 HBase 源连接器有一个全面的了解，并能够根据实际需求进行配置和使用。