### OpenMldb 源连接器说明文档

#### 描述
OpenMldb 源连接器用于从 OpenMldb 数据库中读取数据。它支持批处理和流处理，确保数据的精确一次处理，并提供列投影和并行度等功能，同时支持用户自定义分片。

#### 关键特性
- **批处理**：支持批量读取数据。
- **流处理**：支持实时数据流读取。
- **精确一次**：确保数据在读取过程中只被处理一次，避免数据重复或丢失。
- **列投影**：允许用户指定需要读取的列，提高数据读取效率。
- **并行度**：支持并行读取数据，提高数据处理速度。
- **支持用户自定义分片**：允许用户根据需求自定义数据分片规则。

#### 选项
以下是 OpenMldb 源连接器的配置选项：

| 名称            | 类型    | 是否必需 | 默认值 |
| --------------- | ------- | -------- | ------ |
| cluster_mode    | boolean | 是       | -      |
| sql             | string  | 是       | -      |
| database        | string  | 是       | -      |
| host            | string  | 否       | -      |
| port            | int     | 否       | -      |
| zk_path         | string  | 否       | -      |
| zk_host         | string  | 否       | -      |
| session_timeout | int     | 否       | 10000  |
| request_timeout | int     | 否       | 60000  |
| common-options  | -       | 否       | -      |

##### 详细说明
- **cluster_mode**：指定 OpenMldb 是否处于群集模式。
- **sql**：要执行的 SQL 查询语句。
- **database**：要连接的数据库名称。
- **host**：OpenMldb 主机地址，仅支持 OpenMldb 单模。
- **port**：OpenMldb 端口号，仅支持 OpenMldb 单模。
- **zk_host**：Zookeeper 主机地址，仅在 OpenMldb 集群模式下受支持。
- **zk_path**：Zookeeper 路径，仅在 OpenMldb 集群模式下受支持。
- **session_timeout**：OpenMldb 会话超时时间（毫秒），默认值为 60000。
- **request_timeout**：OpenMldb 请求超时时间（毫秒），默认值为 10000。
- **common-options**：源插件常用参数，具体请参考源插件常用参数文档。

#### 示例
以下是一个使用 OpenMldb 源连接器的示例配置：

```plaintext
OpenMldb {
  host = "172.17.0.2"
  port = 6527
  sql = "select * from demo_table1"
  database = "demo_db"
  cluster_mode = false
}
```

#### 变更日志
- 本文档基于 Apache SeaTunnel 2.3.11 版本编写。

#### 相关链接
- [OpenMldb 源连接器文档](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/source/OpenMldb/)
- [Apache SeaTunnel 官方网站](https://seatunnel.apache.org/)

#### 注意事项
- 请确保在配置连接器时，正确设置所有必需参数，以避免连接失败。
- 如果使用 OpenMldb 集群模式，需要正确配置 Zookeeper 地址和路径。

希望这份说明文档能帮助你更好地理解和使用 OpenMldb 源连接器。如果有任何问题或需要进一步的帮助，请随时联系技术支持。