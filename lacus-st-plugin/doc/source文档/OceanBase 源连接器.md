根据您提供的链接内容，以下是对 Apache SeaTunnel 中 JDBC OceanBase Source Connector 的说明文档整理：

### 连接器概述
- **名称**：JDBC OceanBase Source Connector
- **版本**：2.3.11
- **支持引擎**：Spark、Flink、SeaTunnel Zeta
- **主要特性**：
  - 支持批处理和流处理
  - 精确一次（Exactly-once）语义
  - 列投影
  - 并行处理
  - 支持用户自定义分区

### 描述
通过 JDBC 从外部数据源读取数据。支持所有 OceanBase 服务器版本。

### 支持的数据源信息
- **数据源**：OceanBase
- **支持版本**：所有 OceanBase 服务器版本
- **驱动**：com.oceanbase.jdbc.Driver
- **URL 示例**：jdbc:oceanbase://localhost:2883/test
- **Maven 依赖**：需要下载支持列表并放置在 `$SEATUNNEL_HOME/plugins/jdbc/lib/` 目录下

### 数据库依赖
请下载与 'Maven' 相对应的支持列表，并将其复制到 `$SEATUNNEL_HOME/plugins/jdbc/lib/` 工作目录下。例如：
```bash
cp oceanbase-client-xxx.jar $SEATUNNEL_HOME/plugins/jdbc/lib/
```

### 数据类型映射
- **MySQL 模式**：
  - 映射关系详细列表（略）
- **Oracle 模式**：
  - 映射关系详细列表（略）

### 源选项
- **名称** | **类型** | **是否必需** | **默认值** | **描述**
  - url | String | 是 | - | JDBC 连接的 URL
  - driver | String | 是 | - | 连接远程数据源的 jdbc 类名，应为 com.oceanbase.jdbc.Driver
  - user | String | 否 | - | 连接实例用户名
  - password | String | 否 | - | 连接实例密码
  - compatible_mode | String | 是 | - | OceanBase 的兼容模式，可为 'mysql' 或 'oracle'
  - query | String | 是 | - | 查询语句
  - connection_check_timeout_sec | Int | 否 | 30 | 用于验证连接完成的数据库操作等待时间（秒）
  - partition_column | String | 否 | - | 用于并行性的分区列名，仅支持数值类型列和字符串类型列
  - partition_lower_bound | BigDecimal | 否 | - | 扫描的分区列最小值，若未设置，SeaTunnel 将查询数据库获取最小值
  - partition_upper_bound | BigDecimal | 否 | - | 扫描的分区列最大值，若未设置，SeaTunnel 将查询数据库获取最大值
  - partition_num | Int | 否 | job parallelism | 分区计数，仅支持正整数，默认值为作业并行度
  - fetch_size | Int | 否 | 0 | 对于返回大量对象的查询，可以配置查询中使用的行获取大小，以减少满足选择标准所需的数据库访问次数。零表示使用 jdbc 默认值
  - properties | Map | 否 | - | 额外的连接配置参数，当 properties 和 URL 具有相同参数时，优先级由驱动具体实现决定
  - common-options | No | - | - | 源插件公共参数，详细请参考源公共选项

### 提示
- 如果未设置 partition_column，将运行在单并发中；如果设置了 partition_column，将根据任务的并发性并行执行。

### 任务示例
- **简单示例**：
  ```json
  {
    "env": {
      "parallelism": 2,
      "job.mode": "BATCH"
    },
    "source": {
      "Jdbc": {
        "driver": "com.oceanbase.jdbc.Driver",
        "url": "jdbc:oceanbase://localhost:2883/test?useUnicode=true&characterEncoding=UTF-8&rewriteBatchedStatements=true",
        "user": "root",
        "password": "",
        "compatible_mode": "mysql",
        "query": "select * from source"
      }
    },
    "transform": {
      // 更多信息请参考 https://seatunnel.apache.org/docs/transform/sql
    },
    "sink": {
      "Console": {}
    }
  }
  ```
- **并行示例**：
  ```json
  {
    "env": {
      "parallelism": 10,
      "job.mode": "BATCH"
    },
    "source": {
      "Jdbc": {
        "driver": "com.oceanbase.jdbc.Driver",
        "url": "jdbc:oceanbase://localhost:2883/test?useUnicode=true&characterEncoding=UTF-8&rewriteBatchedStatements=true",
        "user": "root",
        "password": "",
        "compatible_mode": "mysql",
        "query": "select * from source",
        "partition_column": "id",
        "partition_num": 10
      }
    },
    "sink": {
      "Console": {}
    }
  }
  ```
- **并行边界示例**：
  ```json
  {
    "source": {
      "Jdbc": {
        "driver": "com.oceanbase.jdbc.Driver",
        "url": "jdbc:oceanbase://localhost:2883/test?useUnicode=true&characterEncoding=UTF-8&rewriteBatchedStatements=true",
        "user": "root",
        "password": "",
        "compatible_mode": "mysql",
        "query": "select * from source",
        "partition_column": "id",
        "partition_num": 10,
        "partition_lower_bound": 1,
        "partition_upper_bound": 500
      }
    }
  }
  ```

### Changelog
- 版本变更记录（略）

### 版权信息
- 版权 © 2021-2022 The Apache Software Foundation. Apache SeaTunnel, SeaTunnel, 和其羽毛标志是 The Apache Software Foundation 的商标。

以上是 JDBC OceanBase Source Connector 的详细说明文档，希望对您有所帮助。