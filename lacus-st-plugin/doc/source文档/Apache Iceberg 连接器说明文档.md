Apache Iceberg 连接器的说明文档。以下是文档的主要内容：

### Apache Iceberg 连接器说明文档

#### 简介
Apache Iceberg 是一种开放的数据湖表格格式，它支持架构演变、时间旅行和隐藏分区等功能。Apache Iceberg 连接器是 Seatunnel 项目的一部分，它允许用户通过 Seatunnel 将数据传输到 Apache Iceberg 数据源。

#### 支持的功能
- **支持多表写入**：可以同时写入多个 Iceberg 表。
- **支持 CDC 模式**：支持变更数据捕获（CDC），可以实时捕获数据变更并写入 Iceberg 表。
- **自动建表及表结构变更**：连接器支持自动创建表以及表结构的变更。

#### 主要特性
- **数据源支持**：连接器可以与多种数据源进行交互，包括 HDFS 和云存储服务如 AWS S3。
- **依赖**：使用 Iceberg 连接器需要依赖 Apache Iceberg 库和相应的数据存储服务。

#### 使用方法
1. **配置环境变量**：确保配置了 Hadoop 环境变量 `HADOOP_CONF_DIR`，这样 Flink 在执行 `sql-client.sh` 时能够找到 Iceberg 的 jar 包，并直接操作 HDFS。
2. **创建 Iceberg 数据节点**：在数据目标中点击 [新建] → [Iceberg]，设置数据目标名称并选择创建好的 Iceberg 数据节点，库表名称可以选择与数据源一致，或者自定义。
3. **数据传输**：可以使用 Firehose 将流数据传输到亚马逊 S3 中的 Apache Iceberg Tables。

#### 示例
- **MySQL 到 Iceberg 示例**：通过 Apache InLong，可以将 MySQL 数据同步到 Iceberg 数据源。
- **创建 Apache Iceberg 外部表**：可以使用 JSON 元数据文件创建 Iceberg 外部表，但建议使用更动态的方法来管理表的元数据。

#### 参考资料
- [Apache Iceberg 官方文档](https://seatunnel.apache.org/zh-CN/docs/2.3.9/connector-v2/sink/Iceberg/)
- [阿里云 Iceberg 连接器文档](https://www.alibabacloud.com/help/zh/flink/apache-iceberg-connector)
- [Google Cloud Iceberg 数据流管理](https://cloud.google.com/dataflow/docs/guides/managed-io-iceberg?hl=zh-cn)

#### 注意事项
- 确保所有 Iceberg 集群节点和 Seatunnel 实例之间的网络连接正常。
- 在使用动态目标时，需要为 `table` 配置参数提供模板。

通过以上内容，您可以对 Apache Iceberg 连接器有一个全面的了解，并能够根据文档指导进行实际操作。如果您有更多具体的问题或需要进一步的帮助，请随时提问。