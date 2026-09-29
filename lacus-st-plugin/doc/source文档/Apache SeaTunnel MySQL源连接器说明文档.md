根据您提供的链接内容，以下是对Apache SeaTunnel 2.3.11版本中MySQL源连接器的说明文档整理：

### Apache SeaTunnel MySQL源连接器说明文档

#### 简介
Apache SeaTunnel是一个开源的数据集成工具，支持多种数据源的连接和数据流的处理。MySQL源连接器是SeaTunnel的一部分，用于从MySQL数据库中读取数据。

#### 安装与配置
1. **安装SeaTunnel**：
   - 下载SeaTunnel安装包：`apache-seatunnel-2.3.11-bin.tar.gz`
   - 解压安装包到指定目录
   - 配置环境变量（如`PATH`）

2. **配置MySQL连接器**：
   - 编辑SeaTunnel配置文件（通常是`config.json`）
   - 添加MySQL源连接器配置

#### 配置属性
以下是MySQL源连接器的常用配置属性：

- **`connector-type`**：指定连接器类型，值为`mysql-cdc`。
- **`hostname`**：MySQL数据库服务器的IP地址或主机名。
- ****`port`**：MySQL数据库服务器的端口号，默认为3306。
- **`username`**：连接MySQL数据库的用户名。
- **`password`**：连接MySQL数据库的密码。
- **`database`**：要读取数据的数据库名称。
- **`table`**：要读取的数据表名称。
- **`snapshot`**：是否进行表快照，默认为`true`。
- **`binlog`**：是否读取binlog，默认为`true`。
- **`binlog-start-position`**：binlog的起始位置。
- **`topic`**：Kafka主题名称，用于存储binlog数据。
- **`scan-interval`**：扫描间隔时间，单位为毫秒。

#### 示例配置
```json
{
  "job": {
    "name": "mysql-cdc-job",
    "type": "stream"
  },
  "source": {
    "connector-type": "mysql-cdc",
    "hostname": "localhost",
    "port": 3306,
    "username": "root",
    "password": "password",
    "database": "test",
    "table": "orders",
    "snapshot": true,
    "binlog": true,
    "binlog-start-position": "0",
    "topic": "mysql-cdc-topic",
    "scan-interval": 1000
  },
  "sink": {
    "connector-type": "print",
    "print": true
  }
}
```

#### 使用场景
- **数据同步**：将MySQL数据库中的数据同步到其他数据源（如HDFS、Kafka等）。
- **数据仓库**：将MySQL数据库中的数据加载到数据仓库进行进一步处理。
- **实时数据流**：通过binlog读取MySQL数据库的实时数据变化。

#### 注意事项
- 确保MySQL数据库的`log_bin`设置为`on`，以启用binlog。
- 配置文件中的敏感信息（如用户名和密码）应进行加密存储。
- 确保SeaTunnel和MySQL数据库之间的网络连接正常。

#### 参考资料
- [Apache SeaTunnel官方文档](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/MySQL/)
- [MySQL官方文档](https://dev.mysql.com/doc/refman/8.0/en/)

通过以上说明文档，您可以对Apache SeaTunnel的MySQL源连接器有一个全面的了解，并能够进行基本的配置和使用。