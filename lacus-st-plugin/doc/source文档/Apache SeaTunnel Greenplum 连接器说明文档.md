Apache SeaTunnel Greenplum 连接器的说明文档整理：

---

### Apache SeaTunnel Greenplum 连接器说明文档

#### 简介
Apache SeaTunnel Greenplum 连接器允许用户通过 JDBC 连接器读取 Greenplum 数据库中的数据。该连接器支持批处理和流式处理，并具备精确一次（exactly-once）数据处理能力。

#### 关键特性
- **批处理和流式处理**：支持批处理和流式数据处理模式。
- **精确一次（exactly-once）**：确保数据处理的一致性和完整性。
- **列投影**：支持通过查询 SQL 实现列投影，选择性地读取所需列。
- **并行处理**：支持用户自定义数据分片，以实现并行处理。

#### JDBC 驱动
使用 Greenplum 连接器时，需要确保提供的 JDBC 驱动程序支持 Greenplum 数据库。推荐的 JDBC 驱动程序包括：
- `org.postgresql.Driver`
- `com.pivotal.jdbc.GreenplumDriver`

**注意**：为了遵守许可证合规性要求，如果使用 `GreenplumDriver`，必须自行提供 Greenplum JDBC 驱动程序。例如，将 `greenplum-xxx.jar` 复制到 `$SEATUNNEL_HOME/lib` 目录下（适用于 Standalone 模式）。

#### 选项
连接器提供了多种配置选项，具体请参考 SeaTunnel 的源连接器通用参数文档。

#### 版本信息
- **版本**：2.3.11

#### 社区与支持
- **社区**：Apache SeaTunnel 是 Apache 软件基金会（ASF）孵化项目，由 Apache Incubator 赞助。
- **GitHub**：[Apache SeaTunnel GitHub 仓库](https://github.com/apache/seatunnel)
- **Issue Tracker**：[Apache SeaTunnel Issue Tracker](https://issues.apache.org/jira/browse/SEATUNNEL)
- **Pull Requests**：[Apache SeaTunnel Pull Requests](https://github.com/apache/seatunnel/pulls)
- **订阅邮件组**：[如何订阅](https://seatunnel.apache.org/subscribe.html)

#### 版权信息
- **版权**：© 2021-2022 The Apache Software Foundation.
- **商标**：Apache SeaTunnel, SeaTunnel, 和其羽毛标志均为 The Apache Software Foundation 的商标。

#### 相关链接
- [Greenplum 连接器文档](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/Greenplum/)
- [SeaTunnel 项目介绍](https://seatunnel.apache.org/docs/)

---

希望这份整理的说明文档对您有所帮助。如果您有其他问题或需要进一步的信息，请随时告知。