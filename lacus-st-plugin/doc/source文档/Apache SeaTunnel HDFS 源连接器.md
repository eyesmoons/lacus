### Apache SeaTunnel HDFS 连接器

#### 简介
Apache SeaTunnel 是一个开源的分布式数据集成平台，支持海量数据的实时同步。HDFS 连接器是 SeaTunnel 的一部分，用于将数据写入 HDFS（Hadoop 分布式文件系统）。

#### 使用前提
在使用 HDFS 连接器之前，您需要确保您的 Spark/Flink 集群已经集成了 Hadoop。测试过的 Hadoop 版本是 2.x。

#### 安装与集成
如果您使用 SeaTunnel Engine，则在下载和安装 SeaTunnel Engine 时会自动集成 Hadoop。您可以通过检查 `${SEATUNNEL_HOME}/lib` 下的 jar 包来确认这一点。

#### 示例
以下是一个简单的示例，展示如何使用 HDFS 连接器将数据写入 HDFS 文件系统：

```json
{
  "name": "HdfsFileSink",
  "type": "hdfs-file-sink",
  "path": "/user/hdfs/path",
  "filePrefix": "data_",
  "fileSuffix": ".txt",
  "fieldDelimiter": ",",
  "recordDelimiter": "\n",
  "encoding": "UTF-8",
  "partitionField": "partitionField",
  "partitionType": "long",
  "numPartitions": 10
}
```

#### 配置参数
- **path**: HDFS 文件的存储路径。
- **filePrefix**: 文件前缀。
- **fileSuffix**: 文件后缀。
- **fieldDelimiter**: 字段分隔符。
- **recordDelimiter**: 记录分隔符。
- **encoding**: 文件编码。
- **partitionField**: 分区字段。
- **partitionType**: 分区类型。
- **numPartitions**: 分区数量。

#### 注意事项
- 确保您的集群对 HDFS 有正确的访问权限，包括文件的读写权限、目录的创建和删除权限等。
- 如果您使用 SeaTunnel Engine，则在下载和安装 SeaTunnel Engine 时会自动集成 Hadoop。

#### 参考资料
- [Apache SeaTunnel HDFS 连接器文档](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/HdfsFile/)
- [SeaTunnel 官方文档](https://seatunnel.apache.org/)

希望这份整理的说明文档对您有所帮助。如果您有任何其他问题或需要进一步的帮助，请随时告诉我。