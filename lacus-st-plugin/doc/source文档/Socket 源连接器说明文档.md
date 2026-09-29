根据您提供的链接内容，以下是对 Apache SeaTunnel 中 Socket 连接器的功能、配置和使用说明的整理：

### Socket 连接器概述

**功能**：
- 支持的数据引擎：Spark、Flink、SeaTunnel Zeta
- 主要特点：批处理、流处理、精确一次、列投影、并行性支持、用户定义的拆分

**描述**：
Socket 连接器用于从 Socket 读取数据。它允许配置 Socket 服务器的地址和端口，并且可以通过 Schema 指定 SeaTunnel 数据类型，以便将对应的数据转换为所需的 SeaTunnel 数据类型。

### 数据类型映射

在配置 Schema 时，可以指定 SeaTunnel 数据类型，以便正确地映射从 Socket 读取的数据。支持的数据类型包括：
- STRING
- SHORT
- INT
- BIGINT
- BOOLEAN
- DOUBLE
- DECIMAL
- FLOAT
- DATE
- TIMESTAMP
- BYTES
- ARRAY
- MAP

### 配置选项

**基本配置**：
- `host`：String 类型，必填，表示 Socket 服务器的地址。
- `port`：Integer 类型，必填，表示 Socket 服务器的端口。

**通用选项**：
- 提供源插件通用参数，详细内容请参考源通用选项。

### 创建 Socket 数据同步任务

以下是一个配置 SeaTunnel 配置文件以创建从 Socket 读取数据并打印到本地客户端的数据同步任务的示例：

```plaintext
# 设置任务的基本配置
env {
  parallelism = 1
  job.mode = "BATCH"
}

# 创建连接到 Socket 的源
source {
  Socket {
    host = "localhost"
    port = 9999
  }
}

# 控制台打印读取的 Socket 数据
sink {
  Console {
    parallelism = 1
  }
}
```

### 启动端口监听

在运行 SeaTunnel 任务之前，需要启动一个端口进行监听。可以使用如下的命令行工具来启动端口监听并发送测试数据：

```bash
nc -l 9999
```

发送测试数据示例：

```bash
test
hello
flink
spark
```

### 控制台输出

控制台将打印从 Socket 读取的数据：

```
[test]
[hello]
[flink]
[spark]
```

### 更新日志

关于 Socket 连接器的更新日志，请参考相关的版本发布说明。

以上内容整理自 Apache SeaTunnel 官方文档，提供了 Socket 连接器的功能、配置和使用说明，希望对您有所帮助。如果您需要更详细的信息或有其他问题，请随时提问。