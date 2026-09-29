根据您提供的链接内容，以下是整理的关于 Apache SeaTunnel 连接器 V2 版本中，与 Apache Pulsar 交互的文档说明：

### Apache SeaTunnel 连接器 V2 - Apache Pulsar 文档说明

#### 简介
Apache SeaTunnel 是一个易用、高性能的分布式数据集成平台，支持大规模数据的实时同步。SeaTunnel 提供了与多种数据源和目标系统的连接器，其中包括 Apache Pulsar。

#### Apache Pulsar 连接器
Apache Pulsar 连接器允许 SeaTunnel 与 Apache Pulsar 消息系统进行交互。Pulsar 是一个云原生分布式消息流平台，具有高吞吐量、低延迟的特点，适用于实时数据处理。

#### 功能
- **数据源（Source）**：从 Pulsar 主题中读取数据。
- **数据接收器（Sink）**：将数据写入 Pulsar 主题。

#### 安装与配置
在开始使用 Apache Pulsar 连接器之前，需要确保已经安装了以下软件：
- Java (Java 8 或 11)
- 其他可能需要的依赖库

#### 使用示例
以下是一个简单的使用 SeaTunnel 与 Apache Pulsar 交互的示例：

```java
// 创建 Pulsar 数据源
PulsarSource<String> source = PulsarSource.<String>builder()
    .setTopic("persistent://public/default/my-topic")
    .setConsumerName("my-consumer")
    .build();

// 创建 Pulsar 数据接收器
PulsarSink<String> sink = PulsarSink.<String>builder()
    .setTopic("persistent://public/default/my-output-topic")
    .build();

// 在 SeaTunnel 任务中使用数据源和数据接收器
StreamSource<String> streamSource = StreamSource.from(source);
StreamSink<String> streamSink = StreamSink.to(sink);

// 执行任务
streamSource.process().to(streamSink).execute();
```

#### 注意事项
- 确保在配置文件中正确设置了 Pulsar 服务器的地址和认证信息。
- 根据实际需求调整数据源的消费者组和数据接收器的主题。
- 处理异常和错误，确保数据传输的可靠性。

#### 文档链接
更多详细信息和高级配置，请参考官方文档：
[Apache SeaTunnel Pulsar 连接器文档](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/)

通过以上说明，您应该能够了解如何使用 Apache SeaTunnel 的 Apache Pulsar 连接器进行数据集成和实时处理。如果有更多问题或需要进一步的帮助，请参考官方文档或社区资源。