根据您提供的链接内容，以下是关于 Apache SeaTunnel SLS 源连接器的详细说明和使用指南：

### Apache SeaTunnel SLS 源连接器

#### 简介
Apache SeaTunnel SLS 源连接器用于从阿里云 SLS（日志服务）中读取数据。它支持批处理和流处理模式，并确保精确一次（exactly-once）的数据处理。

#### 支持的引擎
- Spark
- Flink
- Seatunnel Zeta

#### 主要特性
- **批处理（batch）**
- **流处理（stream）**
- **精确一次（exactly-once）**
- **列投影（column projection）**
- **并行性（parallelism）**
- **支持用户自定义分区（support user-defined split）**

#### 描述
SLS 源连接器允许用户从阿里云 SLS 日志服务中高效地读取数据，适用于日志分析和数据集成的场景。

#### 支持的数据源信息
为了使用 SLS 连接器，需要以下依赖关系，它们可以通过 `install-plugin.sh` 或 Maven 中央存储库下载。

| 数据源 | 支持的版本 | Maven    |
| ------ | ---------- | -------- |
| Sls    | Universal  | Download |

#### 源选项（Source Options）
以下是 SLS 源连接器的配置选项：

| 名称                                | 类型                                        | 是否必需 | 默认值                   | 描述                                           |
| ----------------------------------- | ------------------------------------------- | -------- | ------------------------ | ---------------------------------------------- |
| project                             | String                                      | 是       | -                        | 阿里云 SLS 项目                                |
| logstore                            | String                                      | 是       | -                        | 阿里云 SLS 日志库                              |
| endpoint                            | String                                      | 是       | -                        | 阿里云访问服务点                               |
| access_key_id                       | String                                      | 是       | -                        | 阿里云访问用户ID                               |
| access_key_secret                   | String                                      | 是       | -                        | 阿里云访问用户密码                             |
| start_mode                          | StartMode[earliest],[group_cursor],[latest] | 否       | group_cursor             | 消费者的初始消费模式                           |
| consumer_group                      | String                                      | 否       | SeaTunnel-Consumer-Group | SLS 消费者组id，用于区分不同的消费者组         |
| auto_cursor_reset                   | CursorMode[begin],[end]                     | 否       | end                      | 当消费者组中没有记录读取游标时，初始化读取游标 |
| batch_size                          | Int                                         | 否       | 1000                     | 每次从 SLS 中读取的数据量                      |
| partition-discovery.interval-millis | Long                                        | 否       | -1                       | 动态发现主题和分区的间隔                       |

#### 任务示例
以下是一个简单的示例，展示如何读取 SLS 的 `logstore1` 数据并将其打印到客户端。

```sql
# Defining the runtime environment
env {
  parallelism = 2
  job.mode = "STREAMING"
  checkpoint.interval = 30000
}

source {
  Sls {
    endpoint = "cn-hangzhou-intranet.log.aliyuncs.com"
    project = "project1"
    logstore = "logstore1"
    access_key_id = "xxxxxxxxxxxxxxxxxxxxxxxx"
    access_key_secret = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
    schema = {
      fields = {
        id = "int"
        name = "string"
        description = "string"
        weight = "string"
      }
    }
  }
}

sink {
  Console {
  }
}
```

#### 变更日志
有关 SLS 源连接器的变更日志，请参考官方文档。

#### 社区与支持
- Apache SeaTunnel 是一个在 Apache 软件基金会（ASF）孵化器中的项目，由 Apache Incubator 赞助。
- 社区资源包括 GitHub、Issue Tracker、Pull Requests 和订阅邮件组。

#### 版权信息
Apache SeaTunnel、SeaTunnel 及其羽毛标志是 Apache 软件基金会的商标。

通过以上说明，您应该能够了解并使用 Apache SeaTunnel SLS 源连接器进行数据读取。如果您需要更详细的信息或有其他问题，请参考官方文档或社区资源。