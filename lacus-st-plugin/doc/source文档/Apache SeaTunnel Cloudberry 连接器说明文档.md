根据您提供的链接内容，以下是关于 Apache SeaTunnel Cloudberry 连接器的说明文档：

---

### Apache SeaTunnel Cloudberry 连接器说明文档

#### 简介
Cloudberry 是 Apache SeaTunnel 中的一个 JDBC 源连接器，用于通过 JDBC 读取外部数据源的数据。Cloudberry 目前未提供原生 JDBC 驱动，需要使用 PostgreSQL 的驱动程序和实现。

#### 支持的引擎
- Spark
- Flink
- SeaTunnel Zeta

#### 使用依赖关系
- **适用于 Spark/Flink 引擎**：确保 JDBC 驱动程序 jar 包已放置在目录 `${SEATUNNEL_HOME}/plugins/` 中。
- **适用于 SeaTunnel Zeta 引擎**：确保 JDBC 驱动程序 jar 包已放置在目录 `${SEATUNNEL_HOME}/lib/` 中。

#### 关键特性
- 批处理
- 流处理
- 精确一次
- 列映射
- 并行度
- 支持用户自定义拆分
- 支持查询 SQL，可以实现映射效果

#### 描述
Cloudberry 通过 JDBC 读取外部数据源的数据。由于 Cloudberry 暂未提供原生 JDBC 驱动，因此需要使用 PostgreSQL 的驱动程序和实现。

#### 支持的数据源信息
| 数据源     | 支持的版本               | 驱动程序                | URL                                     | Maven |
| ---------- | ------------------------ | ----------------------- | --------------------------------------- | ----- |
| Cloudberry | 使用 PostgreSQL 驱动实现 | `org.postgresql.Driver` | `jdbc:postgresql://localhost:5432/test` | 下载  |

#### 数据库相关性
请下载 PostgreSQL 驱动程序的 jar 包，并将其复制到 `${SEATUNNEL_HOME}/plugins/jdbc/lib/` 工作目录下。例如：
```bash
cp postgresql-xxx.jar $SEATUNNEL_HOME/plugins/jdbc/lib/
```

#### 数据类型映射
Cloudberry 使用 PostgreSQL 的数据类型实现。有关数据类型的兼容性和映射关系，请参考 PostgreSQL 文档。

#### 配置项
Cloudberry 连接器使用与 PostgreSQL 相同的配置项。有关详细的配置选项，请参考 PostgreSQL 连接器文档。关键配置项包括：
- `url` (必需): JDBC 连接 URL。
- `driver` (必需): 驱动程序类名 (`org.postgresql.Driver`)。
- `user/password`: 认证凭据。
- `query or table_path`: 要读取的数据。
- 用于并行读取的分区选项。

#### 并行读取
Cloudberry 支持与 PostgreSQL 连接器相同的并行读取规则。有关切片策略和并行读取选项的详细信息，请参考 PostgreSQL 连接器文档。

#### 任务示例
##### 简单示例
```plaintext
env {
  parallelism = 4
  job.mode = "BATCH"
}

source {
  Jdbc {
    url = "jdbc:postgresql://localhost:5432/cloudberrydb"
    driver = "org.postgresql.Driver"
    user = "dbadmin"
    password = "password"
    query = "select * from mytable limit 100"
  }
}

sink {
  Console {}
}
```

##### 使用 `table_path` 进行并行读取
```plaintext
env {
  parallelism = 4
  job.mode = "BATCH"
}

source {
  Jdbc {
    url = "jdbc:postgresql://localhost:5432/cloudberrydb"
    driver = "org.postgresql.Driver"
    user = "dbadmin"
    password = "password"
    table_path = "public.mytable"
    split.size = 10000
  }
}

sink {
  Console {}
}
```

##### 读取多张表
```plaintext
env {
  job.mode = "BATCH"
  parallelism = 4
}

source {
  Jdbc {
    url = "jdbc:postgresql://localhost:5432/cloudberrydb"
    driver = "org.postgresql.Driver"
    user = "dbadmin"
    password = "password"
    "table_list" = [
      {
        "table_path" = "public.table1"
      },
      {
        "table_path" = "public.table2"
      }
    ]
    split.size = 10000
  }
}

sink {
  Console {}
}
```

#### 变更日志
请参考 SeaTunnel 的官方文档获取最新的变更日志。

#### 更多信息
有关更详细的示例和配置，请参阅 PostgreSQL 连接器文档。

---

希望这份说明文档对您有所帮助！如果您有任何其他问题或需要进一步的信息，请随时告诉我。