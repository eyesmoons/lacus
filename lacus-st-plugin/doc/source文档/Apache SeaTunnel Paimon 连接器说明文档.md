根据您提供的链接内容，以下是对 Apache SeaTunnel Paimon 连接器的说明文档整理：

---

### Apache SeaTunnel Paimon 连接器说明文档

#### 简介
Paimon 源连接器用于从 Apache Paimon 读取数据，支持批处理和流处理，确保精确一次的数据处理，并支持列投影和并行度。此外，它还支持用户自定义分片，适用于多种数据处理场景。

#### 主要功能
- **批处理**：支持大规模数据的批量读取。
- **流处理**：支持实时数据的流式读取。
- **精确一次**：确保数据处理的精确性和一致性。
- **列投影**：支持只读取需要的列，提高数据处理效率。
- **并行度**：支持并行处理，提高数据处理速度。
- **用户自定义分片**：允许用户根据需求自定义数据分片规则。

#### 配置选项
以下是 Paimon 连接器的配置选项及其说明：

| 名称                    | 类型   | 是否必须 | 默认值     |
| ----------------------- | ------ | -------- | ---------- |
| warehouse               | String | 是       | -          |
| catalog_type            | String | 否       | filesystem |
| catalog_uri             | String | 否       | -          |
| database                | String | 是       | -          |
| table                   | String | 是       | -          |
| hdfs_site_path          | String | 否       | -          |
| query                   | String | 否       | -          |
| paimon.hadoop.conf      | Map    | 否       | -          |
| paimon.hadoop.conf-path | String | 否       | -          |

##### 详细说明
- **warehouse**：Paimon warehouse 路径。
- **catalog_type**：Paimon Catalog 类型，支持 filesystem 和 hive。
- **catalog_uri**：Paimon 的 catalog uri，仅当 catalog_type 为 hive 时需要。
- **database**：需要访问的数据库。
- **table**：需要访问的表。
- **hdfs_site_path**：hdfs-site.xml 文件地址。
- **query**：读取表格的筛选条件，例如：`select * from st_test where id > 100`。如果未指定，则将读取所有记录。
- **paimon.hadoop.conf**：hadoop conf 属性。
- **paimon.hadoop.conf-path**：指定 'core-site.xml', 'hdfs-site.xml', 'hive-site.xml' 文件加载路径。

##### query 语法
- 支持的 where 条件：`<, <=, >, >=, =, !=, or, and, is null, is not null, between...and`。
- 不支持的条件：`Having, Group By 和 Order By`（未来版本将会支持 projection 和 limit）。
- 字符串和布尔值字段值必须使用单引号，例如：`name='abc'` 或 `tag='true'`。
- 支持的字段数据类型：`string, boolean, tinyint, smallint, int, bigint, float, double, date, timestamp, time`。

#### 文件系统支持
Paimon 连接器支持向多个文件系统写入数据，目前支持的文件系统有 hdfs 和 s3。如果使用 s3 文件系统，可以在 `paimon.hadoop.conf` 中配置以下属性：
- `fs.s3a.access-key`
- `fs.s3a.secret-key`
- `fs.s3a.endpoint`
- `fs.s3a.path.style.access`
- `fs.s3a.aws.credentials.provider`

数仓地址应该以 `s3a://` 开头。

#### 示例
##### 简单示例
```plaintext
source {
  Paimon {
    warehouse = "/tmp/paimon"
    database = "default"
    table = "st_test"
  }
}
```

##### Filter 示例
```plaintext
source {
  Paimon {
    warehouse = "/tmp/paimon"
    database = "full_type"
    table = "st_test"
    query = "select c_boolean, c_tinyint from st_test where c_boolean='true' and c_tinyint > 116 and c_smallint = 15987 or c_decimal='2924137191386439303744.39292213'"
  }
}
```

##### S3 示例
```plaintext
env {
  execution.parallelism = 1
  job.mode = "BATCH"
}

source {
  Paimon {
    warehouse = "s3a://test/"
    database = "seatunnel_namespace11"
    table = "st_test"
    paimon.hadoop.conf = {
      fs.s3a.access-key = "G52pnxg67819khOZ9ezX"
      fs.s3a.secret-key = "SHJuAQqHsLrgZWikvMa3lJf5T0NfM5LMFliJh9HF"
      fs.s3a.endpoint = "http://minio4:9000"
      fs.s3a.path.style.access = true
      fs.s3a.aws.credentials.provider = "org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider"
    }
  }
}
```

##### Hadoop 配置示例
```plaintext
source {
  Paimon {
    catalog_name = "seatunnel_test"
    warehouse = "hdfs:///tmp/paimon"
    database = "seatunnel_namespace1"
    table = "st_test"
    query = "select * from st_test where pk_id is not null and pk_id < 3"
    paimon.hadoop.conf = {
      hadoop_user_name = "hdfs"
      fs.defaultFS = "hdfs://nameservice1"
      dfs.nameservices = "nameservice1"
      dfs.ha.namenodes.nameservice1 = "nn1,nn2"
      dfs.namenode.rpc-address.nameservice1.nn1 = "hadoop03:8020"
      dfs.namenode.rpc-address.nameservice1.nn2 = "hadoop04:8020"
      dfs.client.failover.proxy.provider.nameservice1 = "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider"
      dfs.client.use.datanode.hostname = "true"
    }
  }
}
```

##### Hive catalog 示例
```plaintext
source {
  Paimon {
    catalog_name = "seatunnel_test"
    catalog_type = "hive"
    catalog_uri = "thrift://hadoop04:9083"
    warehouse = "hdfs:///tmp/seatunnel"
    database = "seatunnel_test"
    table = "st_test3"
    paimon.hadoop.conf = {
      fs.defaultFS = "hdfs://nameservice1"
      dfs.nameservices = "nameservice1"
      dfs.ha.namenodes.nameservice1 = "nn1,nn2"
      dfs.namenode.rpc-address.nameservice1.nn1 = "hadoop03:8020"
      dfs.namenode.rpc-address.nameservice1.nn2 = "hadoop04:8020"
      dfs.client.failover.proxy.provider.nameservice1 = "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider"
      dfs.client.use.datanode.hostname = "true"
    }
  }
}
```

#### 变更日志
如果要读取 Paimon 表的 changelog，首先要为 Paimon 源表设置 changelog-producer，然后使用 SeaTunnel 流任务读取。注意，批读取总是读取最新的快照，如需读取更完整的 changelog 数据，需使用流读取，并在将数据写入 Paimon 表之前开始流读取，为了确保顺序，流读取任务并行度应该设置为 1。

##### Streaming read 示例
```plaintext
env {
  parallelism = 1
  job.mode = "Streaming"
}

source {
  Paimon {
    warehouse = "/tmp/paimon"
    database = "full_type"
    table = "st_test"
  }
}

sink {
  Paimon {
    warehouse = "/tmp/paimon"
    database = "full_type"
    table = "st_test_sink"
    paimon.table.primary-keys = "c_tinyint"
  }
}
```

---

以上是对 Apache SeaTunnel Paimon 连接器的详细说明文档整理，希望对您有所帮助。