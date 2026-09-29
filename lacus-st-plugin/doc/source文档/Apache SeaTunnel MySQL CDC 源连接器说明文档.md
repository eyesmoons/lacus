根据您提供的链接内容，我将为您整理一份关于 Apache SeaTunnel MySQL CDC 连接器的说明文档。以下是文档的主要内容：

---

### Apache SeaTunnel MySQL CDC 连接器说明文档

#### 简介
Apache SeaTunnel 是一个开源的数据集成工具，支持多种数据源的连接和数据同步。MySQL CDC 连接器是 SeaTunnel 的一部分，用于从 MySQL 数据库中读取快照数据和增量数据。

#### 支持的 MySQL 版本
- 5.5
- 5.6
- 5.7
- 8.0
- 8.1
- 8.2
- 8.3
- 8.4

#### 支持的引擎
- Spark
- Flink
- SeaTunnel
- Zeta

#### 功能描述
MySQL CDC 连接器通过 JDBC 读取外部数据源数据，支持 MySQL 数据库的整库数据同步能力。它可以捕获数据库的变更数据，并支持端到端的实时数据同步。

#### 安装与配置
1. **添加依赖项**：
   确保在项目中添加 SeaTunnel 和 MySQL JDBC 驱动的依赖项。

2. **配置连接信息**：
   - 数据库地址
   - 用户名
   - 密码
   - 数据库名与表名

3. **配置 Checkpoint 存储**：
   为了保证数据同步的精确一次语义，需要配置 Checkpoint 存储。

#### 示例配置
```yaml
job.setMode("batch")

source.set("type", "mysql-cdc")
source.set("hostname", "localhost")
source.set("port", "3306")
source.set("username", "user")
source.set("password", "password")
source.set("database-name", "database")
source.set("table-name", "table")

sink.set("type", "print")
sink.set("checkpoint", "true")
```

#### 高级配置
- **并发写入**：支持并发写入数据，提高数据同步效率。
- **精确一次语义**：使用 XA 事务保证数据的精确一次写入。
- **数据切分**：支持多种切分策略，包括均匀切分、不均匀切分和采样切分。

#### 常见问题
1. **如何处理数据时区问题**？
   - 确保源数据库和目标数据库的时区配置一致。

2. **如何处理 `deatetime` 数据同步报错问题**？
   - 检查数据类型配置，确保目标数据库支持 `deatetime` 类型。

#### 参考资料
- [Apache SeaTunnel 官方文档](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/MySQL CDC)
- [MySQL CDC 连接器](https://nightlies.apache.org/flink/flink-cdc-docs-master/zh/docs/connectors/flink-sources/mysql-cdc/)

---

以上是关于 Apache SeaTunnel MySQL CDC 连接器的说明文档，涵盖了其基本功能、配置方法和常见问题解答。希望这份文档能帮助您更好地使用 SeaTunnel 进行数据同步。