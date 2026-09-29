根据您提供的链接内容，以下是对 Apache SeaTunnel 中 PostgreSQL CDC 连接器说明文档的整理：

### PostgreSQL CDC 连接器说明文档

#### 简介
PostgreSQL CDC 连接器允许从 PostgreSQL 数据库读取快照数据和增量数据。本文件描述了如何设置 PostgreSQL CDC 连接器，以便对 PostgreSQL 数据库执行 SQL 查询。

#### 支持的数据源信息
- **数据源**：PostgreSQL
- **支持的版本**：不同的依赖版本有不同的驱动类。
- **驱动**：`org.postgresql.Driver`
- **URL**：`jdbc:postgresql://localhost:5432/test`
- **Maven**：提供 Maven 依赖列表，用于下载相应的驱动包。

#### 数据库依赖
- 请下载与 'Maven' 对应的支持列表，并将其复制到 `$SEATUNNEL_HOME/plugins/jdbc/lib/' 工作目录中。
- 如果需要在 PostgreSQL 中操作 GEOMETRY 类型，请将 `postgresql-xxx.jar` 和 `postgis-jdbc-xxx.jar` 添加到 `$SEATUNNEL_HOME/plugins/jdbc/lib/'。

#### 数据类型映射
- 提供了 PostgreSQL 数据类型到 SeaTunnel 数据类型的映射表，方便用户理解数据类型转换。

#### 选项
- **url**：String，必需，JDBC 连接的 URL。
- **driver**：String，必需，用于连接到远程数据源的 JDBC 类名。
- **user**：String，可选，连接实例的用户名。
- **password**：String，可选，连接实例的密码。
- **query**：String，必需，查询语句。
- **connection_check_timeout_sec**：Int，可选，用于验证连接的数据库操作完成的等待时间（秒）。
- **partition_column**：String，可选，用于并行化的分区列名，仅支持数字类型。
- **partition_lower_bound**：BigDecimal，可选，扫描的 `partition_column` 的最小值。
- **partition_upper_bound**：BigDecimal，可选，扫描的 `partition_column` 的最大值。
- **partition_num**：Int，可选，分区数量，仅支持正整数。
- **fetch_size**：Int，可选，用于查询的行抓取大小。
- **properties**：Map，可选，其他连接配置参数。
- **table_path**：String，可选，表的完整路径，可以替代 `query`。
- **table_list**：Array，可选，要读取的表列表，可以替代 `table_path`。
- **where_condition**：String，可选，所有表/查询的通用行过滤条件。
- **split.size**：Int，可选，表的拆分大小（行数）。
- **split.even-distribution.factor.lower-bound**：Double，可选，块键分布因子的下限。
- **split.even-distribution.factor.upper-bound**：Double，可选，块键分布因子的上限。
- **split.sample-sharding.threshold**：Int，可选，触发样本分片策略的估计分片数阈值。
- **split.inverse-sampling.rate**：Int，可选，在样本分片策略中使用的采样率的逆数。

#### 并行读取器
- JDBC 源连接器支持从表中并行读取数据。SeaTunnel 将使用某些规则来拆分表中的数据，这些数据将交给读取器进行读取。读取器的数量由 `parallelism` 选项确定。
- 拆分键规则：
  - 如果 `partition_column` 不为 null，将用于计算拆分。
  - 如果 `partition_column` 为 null，SeaTunnel 将从表中读取模式并获取主键和唯一索引。
- 支持的拆分数据类型：字符串、数字（int, bigint, decimal, ...）、日期。

#### 拆分相关的选项
- **split.size**：每个拆分中有多少行。
- **split.even-distribution.factor.lower-bound**：块键分布因子的下限。
- **split.even-distribution.factor.upper-bound**：块键分布因子的上限。
- **split.sample-sharding.threshold**：触发样本分片策略的估计分片数阈值。
- **split.inverse-sampling.rate**：在样本分片策略中使用的采样率的逆数。

#### 提示
- 如果表无法拆分（例如，表没有主键或唯一索引，并且未设置 `partition_column`），将以单一并发运行。
- 使用 `table_path` 替代 `query` 进行单表读取。如果需要读取多个表，请使用 `table_list`。

#### 任务示例
- **简单示例**：查询测试数据库中 type_bin 为 'table' 的 16 条数据，并以单并行方式查询其所有字段。
- **按 `partition_column` 并行读取**：使用配置的分片字段和分片数据并行读取查询表。
- **按主键或唯一索引并行读取**：配置 `table_path` 将启用自动拆分，可以配置 `split.*` 来调整拆分策略。
- **并行的同时指定边界**：在查询中指定上下边界内的数据更为高效。
- **多表读取**：配置 `table_list` 将启用自动拆分，可以配置 `split.*` 来调整拆分策略。

#### 变更日志
- 提供了 PostgreSQL CDC 连接器的变更日志，方便用户了解不同版本之间的变化。

#### 版本
- 本文档适用于 Apache SeaTunnel 2.3.11 版本。

#### 社区
- 用户可以通过 Apache SeaTunnel 社区获取更多支持和交流。

#### GitHub
- 用户可以通过 GitHub 了解更多关于 Apache SeaTunnel 的信息。

#### Issue Tracker
- 用户可以通过 Issue Tracker 提交问题和建议。

#### Pull Requests
- 用户可以通过 Pull Requests 参与到 Apache SeaTunnel 的开发中。

#### 订阅邮件组
- 用户可以通过订阅邮件组获取最新的信息和通知。

#### 邮件归档
- 提供了邮件归档，方便用户查阅历史邮件。

#### 版权声明
- Apache SeaTunnel 是一个由 Apache 软件基金会（ASF）孵化的项目，受 Apache Incubator 支持。

以上是对 Apache SeaTunnel 中 PostgreSQL CDC 连接器说明文档的整理，希望对您有所帮助。如果您有任何进一步的问题，请随时提问。