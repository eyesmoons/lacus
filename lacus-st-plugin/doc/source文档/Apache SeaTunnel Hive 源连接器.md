Apache SeaTunnel Hive 连接器是一个用于从 Hive 数据库中读取数据的源连接器。它支持批处理和流处理，能够实现精确一次的数据读取。以下是该连接器的详细说明文档：

### 描述
- **功能**：从 Hive 读取数据。
- **集成要求**：使用此连接器前，必须确保 Spark/Flink 集群已经集成了 Hive。测试支持的 Hive 版本为 2.3.9 和 3.1.3。

### 关键特性
- **批处理**：在 pollNext 调用中读取分片中的所有数据，并将读取的数据保存在快照中。
- **流处理**：支持实时数据流处理。
- **精确一次**：确保数据在读取过程中不会丢失。
- **schema 投影**：支持用户定义的数据投影。
- **并行度**：支持并行处理数据。
- **用户定义的分片**：支持用户自定义数据分片规则。
- **文件格式**：支持多种文件格式，包括文本、CSV、Parquet、ORC 和 JSON。

### 选项
- **table_name**：目标 Hive 表名，例如 `db1.table1`。
- **metastore_uri**：Hive 元存储 URI。
- **krb5_path**：Kerberos 认证配置文件路径。
- **kerberos_principal**：Kerberos 认证主体。
- **kerberos_keytab_path**：Kerberos 认证的 keytab 文件路径。
- **hdfs_site_path**：`hdfs-site.xml` 文件的路径，用于加载 Namenode 的高可用配置。
- **hive_site_path**：`hive-site.xml` 文件的路径。
- **hive.hadoop.conf**：Hadoop 配置中的属性（core-site.xml、hdfs-site.xml、hive-site.xml）。
- **hive.hadoop.conf-path**：指定加载 core-site.xml、hdfs-site.xml、hive-site.xml 文件的路径。
- **read_partitions**：用户希望从 Hive 表中读取的目标分区。
- **read_columns**：数据源的读取列列表，用于字段投影。
- **compress_codec**：文件的压缩编解码器，支持的编解码器包括 lzo 和 none。

### 示例
#### 示例 1：单表
```json
Hive {
  table_name = "default.seatunnel_orc"
  metastore_uri = "thrift://namenode001:9083"
}
```

#### 示例 2：多表
```json
Hive {
  table_list = [
    {
      table_name = "default.seatunnel_orc_1"
      metastore_uri = "thrift://namenode001:9083"
    },
    {
      table_name = "default.seatunnel_orc_2"
      metastore_uri = "thrift://namenode001:9083"
    }
  ]
}
```

#### 示例 3：Kerberos
```json
source {
  Hive {
    table_name = "default.test_hive_sink_on_hdfs_with_kerberos"
    metastore_uri = "thrift://metastore:9083"
    hive.hadoop.conf-path = "/tmp/hadoop"
    plugin_output = hive_source
    hive_site_path = "/tmp/hive-site.xml"
    kerberos_principal = "hive/metastore.seatunnel@EXAMPLE.COM"
    kerberos_keytab_path = "/tmp/hive.keytab"
    krb5_path = "/tmp/krb5.conf"
  }
}
```

### Hive on S3
#### 步骤 1
为 EMR 的 Hive 创建 lib 目录。
```bash
mkdir -p ${SEATUNNEL_HOME}/plugins/Hive/lib
```

#### 步骤 2
从 Maven 中心获取 jar 文件到 lib 目录。
```bash
cd ${SEATUNNEL_HOME}/plugins/Hive/lib
wget https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-aws/2.6.5/hadoop-aws-2.6.5.jar
wget https://repo1.maven.org/maven2/org/apache/hive/hive-exec/2.3.9/hive-exec-2.3.9.jar
```

#### 步骤 3
从您的 EMR 环境中复制 jar 文件到 lib 目录。
```bash
cp /usr/share/aws/emr/emrfs/lib/emrfs-hadoop-assembly-2.60.0.jar ${SEATUNNEL_HOME}/plugins/Hive/lib
cp /usr/share/aws/emr/hadoop-state-pusher/lib/hadoop-common-3.3.6-amzn-1.jar ${SEATUNNEL_HOME}/plugins/Hive/lib
cp /usr/share/aws/emr/hadoop-state-pusher/lib/javax.inject-1.jar ${SEATUNNEL_HOME}/plugins/Hive/lib
cp /usr/share/aws/emr/hadoop-state-pusher/lib/aopalliance-1.0.jar ${SEATUNNEL_HOME}/plugins/Hive/lib
```

#### 步骤 4
运行案例。

### Hive on OSS
#### 步骤 1
为 EMR 的 Hive 创建 lib 目录。
```bash
mkdir -p ${SEATUNNEL_HOME}/plugins/Hive/lib
```

#### 步骤 2
从 Maven 中心获取 jar 文件到 lib 目录。
```bash
cd ${SEATUNNEL_HOME}/plugins/Hive/lib
wget https://repo1.maven.org/maven2/org/apache/hive/hive-exec/2.3.9/hive-exec-2.3.9.jar
```

#### 步骤 3
从您的 EMR 环境中复制 jar 文件到 lib 目录并删除冲突的 jar。
```bash
cp -r /opt/apps/JINDOSDK/jindosdk-current/lib/jindo-*.jar ${SEATUNNEL_HOME}/plugins/Hive/lib
rm -f ${SEATUNNEL_HOME}/lib/hadoop-aliyun-*.jar
```

#### 步骤 4
运行案例。

### 总结
Apache SeaTunnel Hive 连接器提供了丰富的功能和灵活的配置选项，能够满足不同场景下的数据读取需求。通过以上文档，您可以详细了解该连接器的使用方法和配置细节，从而高效地从 Hive 数据库中读取数据。