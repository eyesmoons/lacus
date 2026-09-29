Apache SeaTunnel Google Sheets 连接器的说明文档：

### GoogleSheets 源连接器

#### 描述
GoogleSheets 源连接器用于从 Google Sheets 读取数据。

#### 关键特性
- **批处理**：支持批量读取数据。
- **流处理**：支持流式读取数据。
- **精确一次**：确保数据精确一次性处理。
- **列投影**：支持列的投影，即选择性地读取某些列。
- **并行度**：支持并行处理，提高读取效率。
- **支持用户自定义分片**：允许用户自定义数据分片策略。

#### 文件格式
- **text**
- **csv**
- **json**

#### 选项
| 名称                | 类型   | 必需 | 默认值 |
| ------------------- | ------ | ---- | ------ |
| service_account_key | string | 是   | -      |
| sheet_id            | string | 是   | -      |
| sheet_name          | string | 是   | -      |
| range               | string | 是   | -      |
| schema              | config | 否   | -      |

#### 详细说明
- **service_account_key**：谷歌云服务帐户，需要 base64 编码。
- **sheet_id**：Google 表格 URL 中的表格 ID。
- **sheet_name**：要导入的工作表的名称。
- **range**：要导入的 sheet 页的范围。
- **schema**：上游数据的字段配置。

#### 示例
```plaintext
GoogleSheets {
  service_account_key = "seatunnel-test"
  sheet_id = "1VI0DvyZK-NIdssSdsDSsSSSC-_-rYMi7ppJiI_jhE"
  sheet_name = "sheets01"
  range = "A1:C3"
  schema = {
    fields {
      a = int
      b = string
      c = string
    }
  }
}
```

#### 变更日志
（此处应包含最新的变更日志信息，但文档中未提供具体内容）

#### 版本信息
Apache SeaTunnel 是一个在 Apache 软件基金会（ASF）孵化器中的项目，由 Apache Incubator 赞助。孵化期是为了确保新接受的项目在基础设施、通信和决策流程方面达到与其他成功 ASF 项目的稳定状态。

#### 版权信息
Copyright © 2021-2022 The Apache Software Foundation. Apache SeaTunnel, SeaTunnel, 和其羽毛标志是 The Apache Software Foundation 的商标。

请注意，以上信息是基于您提供的链接内容整理的，如有需要进一步详细的信息，请参考官方文档。