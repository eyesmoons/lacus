根据您提供的链接内容，以下是对 Apache SeaTunnel 中 Redshift 数据源连接器的说明文档整理：

---

### **Apache SeaTunnel Redshift 数据源连接器说明文档**

#### **概述**
Apache SeaTunnel 是一个开源的实时数据集成工具，支持多种数据源和目标系统的连接。Redshift 数据源连接器允许用户通过 JDBC 从 Redshift 数据库中读取数据。

#### **连接器描述**
- **功能**：通过 JDBC 从外部数据源（如 Redshift）读取数据。
- **支持的平台**：
  - Spark
  - Flink
  - SeaTunnel Zeta

#### **配置要求**
- **JDBC 驱动**：
  - 对于 Spark/Flink 引擎，需要将 JDBC 驱动 jar 包放置在 `${SEATUNNEL_HOME}/plugins/` 目录下。
  - 对于 SeaTunnel Zeta 引擎，需要将 JDBC 驱动 jar 包放置在 `${SEATUNNEL_HOME}/lib/` 目录下。

#### **关键特性**
- **批处理**：支持批处理模式。
- **精确一次语义**：支持精确一次（exactly-once）语义，使用 XA transaction 保证。
- **列投影**：支持列投影，可以指定查询哪些列。
- **并行处理**：支持并行处理，提高数据读取效率。
- **用户自定义拆分**：支持用户自定义拆分策略。

#### **数据类型映射**
以下列出了 Redshift 数据类型与 SeaTunnel 数据类型的映射关系：

| Redshift 数据类型 | SeaTunnel 数据类型           |
| ----------------- | ---------------------------- |
| SMALLINT          | SHORT                        |
| INTEGER           | INT                          |
| BIGINT            | INT8                         |
| OID               | LONG                         |
| DECIMAL           | NUMERIC                      |
| REAL              | FLOAT                        |
| DOUBLE_PRECISION  | DOUBLE                       |
| BOOLEAN           | BOOLEAN                      |
| CHAR              | CHARACTER                    |
| NCHAR             | BPCHAR                       |
| VARCHAR           | CHARACTER_VARYING            |
| NVARCHAR          | TEXT                         |
| SUPER             | STRING                       |
| VARBYTE           | BINARY_VARYING               |
| TIME              | TIME_WITH_TIME_ZONE          |
| TIMETZ            | TIMESTAMPTZ                  |
| TIMESTAMP         | TIMESTAMP_WITH_OUT_TIME_ZONE |
| TIMESTAMPTZ       | LOCALDATETIME                |

#### **示例配置**
以下是一些使用 SeaTunnel 从 Redshift 读取数据的示例配置：

##### **简单示例**
```plaintext
env {
  parallelism = 2
  job.mode = "BATCH"
}

source {
  Jdbc {
    url = "jdbc:redshift://localhost:5439/dev"
    driver = "com.amazon.redshift.jdbc.Driver"
    user = "root"
    password = "123456"

    table_path = "public.table2"
    query = "select id, name from public.table2 where id > 100"
  }
}

sink {
  Console {}
}
```

##### **多表读取示例**
```plaintext
env {
  job.mode = "BATCH"
  parallelism = 2
}

source {
  Jdbc {
    url = "jdbc:redshift://localhost:5439/dev"
    driver = "com.amazon.redshift.jdbc.Driver"
    user = "root"
    password = "123456"

    table_list = [
      {
        table_path = "public.table1"
      },
      {
        table_path = "public.table2",
        query = "select id, name from public.table2 where id > 100"
      }
    ]
  }
}

sink {
  Console {}
}
```

#### **变更日志**
- **最新版本**：2.3.11
- **文档更新**：请参考最新版本的文档。

#### **其他资源**
- **社区支持**：Apache SeaTunnel 社区提供丰富的文档和社区支持。
- **GitHub**：[Apache SeaTunnel GitHub 仓库](https://github.com/apache/seatunnel)
- **Issue Tracker**：[Apache SeaTunnel Issue Tracker](https://github.com/apache/seatunnel/issues)
- **Pull Requests**：[Apache SeaTunnel Pull Requests](https://github.com/apache/seatunnel/pulls)

---

希望这份整理的说明文档对您有所帮助！如果您有其他问题或需要进一步的帮助，请随时告诉我。