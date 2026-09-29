根据您提供的链接内容，以下是对 Apache SeaTunnel RabbitMQ 连接器的说明文档整理：

---

### Apache SeaTunnel RabbitMQ 连接器说明文档

#### 简介
Apache SeaTunnel 的 RabbitMQ 连接器用于从 RabbitMQ 数据源中读取数据。它支持批处理和流式处理，并确保精确一次处理。

#### 关键特性
- **批处理**：支持批量读取数据。
- **流式处理**：支持实时流式读取数据。
- **精确一次**：确保数据被精确处理一次。
- **列投影**：支持指定需要读取的列。
- **并行性**：支持并行处理，但为了实现精确一次，源必须是非并行的（parallelism 设置为 1）。
- **用户自定义分割**：支持用户自定义数据分割逻辑。

#### 配置选项
以下是 RabbitMQ 连接器的主要配置选项：

| 参数名                     | 类型    | 是否必需 | 默认值 | 描述                                                         |
| -------------------------- | ------- | -------- | ------ | ------------------------------------------------------------ |
| host                       | string  | 是       | -      | 连接 RabbitMQ 的默认主机地址。                               |
| port                       | int     | 是       | -      | 连接 RabbitMQ 的默认端口号。                                 |
| virtual_host               | string  | 是       | -      | 连接 RabbitMQ 时使用的虚拟主机。                             |
| username                   | string  | 是       | -      | 连接 RabbitMQ 时使用的用户名。                               |
| password                   | string  | 是       | -      | 连接 RabbitMQ 时使用的密码。                                 |
| queue_name                 | string  | 是       | -      | 发布消息到的队列名称。                                       |
| routing_key                | string  | 否       | -      | 发布消息时的路由键。                                         |
| exchange                   | string  | 否       | -      | 发布消息时的交换器。                                         |
| schema                     | config  | 是       | -      | 上游数据的模式字段。                                         |
| url                        | string  | 否       | -      | 设置 AMQP URI 的便捷方法，包含主机、端口、用户名、密码和虚拟主机。 |
| network_recovery_interval  | int     | 否       | -      | 自动重连前等待的时间（毫秒）。                               |
| topology_recovery_enabled  | boolean | 否       | -      | 是否启用拓扑恢复。                                           |
| automatic_recovery_enabled | boolean | 否       | -      | 是否启用连接恢复。                                           |
| connection_timeout         | int     | 否       | -      | TCP 建立连接的超时时间（毫秒）；零表示无限。                 |
| requested_channel_max      | int     | 否       | -      | 请求的最大通道数；零表示无限制。**注意：值必须在 0 到 65535 之间（AMQP 0-9-1 中的无符号短整型）。** |
| requested_frame_max        | int     | 否       | -      | 请求的最大帧大小。                                           |
| requested_heartbeat        | int     | 否       | -      | 设置请求的心跳超时时间。**注意：值必须在 0 到 65535 之间（AMQP 0-9-1 中的无符号短整型）。** |
| prefetch_count             | int     | 否       | -      | 接收消息时无需确认的最大消息数。                             |
| delivery_timeout           | long    | 否       | -      | 下一条消息交付的最大等待时间（毫秒）。                       |

#### 常见选项
- **common-options**：源插件通用参数，详细请参考源通用选项。

#### 示例配置
以下是一个简单的配置示例：

```plaintext
source {
  RabbitMQ {
    host = "rabbitmq-e2e"
    port = 5672
    virtual_host = "/"
    username = "guest"
    password = "guest"
    queue_name = "test"
    schema = {
      fields {
        id = bigint
        c_map = "map<string, smallint>"
        c_array = "array<tinyint>"
      }
    }
  }
}
```

#### 变更日志
- 变更日志的具体内容请参考官方文档中的 Change Log 部分。

#### 版本信息
- Apache SeaTunnel 是 Apache 软件基金会（ASF）孵化项目，由 Apache Incubator 赞助。孵化状态表示项目尚未完全得到 ASF 的认可，但正在逐步完善。

#### 版权信息
- 版权 © 2021-2022 The Apache Software Foundation。Apache SeaTunnel、SeaTunnel 及其羽毛标志是 The Apache Software Foundation 的商标。

---

以上是 Apache SeaTunnel RabbitMQ 连接器的说明文档整理，希望对您有所帮助。