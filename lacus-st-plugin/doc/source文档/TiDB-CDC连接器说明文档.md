根据您提供的链接，我整理了关于Apache SeaTunnel TiDB连接器的说明文档。以下是文档的主要内容：

### TiDB-CDC连接器简介

TiDB-CDC连接器允许从TiDB数据库读取快照数据和增量数据。本文将介绍如何设置TiDB-CDC连接器，在TiDB数据库中对数据进行快照和捕获流事件。

### 功能特性

- **快照数据读取**：可以从TiDB数据库中读取快照数据，即某一时刻数据库的完整状态。
- **增量数据捕获**：可以捕获TiDB数据库中的增量数据，即数据发生变化时产生的数据变更。

### 设置步骤

1. **环境准备**：
   - 确保已经安装了Apache SeaTunnel。
   - 确保已经安装了TiDB数据库，并且可以正常访问。

2. **配置连接器**：
   - 在SeaTunnel配置文件中添加TiDB-CDC连接器的配置。
   - 配置需要连接的TiDB数据库的连接信息，包括主机名、端口号、数据库名、用户名和密码。

3. **启动SeaTunnel**：
   - 使用SeaTunnel命令行工具启动任务，开始从TiDB数据库读取数据。

### 配置示例

以下是一个简单的TiDB-CDC连接器配置示例：

```json
{
  "source": {
    "type": "tidb-cdc",
    "version": "2.3.11",
    "config": {
      "hostname": "127.0.0.1",
      "port": 4000,
      "database": "test",
      "username": "root",
      "password": "password",
      "snapshot": {
        "enabled": true,
        "interval": "1m"
      }
    }
  }
}
```

### 常见问题解答

- **SeaTunnel CDC 同步需要的权限如何开启？**
  - 需要在TiDB数据库中创建一个具有SELECT权限的账号，用于连接TiDB数据库。

- **支持哪些版本的TiDB数据库？**
  - SeaTunnel TiDB连接器支持多个版本的TiDB数据库，具体请参考官方文档。

- **如何处理数据同步中的错误？**
  - SeaTunnel提供了错误处理机制，可以在配置文件中设置错误处理策略，如重试、告警等。

### 更多信息

- **官方文档**：[Apache SeaTunnel TiDB连接器文档](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/TiDB-CDC/)
- **GitHub仓库**：[Apache SeaTunnel GitHub](https://github.com/apache/seatunnel)

以上是关于Apache SeaTunnel TiDB连接器的说明文档的主要内容。如果您需要更详细的信息，请参考官方文档。