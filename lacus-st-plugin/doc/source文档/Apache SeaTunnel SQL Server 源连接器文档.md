根据您提供的链接内容，以下是对 Apache SeaTunnel SQL Server 连接器的说明文档整理：

---

### Apache SeaTunnel SQL Server 连接器文档

#### 简介
Apache SeaTunnel 是一个开源的数据集成工具，支持多种数据源的连接和数据处理。SQL Server 连接器是 SeaTunnel 的一部分，用于连接 SQL Server 数据库，实现数据的读取和写入。

#### 安装与配置
1. **下载驱动**：
   - 首先，您需要下载 SQL Server 的 JDBC 驱动。例如，SQL Server 数据源驱动类名为 `com.microsoft.sqlserver.jdbc.SQLServerDriver`。
   - 将下载的驱动 JAR 文件复制到 SeaTunnel 安装目录下的 `plugins/jdbc/lib` 目录中。

2. **配置数据源**：
   - 在 SeaTunnel 的配置文件中，配置 SQL Server 数据源。示例如下：
     ```properties
     plugin.class=org.apache.seatunnel.core.source.jdbc.JdbcSource
     plugin.config=
       username=your_username
       password=your_password
       url=jdbc:sqlserver://your_server:1433;databaseName=your_database
       driver=com.microsoft.sqlserver.jdbc.SQLServerDriver
       query=SELECT * FROM your_table
     ```

#### 数据类型映射
SeaTunnel 支持将 SQL Server 数据类型映射到相应的 SeaTunnel 数据类型。以下是一些常见的数据类型映射示例：

- **SQL Server 数据类型** | **SeaTunnel 数据类型**
- --- | ---
- BIT | BOOLEAN
- TINYINT | BYTE
- SMALLINT | SHORT
- INT | INT
- BIGINT | LONG
- FLOAT | FLOAT
- DOUBLE | DOUBLE
- DECIMAL | DECIMAL
- NUMERIC | NUMERIC
- DATE | DATE
- DATETIME | TIMESTAMP
- NCHAR | STRING
- NVARCHAR | STRING
- TEXT | STRING
- CLOB | STRING

#### 使用示例
以下是一个简单的 SeaTunnel 任务配置示例，用于从 SQL Server 读取数据：

```json
{
  "name": "sql-server-source",
  "plugin": {
    "class": "org.apache.seatunnel.core.source.jdbc.JdbcSource",
    "config": {
      "username": "your_username",
      "password": "your_password",
      "url": "jdbc:sqlserver://your_server:1433;databaseName=your_database",
      "driver": "com.microsoft.sqlserver.jdbc.SQLServerDriver",
      "query": "SELECT * FROM your_table"
    }
  }
}
```

#### 高级配置
- **分区列**：如果您希望对数据进行分区处理，可以在配置中指定 `partition_column`。
- **事务性**：SeaTunnel 支持事务性写入，确保数据的完整性和一致性。

#### 常见问题
- **驱动版本**：确保使用与 SQL Server 版本兼容的 JDBC 驱动。
- **连接池**：如果使用连接池，需要在配置中指定连接池的相关参数。

#### 参考资料
- [Apache SeaTunnel SQL Server 连接器文档](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/SqlServer/)
- [SeaTunnel 官方文档](https://seatunnel.apache.org/docs/)

---

以上文档整理了 Apache SeaTunnel SQL Server 连接器的安装、配置、数据类型映射和使用示例等内容，希望能帮助您更好地使用 SeaTunnel 连接和操作 SQL Server 数据库。