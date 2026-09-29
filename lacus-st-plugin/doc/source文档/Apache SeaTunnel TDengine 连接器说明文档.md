### Apache SeaTunnel TDengine 连接器说明文档

#### 简介
Apache SeaTunnel 是一个分布式、高性能、易扩展的数据集成平台，用于海量数据的同步和转化。TDengine 连接器是 SeaTunnel 的一部分，允许用户通过 TDengine 读取外部数据源的数据。

#### TDengine 源连接器特性
- **批处理**：支持批量读取数据。
- **流式处理**：支持流式数据读取。
- **精确一次**：确保数据处理的精确性，一次处理完成。
- **列投影**：支持查询 SQL 并实现投影效果。
- **并行度**：支持用户自定义分片，提高数据处理效率。

#### 配置选项
以下是 TDengine 源连接器的配置选项：

| 参数名      | 类型   | 是否必需 | 默认值 |
| ----------- | ------ | -------- | ------ |
| url         | string | 是       | -      |
| username    | string | 是       | -      |
| password    | string | 是       | -      |
| database    | string | 是       | -      |
| stable      | string | 是       | -      |
| lower_bound | long   | 是       | -      |
| upper_bound | long   | 是       | -      |

#### 参数说明
- **url**：TDengine 的 URL 地址，例如 `jdbc:TAOS-RS://localhost:6041/`。
- **username**：TDengine 的用户名。
- **password**：TDengine 的密码。
- **database**：TDengine 的数据库名。
- **stable**：TDengine 的稳定名称。
- **lower_bound**：迁移周期的下边界。
- **upper_bound**：迁移周期的上边界。

#### 示例配置
以下是一个 TDengine 源连接器的配置示例：

```plaintext
source {
  TDengine {
    url      : "jdbc:TAOS-RS://localhost:6041/"
    username : "root"
    password : "taosdata"
    database : "power"
    stable   : "meters"
    lower_bound : "2018-10-03 14:38:05.000"
    upper_bound : "2018-10-03 14:38:16.800"
    plugin_output = "tdengine_result"
  }
}
```

#### 变更日志
- 更新记录和版本信息可以在 SeaTunnel 的官方文档中找到。

#### 相关链接
- [TDengine 源连接器文档](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/TDengine)
- [Apache SeaTunnel 官方网站](https://seatunnel.apache.org/)

#### 注意事项
- 确保在配置连接器时提供正确的 TDengine 地址、用户名、密码和数据库名称。
- 根据实际需求调整迁移周期的上下边界。

通过以上说明文档，用户可以更好地理解和使用 Apache SeaTunnel 的 TDengine 源连接器，实现高效的数据集成和处理。