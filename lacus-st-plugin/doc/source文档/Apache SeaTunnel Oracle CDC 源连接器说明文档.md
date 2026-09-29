根据您提供的链接内容，以下是对 Apache SeaTunnel Oracle CDC 连接器文档的整理说明：

### Apache SeaTunnel Oracle CDC 连接器文档

#### 简介
Apache SeaTunnel 是一个开源的数据集成工具，支持多种数据源的实时数据同步。Oracle CDC 连接器是 SeaTunnel 的一部分，用于从 Oracle 数据库中读取增量数据。

#### 文档链接
- [Apache SeaTunnel Oracle CDC 连接器文档](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/Oracle-CDC/)

#### 文档内容概述
1. **概述**
   - Oracle CDC 连接器允许从 Oracle 数据库中读取快照数据和增量数据。
   - 该文档描述了如何设置 Oracle CDC 连接器以在 Oracle 数据库中运行 SQL 查询。

2. **使用步骤**
   - **安装连接器插件**：
     - 确保已安装 SeaTunnel，并配置好环境。
     - 下载所需的连接器插件，并将其放置在正确的目录下。
   - **配置连接器**：
     - 在配置文件中指定连接器参数，如数据库 URL、用户名、密码等。
   - **运行任务**：
     - 使用 SeaTunnel 的任务配置文件定义数据同步任务。
     - 运行任务以开始数据同步。

3. **配置参数**
   - **基本连接参数**：
     - `url`：JDBC 连接 URL。
     - `driver`：JDBC 驱动类名。
     - `user`：数据库用户名。
     - `password`：数据库密码。
   - **查询参数**：
     - `query`：要执行的 SQL 查询语句。
   - **高级参数**：
     - `split.size`：每个分片的大小（行数）。
     - `split.even-distribution.factor.lower-bound`：分片键分布的下限。
     - `split.even-distribution.factor.upper-bound`：分片键分布的上限。
     - `split.sample-sharding.threshold`：采样分片的阈值。
     - `split.inverse-sampling.rate`：采样率的倒数。

4. **示例**
   - **简单示例**：
     ```plaintext
     env {
         parallelism = 4
         job.mode = "BATCH"
     }
     source {
         Jdbc {
             url = "jdbc:oracle:thin:@datasource01:1523:xe"
             driver = "oracle.jdbc.OracleDriver"
             user = "root"
             password = "123456"
             query = "SELECT * FROM TEST_TABLE"
         }
     }
     transform {
         # 可以添加转换逻辑
     }
     sink {
         Console {}
     }
     ```
   - **并行读取示例**：
     ```plaintext
     env {
         parallelism = 4
         job.mode = "BATCH"
     }
     source {
         Jdbc {
             url = "jdbc:oracle:thin:@datasource01:1523:xe"
             driver = "oracle.jdbc.OracleDriver"
             connection_check_timeout_sec = 100
             user = "root"
             password = "123456"
             query = "SELECT * FROM TEST_TABLE"
             partition_column = "ID"
             partition_num = 10
         }
     }
     sink {
         Console {}
     }
     ```

5. **多表读取示例**：
   ```plaintext
   env {
       job.mode = "BATCH"
       parallelism = 4
   }
   source {
       Jdbc {
           url = "jdbc:oracle:thin:@datasource01:1523:xe"
           driver = "oracle.jdbc.OracleDriver"
           connection_check_timeout_sec = 100
           user = "root"
           password = "123456"
           table_list = [
               { table_path = "XE.TEST.USER_INFO" },
               { table_path = "XE.TEST.YOURTABLENAME" }
           ]
           split.size = 10000
       }
   }
   sink {
       Console {}
   }
   ```

#### 注意事项
- 确保已下载并配置好 Oracle JDBC 驱动。
- 根据实际需求调整配置参数，以优化性能和同步效果。
- 参考 SeaTunnel 的官方文档获取更多详细信息和高级配置选项。

#### 总结
Apache SeaTunnel Oracle CDC 连接器为从 Oracle 数据库中实时同步数据提供了强大的支持。通过合理的配置和使用，可以高效地实现数据的同步和集成。