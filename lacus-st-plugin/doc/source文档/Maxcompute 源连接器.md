根据您提供的链接内容，以下是对 Apache SeaTunnel 中 Maxcompute 源连接器的说明文档整理：

### Maxcompute 源连接器

#### 描述
Maxcompute 源连接器用于从 Maxcompute 读取数据。

#### 关键特性
- **批处理**：支持批量数据处理。
- **精确一次**：确保数据精确传输一次，无重复或丢失。
- **列投影**：支持指定读取特定列，提高数据传输效率。
- **并行度**：支持并行数据处理，提升性能。
- **支持用户自定义分片**：允许用户自定义数据分片规则，优化数据处理。

#### 选项
| 名称           | 类型   | 必需 | 默认值 |
| -------------- | ------ | ---- | ------ |
| accessId       | string | 是   | -      |
| accesskey      | string | 是   | -      |
| endpoint       | string | 是   | -      |
| project        | string | 是   | -      |
| table_name     | string | 是   | -      |
| partition_spec | string | 否   | -      |
| split_row      | int    | 否   | 10000  |
| read_columns   | Array  | 否   | -      |
| table_list     | Array  | 否   | -      |
| common-options | string | 否   | -      |
| schema         | config | 否   | -      |

#### 选项说明
- **accessId [string]**：您的 Maxcompute 密钥 Id，用于从阿里云访问服务。
- **accesskey [string]**：您的 Maxcompute 密钥，用于从阿里云访问服务。
- **endpoint [string]**：您的 Maxcompute 端点，以 http 开头。
- **project [string]**：您在阿里云中创建的 Maxcompute 项目名称。
- **table_name [string]**：目标 Maxcompute 表名，例如：fake.
- **partition_spec [string]**：Maxcompute 分区表的规范，例如：ds='20220101'.
- **split_row [int]**：每次拆分的行数，默认值: 10000.
- **read_columns [Array]**：要读取的列，如果未设置，则读取所有列。例如: ["col1", "col2"].
- **table_list [Array]**：要读取的表列表，可以使用此配置代替 table_name.
- **common options**：源插件常用参数，详见源通用选项。

#### 示例
##### 表读取
```plaintext
source {
 Maxcompute {
 accessId="your access id"
 accesskey="your access Key"
 endpoint="http://service.odps.aliyun.com/api"
 project="your project"
 table_name="your table name"
 #partition_spec="your partition spec"
 #split_row = 10000
 #read_columns = ["col1", "col2"]
 }
}
```

##### 使用表列表读取
```plaintext
source {
 Maxcompute {
 accessId="your access id"
 accesskey="your access Key"
 endpoint="http://service.odps.aliyun.com/api"
 project="your project" # default project
 table_list = [
 { table_name = "test_table" #partition_spec="your partition spec"
 #split_row = 10000
 #read_columns = ["col1", "col2"]
 },
 { project = "test_project"
 table_name = "test_table2"
 #partition_spec="your partition spec"
 #split_row = 10000
 #read_columns = ["col1", "col2"]
 }
 ]
 }
}
```

#### 变更日志
- 更新文档内容，确保信息的准确性和完整性。

#### 版本信息
- 本文档适用于 Apache SeaTunnel 版本 2.3.11。

#### 社区与支持
- Apache SeaTunnel 是一个由社区开发的项目，支持通过 GitHub、Issue Tracker 和邮件组获取帮助。

#### 版权信息
- 版权 © 2021-2022 The Apache Software Foundation. Apache SeaTunnel, SeaTunnel, 和其羽毛标志是 The Apache Software Foundation 的商标。

以上内容整理自提供的链接，希望能帮助您更好地理解和使用 Maxcompute 源连接器。