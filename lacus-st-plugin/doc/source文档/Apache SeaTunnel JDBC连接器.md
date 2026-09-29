根据您提供的链接，我将整理出Apache SeaTunnel JDBC连接器的相关说明文档。以下是文档的主要内容：

### Apache SeaTunnel JDBC连接器

#### 简介
Apache SeaTunnel是一个开源的分布式数据集成工具，支持多种数据源的连接和数据流的处理。JDBC连接器是SeaTunnel中的一种数据源连接器，它允许SeaTunnel通过JDBC API连接到各种关系型数据库。

#### 功能
- **并行读取数据**：JDBC Source connector支持从数据库表中并行读取数据。
- **数据分割**：SeaTunnel会使用特定的规则来分割表中的数据，以便并行处理。
- **支持多种数据库**：JDBC连接器支持多种关系型数据库，如MySQL、PostgreSQL、Oracle等。

#### 使用方法
1. **配置连接器**：
   - 在SeaTunnel的任务配置中，指定JDBC连接器的相关参数，如数据库URL、用户名、密码等。
   - 示例配置：
     ```json
     {
       "name": "jdbc-source",
       "type": "jdbc",
       "version": "2.3.11",
       "configuration": {
         "url": "jdbc:mysql://localhost:3306/database_name",
         "username": "user_name",
         "password": "password",
         "table": "table_name",
         "split-mode": "hash",
         "split-key": "id"
       }
     }
     ```

2. **使用split-mode和split-key**：
   - `split-mode`：指定数据分割模式，如`hash`、`range`等。
   - `split-key`：指定用于数据分割的列名。

3. **使用database和table-name生成SQL**：
   - 可以使用`database`和`table-name`参数自动生成SQL语句，并接收上游输入的数据写入数据库。
   - 注意：此选项与`query`选项是互斥的，且此选项具有更高的优先级。

#### 示例
以下是一个简单的JDBC Source连接器配置示例：
```json
{
  "name": "jdbc-source",
  "type": "jdbc",
  "version": "2.3.11",
  "configuration": {
    "url": "jdbc:mysql://localhost:3306/database_name",
    "username": "user_name",
    "password": "password",
    "table": "table_name",
    "split-mode": "hash",
    "split-key": "id"
  }
}
```

#### 注意事项
- 确保数据库驱动程序已正确添加到SeaTunnel的类路径中。
- 根据实际需求调整split-mode和split-key的配置。
- 注意数据库连接的安全性和性能优化。

#### 参考资料
- [Apache SeaTunnel JDBC连接器文档](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/JDBC)

通过以上内容，您可以对Apache SeaTunnel JDBC连接器有一个基本的了解和使用指导。如需更详细的信息，请参考官方文档。