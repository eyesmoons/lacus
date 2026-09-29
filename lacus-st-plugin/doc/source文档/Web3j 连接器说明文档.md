根据您提供的链接内容，以下是对 Apache SeaTunnel 2.3.11 版本中 Web3j 连接器的说明文档整理：

---

### Web3j 连接器说明文档

#### 简介
Web3j 连接器是 Apache SeaTunnel 的一部分，用于从区块链中读取数据，如区块信息、交易、智能合约事件等。目前，它主要支持读取区块高度数据。

#### 支持的引擎
- Spark
- Flink
- Seatunnel Zeta

#### 主要特性
- 支持批处理（batch）
- 支持流处理（stream）
- 精确一次（exactly-once）
- 列投影（column projection）
- 并行处理（parallelism）
- 支持用户自定义分割（support user-defined split）

#### 描述
Web3j 源连接器用于读取区块链数据。它可以读取区块信息、交易、智能合约事件等数据。目前，它主要支持读取区块高度数据。

#### 源选项
| 名称 | 类型   | 是否必需 | 默认值 | 描述                                                         |
| ---- | ------ | -------- | ------ | ------------------------------------------------------------ |
| url  | String | 是       | -      | 当使用 Infura 作为服务提供商时，用于与 Ethereum 网络通信的 URL。 |

#### 如何创建 Http 数据同步作业
```plaintext
env {
  parallelism = 1
  job.mode = "BATCH"
}

source {
  Web3j {
    url = "https://mainnet.infura.io/v3/xxxxx"
  }
}

# 控制台打印读取的 Http 数据
sink {
  Console {
    parallelism = 1
  }
}
```

#### 输出示例
```json
{"blockNumber":19525949,"timestamp":"2024-03-27T13:28:45.605Z"}
```

#### 更改日志
- 版本更新和改进记录将在官方文档中提供。

#### 版本信息
- Apache SeaTunnel 是在 Apache 软件基金会（ASF）的孵化器项目，由 Apache Incubator 赞助。

#### 版权信息
- 版权 © 2021-2022 The Apache Software Foundation.
- Apache SeaTunnel、SeaTunnel 及其羽毛标志是 The Apache Software Foundation 的商标。

---

以上内容整理自 Apache SeaTunnel 2.3.11 版本中 Web3j 连接器的官方文档。如需更多详细信息，请参考官方文档或相关社区资源。