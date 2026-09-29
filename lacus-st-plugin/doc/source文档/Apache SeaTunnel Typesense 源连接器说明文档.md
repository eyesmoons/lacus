根据您提供的链接内容，以下是对 Apache SeaTunnel 中 Typesense 数据连接器的说明文档整理：

---

### **Apache SeaTunnel Typesense 数据连接器说明文档**

#### **1. 简介**
Apache SeaTunnel 是一个高性能、分布式、海量数据集成框架，支持多种数据源的连接。Typesense 是 SeaTunnel 中的一种数据连接器，用于从 Typesense 读取数据。

#### **2. 功能描述**
Typesense 源连接器的主要功能包括：
- **批处理**：支持批量读取数据。
- **流处理**：支持实时数据流处理。
- **精确一次**：确保数据精确读取一次，避免重复。
- **Schema**：支持定义数据模式。
- **并行度**：支持并行读取，提高效率。
- **支持用户定义的拆分**：允许用户自定义数据拆分方式。

#### **3. 主要选项**
连接器提供了以下主要配置选项：

| 名称       | 类型   | 必填 | 默认值 |
| ---------- | ------ | ---- | ------ |
| hosts      | array  | 是   | -      |
| collection | string | 是   | -      |
| schema     | config | 是   | -      |
| api_key    | string | 否   | -      |
| query      | string | 否   | -      |
| batch_size | int    | 否   | 100    |

##### **详细说明**
- **hosts**: Typesense 的访问地址，格式为 `host:port`，例如：`["typesense-01:8108"]`。
- **collection**: 要写入的集合名，例如：“seatunnel”。
- **schema**: Typesense 需要读取的列配置。
- **api_key**: Typesense 安全认证的 API 密钥。
- **batch_size**: 读取数据时，每批次查询数量。
- **query**: 查询条件，例如：`q=*&filter_by=num_employees:>9000`。

#### **4. 示例配置**
以下是一个使用 Typesense 连接器的示例配置：

```plaintext
source {
  Typesense {
    hosts = ["localhost:8108"]
    collection = "companies"
    api_key = "xyz"
    query = "q=*&filter_by=num_employees:>9000"
    schema = {
      fields {
        company_name_list = array<string>
        company_name = string
        num_employees = long
        country = string
        id = string
      }
    }
  }
}
```

#### **5. 变更日志**
（此处应包含最新的变更日志，但链接内容未提供详细信息）

#### **6. 社区与支持**
- **社区**: Apache SeaTunnel 社区提供支持和交流。
- **GitHub**: [Apache SeaTunnel GitHub](https://github.com/apache/seatunnel)
- **Issue Tracker**: [Apache SeaTunnel Issue Tracker](https://issues.apache.org/jira/projects/SEATUNNEL)
- **Pull Requests**: [Apache SeaTunnel Pull Requests](https://github.com/apache/seatunnel/pulls)

#### **7. 注意事项**
- 确保安装并配置好 Typesense 服务。
- 确认 API 密钥和集合名称正确无误。
- 根据实际需求调整 `batch_size` 和查询条件。

---

以上是 Apache SeaTunnel Typesense 数据连接器的说明文档整理，希望对您有所帮助。如有更多问题，请参考官方文档或社区支持。