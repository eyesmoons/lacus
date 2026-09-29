根据您提供的链接内容，以下是关于 Apache SeaTunnel 2.3.11 版本中 SQL Server CDC 连接器的说明文档：

### Apache SeaTunnel 2.3.11 SQL Server CDC 连接器文档

#### 简介
Apache SeaTunnel 是一个开源的数据集成工具，支持多种数据源的连接和数据转换。SQL Server CDC 连接器是 SeaTunnel 的一部分，用于实现 SQL Server 数据库的变更数据捕获（CDC），从而实现实时数据同步。

#### 安装与配置
1. **安装 SeaTunnel 引擎**：
   - 首先，确保您已经安装了 Apache SeaTunnel 引擎。
   - 下载 SeaTunnel 2.3.11 版本，并解压到指定目录。

2. **安装 SQL Server CDC 连接器**：
   - 在 `${SEATUNNEL_HOME}/config/plugin_config` 文件中添加连接器名称。
   - 执行以下命令安装连接器：
     ```bash
     mvn install:install-file -Dfile=path/to/connector-jar -DgroupId=com.seatunnel -DartifactId=connector-jdbc -Dversion=2.3.11 -Dpackaging=jar
     ```

3. **配置连接器**：
   - 在您的 SeaTunnel 配置文件中，配置 SQL Server CDC 连接器的相关参数。以下是一个示例配置：
     ```json
     {
       "source": {
         "type": "sqlserver-cdc",
         "name": "sqlserver-cdc-source",
         "server": "your_sql_server_host",
         "port": 1433,
         "database": "your_database_name",
         "username": "your_username",
         "password": "your_password",
         "table": ["table1", "table2"],
         "topic": "your_topic_name"
       }
     }
     ```

#### 参数说明
- **server**：SQL Server 的主机名或 IP 地址。
- **port**：SQL Server 的端口号，默认为 1433。
- **database**：要连接的数据库名称。
- **username**：连接 SQL Server 的用户名。
- **password**：连接 SQL Server 的密码。
- **table**：要捕获变更的表名称，可以是多个表，用逗号分隔。
- **topic**：CDC 主题名称，用于标识数据源。

#### 使用示例
以下是一个简单的 SeaTunnel 配置文件示例，展示如何使用 SQL Server CDC 连接器：

```json
{
  "job": {
    "name": "sqlserver-cdc-job",
    "type": "stream"
  },
  "source": {
    "type": "sqlserver-cdc",
    "name": "sqlserver-cdc-source",
    "server": "your_sql_server_host",
    "port": 1433,
    "database": "your_database_name",
    "username": "your_username",
    "password": "your_password",
    "table": ["table1", "table2"],
    "topic": "your_topic_name"
  },
  "transform": [
    // 在这里添加转换逻辑
  ],
  "sink": {
    // 在这里配置数据接收端
  }
}
```

#### 常见问题
1. **如何开启 SQL Server 的 CDC 功能**？
   - 在 SQL Server 中，需要启用 MS-CDC 功能。可以通过以下步骤开启：
     ```sql
     EXEC master.dbo.xp_instance_regread N'HKEY_LOCAL_MACHINE', N'Software\Microsoft\SQL Server\MSDTC', N'InboundSession' 
     ```
   - 确保 SQL Server CDC Agent 已经开启。

2. **SeaTunnel 支持哪些数据源的 CDC**？
   - SeaTunnel 目前支持多种数据源的 CDC，包括 MongoDB CDC、MySQL CDC、Opengauss CDC、Oracle CDC、PostgreSQL CDC、Sql Server CDC、TiDB CDC 等。

#### 参考资料
- [Apache SeaTunnel 官方文档](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/)
- [SQL Server CDC 机制全解](https://www.cnblogs.com/seatunnel/p/18932412)

通过以上说明文档，您应该能够了解如何在 Apache SeaTunnel 2.3.11 版本中使用 SQL Server CDC 连接器进行实时数据同步。如果有更多问题，请参考官方文档或社区支持。