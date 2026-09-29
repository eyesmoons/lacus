### Apache SeaTunnel Phoenix 连接器说明文档

#### 简介
Apache SeaTunnel 的 Phoenix 源连接器用于通过 JDBC 连接器读取 Phoenix 数据。它支持批处理模式和流模式，适用于 Apache Phoenix 版本 4.xx 和 5.xx。底层实现通过 Phoenix 的 JDBC 驱动程序执行 upsert 语句将数据写入 HBase。

#### 关键特性
- **批处理**：支持批量数据处理。
- **流处理**：支持流式数据处理。
- **精确一次**：确保数据精确一次性写入。
- **列投影**：支持查询 SQL，实现投影效果。
- **并行度**：支持用户自定义分片。

#### 连接方法
Apache SeaTunnel 提供两种通过 Java JDBC 连接 Phoenix 的方法：
1. 通过 JDBC 连接到 Zookeeper。
2. 使用 JDBC thin 客户端连接到 queryserver。

#### 配置选项
以下是 Phoenix 连接器的主要配置选项：

1. **driver**
   - 描述：指定使用的 JDBC 驱动程序。
   - 默认值：`org.apache.phoenix.jdbc.PhoenixDriver`（thick 驱动程序）或 `org.apache.phoenix.queryserver.client.Driver`（thin 驱动程序）。
   - 示例：
     ```json
     driver = org.apache.phoenix.jdbc.PhoenixDriver
     ```

2. **url**
   - 描述：指定连接的 URL。
   - 默认值：
     - thick 驱动程序：`jdbc:phoenix:localhost:2182/hbase`
     - thin 驱动程序：`jdbc:phoenix:thin:url=http://localhost:8765;serialization=PROTOBUF`
   - 示例：
     ```json
     url = jdbc:phoenix:thin:url=http://spark_e2e_phoenix_sink:8765;serialization=PROTOBUF
     ```

3. **common options**
   - 描述：源插件常用参数，详细参数请参考 SeaTunnel 源插件常见选项。

#### 示例配置
以下是使用 thick 和 thin 客户端驱动程序的示例配置：

1. **使用 thick 客户端驱动程序**
   ```json
   Jdbc {
       driver = org.apache.phoenix.jdbc.PhoenixDriver
       url = jdbc:phoenix:localhost:2182/hbase
       query = select age, name from test.source
   }
   ```

2. **使用 thin 客户端驱动程序**
   ```json
   Jdbc {
       driver = org.apache.phoenix.queryserver.client.Driver
       url = jdbc:phoenix:thin:url=http://spark_e2e_phoenix_sink:8765;serialization=PROTOBUF
       query = select age, name from test.source
   }
   ```

#### 变更日志
更多关于 SeaTunnel Phoenix 连接器的变更日志和详细信息，请参考官方文档。

#### 总结
Apache SeaTunnel 的 Phoenix 连接器是一个功能强大的工具，支持通过 JDBC 连接和读取 Phoenix 数据，适用于批处理和流处理模式。通过合理的配置，可以高效地管理和处理数据。

#### 官方文档链接
[Apache SeaTunnel Phoenix 连接器文档](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/Phoenix)

希望这份说明文档能帮助你更好地理解和使用 Apache SeaTunnel 的 Phoenix 连接器。