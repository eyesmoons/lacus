### Apache SeaTunnel Tablestore Source Connector 说明文档

#### 简介
Apache SeaTunnel 是一个开源的数据集成工具，支持大规模数据的实时同步。Tablestore Source Connector 是 SeaTunnel 的一个组件，用于从阿里云 Tablestore 数据库中读取数据，支持全量读取和变更数据捕获（CDC）。

#### 描述
Tablestore Source Connector 用于从阿里云 Tablestore 读取数据，支持批处理和流式处理模式，保证数据精确一次处理，支持列投影和用户自定义分区。

#### 主要特性
- **批处理**：支持全量数据读取。
- **流式处理**：支持实时数据变更捕获（CDC）。
- **精确一次**：确保数据在处理过程中不会丢失。
- **列投影**：可以选择性地读取特定的列。
- **并行处理**：支持并行读取，提高数据处理效率。
- **用户自定义分区**：支持自定义分区键，优化数据读取性能。

#### 选项
以下是 Tablestore Source Connector 的主要配置选项：

| 参数名              | 类型   | 是否必须 | 默认值 | 描述                               |
| ------------------- | ------ | -------- | ------ | ---------------------------------- |
| `end_point`         | string | 是       | -      | Tablestore 的端点。                |
| `instance_name`     | string | 是       | -      | Tablestore 的实例名称。            |
| `access_key_id`     | string | 是       | -      | Tablestore 的访问ID。              |
| `access_key_secret` | string | 是       | -      | Tablestore 的访问密钥。            |
| `table`             | string | 是       | -      | Tablestore 的表名。                |
| `primary_keys`      | array  | 是       | -      | 表的主键，只需添加一个唯一主键。   |
| `schema`            | config | 是       | -      | 数据模式配置，定义读取数据的结构。 |

#### 示例配置
以下是一个使用 Tablestore Source Connector 的示例配置：

```plaintext
env {
  parallelism = 1
  job.mode = "STREAMING"
}

source {
  # This is a example source plugin **only for test and demonstrate the feature source plugin**
  Tablestore {
    end_point = "https://****.cn-zhangjiakou.tablestore.aliyuncs.com"
    instance_name = "****"
    access_key_id = "***************2Ag5"
    access_key_secret = "***********2Dok"
    table = "test"
    primary_keys = ["id"]
    schema = {
      fields {
        id = string
        name = string
      }
    }
  }
}

sink {
  MongoDB {
    uri = "mongodb://localhost:27017"
    database = "test"
    collection = "test"
    primary-key = ["id"]
    schema = {
      fields {
        id = string
        name = string
      }
    }
  }
}
```

#### 更新日志
- 本文档基于 Apache SeaTunnel 2.3.11 版本。
- 更新日志和版本信息请参考官方文档。

#### 参考链接
- [Tablestore Source Connector 文档](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/Tablestore)

#### 注意事项
- 确保在使用 Tablestore Source Connector 之前已经正确配置了阿里云 Tablestore 的访问凭证和端点。
- 示例中的访问凭证和端点需要替换为实际的值。

通过以上说明文档，您可以对 Apache SeaTunnel Tablestore Source Connector 有一个全面的了解，并能够根据实际需求进行配置和使用。