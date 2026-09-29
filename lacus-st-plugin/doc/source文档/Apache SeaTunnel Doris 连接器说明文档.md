Apache SeaTunnel Doris 连接器说明文档

一、概述

Apache SeaTunnel Doris 连接器是一个用于 Apache Doris 的源连接器，支持批处理和流处理，能够实现精确一次的数据传输。该连接器兼容 Spark 和 Flink 引擎，并支持多表读和用户自定义分片。

二、依赖

1. 对于 Spark/Flink，需要下载 jdbc driver jar package 并添加到目录 ${SEATUNNEL_HOME}/plugins/。
2. 对于 SeaTunnel Zeta，需要下载 jdbc driver jar package 并添加到目录 ${SEATUNNEL_HOME}/lib/。

三、支持的数据源信息

数据源：Doris
支持版本：仅支持 Doris2.0及以上版本

四、数据类型映射

| Doris 数据类型 | SeaTunnel 数据类型                                           |
| -------------- | ------------------------------------------------------------ |
| INT            | INT                                                          |
| TINYINT        | TINYINT                                                      |
| SMALLINT       | SMALLINT                                                     |
| BIGINT         | BIGINT                                                       |
| LARGEINT       | STRING                                                       |
| BOOLEAN        | BOOLEAN                                                      |
| DECIMAL        | DECIMAL((Get the designated column's specified column size)+1, <br> (Gets the designated column's number of digits to right of the decimal point.))) |
| FLOAT          | FLOAT                                                        |
| DOUBLE         | DOUBLE                                                       |
| CHAR           |                                                              |
| VARCHAR        |                                                              |
| STRING         |                                                              |
| TEXT           | STRING                                                       |
| DATE           | DATE                                                         |
| DATETIME       | DATETIME                                                     |
| DATETIME(p)    | TIMESTAMP                                                    |
| ARRAY          | ARRAY                                                        |

五、源选项

1. 基础配置：
   - fenodes：string，必填，FE 地址，格式：\“fe_host:fe_http_port\”
   - username：string，必填，用户名
   - password：string，必填，密码
   - doris.request.retries：int，非必填，默认为 3，请求 Doris FE 的重试次数
   - doris.request.read.timeout.ms：int，非必填，默认为 30000，Doris 读取超时时间，单位毫秒
   - doris.request.connect.timeout.ms：int，非必填，默认为 30000，Doris 连接超时时间，单位毫秒
   - query-port：string，非必填，默认为 9030，Doris 查询端口
   - doris.request.query.timeout.s：int，非必填，默认为 3600，Doris 扫描数据的超时时间，单位秒
   - table_list：string，非必填，表清单

2. 表清单配置：
   - database：string，必填，数据库
   - table：string，必填，表名
   - doris.read.field：string，非必填，选择要读取的 Doris 表字段
   - doris.filter.query：string，非必填，数据过滤。格式：“字段 = 值”，例如：doris.filter.query = “F_ID > 2”
   - doris.batch.size：int，非必填，默认为 1024，每次能够从 BE 中读取到的最大行数
   - doris.exec.mem.limit：long，非必填，默认为 2147483648，单个 be 扫描请求可以使用的最大内存。默认内存为 2G（2147483648）

六、注意

不建议随意修改高级参数。

七、例子

1. 单表：
```plaintext
env {
  parallelism = 2
  job.mode = "BATCH"
}
source {
  Doris {
    fenodes = "doris_e2e:8030"
    username = root
    password = ""
    database = "e2e_source"
    table = "doris_e2e_table"
  }
}
transform {
  # If you would like to get more information about how to configure seatunnel and see full list of transform plugins,
  # please go to https://seatunnel.apache.org/docs/transform/sql
}
sink {
  Console {}
}
```

2. 使用 doris.read.field 参数来选择需要读取的 Doris 表字段：
```plaintext
env {
  parallelism = 2
  job.mode = "BATCH"
}
source {
  Doris {
    fenodes = "doris_e2e:8030"
    username = root
    password = ""
    database = "e2e_source"
    table = "doris_e2e_table"
    doris.read.field = "F_ID,F_INT,F_BIGINT,F_TINYINT,F_SMALLINT"
  }
}
transform {
  # If you would like to get more information about how to configure seatunnel and see full list of transform plugins,
  # please go to https://seatunnel.apache.org/docs/transform/sql
}
sink {
  Console {}
}
```

3. 使用 doris.filter.query 来过滤数据，参数值将作为过滤条件直接传递到 Doris：
```plaintext
env {
  parallelism = 2
  job.mode = "BATCH"
}
source {
  Doris {
    fenodes = "doris_e2e:8030"
    username = root
    password = ""
    database = "e2e_source"
    table = "doris_e2e_table"
    doris.filter.query = "F_ID > 2"
  }
}
transform {
  # If you would like to get more information about how to configure seatunnel and see full list of transform plugins,
  # please go to https://seatunnel.apache.org/docs/transform/sql
}
sink {
  Console {}
}
```

4. 多表：
```plaintext
env {
  parallelism = 1
  job.mode = "BATCH"
}
source {
  Doris {
    fenodes = "xxxx:8030"
    username = root
    password = ""
    table_list = [
      {
        database = "st_source_0"
        table = "doris_table_0"
        doris.read.field = "F_ID,F_INT,F_BIGINT,F_TINYINT"
        doris.filter.query = "F_ID >= 50"
      },
      {
        database = "st_source_1"
        table = "doris_table_1"
      }
    ]
  }
}
transform {}
sink {
  Doris {
    fenodes = "xxxx:8030"
    schema_save_mode = "RECREATE_SCHEMA"
    username = root
    password = ""
    database = "st_sink"
    table = "${table_name}"
    sink.enable-2pc = "true"
    sink.label-prefix = "test_json"
    doris.config = {
      format="json"
      read_json_by_line="true"
    }
  }
}
```

以上是 Apache SeaTunnel Doris 连接器的详细说明文档，希望对您有所帮助。