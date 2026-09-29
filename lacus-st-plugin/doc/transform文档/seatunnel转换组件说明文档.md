## 一. copy

### 1. 定义

复制转换插件。将字段复制到一个新字段。

### 2. 属性

fields

### 3. 常见参数

| 参数名称      | 参数类型 | 是否必须 | 默认值 |
| ------------- | -------- | -------- | ------ |
| plugin_output | string   | no       | -      |
| plugin_input  | string   | no       | -      |

### 4. 示例

从源读取的数据是这样的一个表:

| name     | age  | card |
| -------- | ---- | ---- |
| Joy Ding | 20   | 123  |
| May Ding | 20   | 123  |
| Kin Dom  | 20   | 123  |
| Joy Dom  | 20   | 123  |

想要将字段 `name`、`age` 复制到新的字段 `name1`、`name2`、`age1`，我们可以像这样添加 `Copy` 转换：

```json
transform {
  Copy {
    plugin_input = "fake"
    plugin_output = "fake1"
    fields {
      name1 = name
      name2 = name
      age1 = age
    }
  }
}
```

那么结果表 `fake1` 中的数据将会像这样：

| name     | age  | card | name1    | name2    | age1 |
| -------- | ---- | ---- | -------- | -------- | ---- |
| Joy Ding | 20   | 123  | Joy Ding | Joy Ding | 20   |
| May Ding | 20   | 123  | May Ding | May Ding | 20   |
| Kin Dom  | 20   | 123  | Kin Dom  | Kin Dom  | 20   |
| Joy Dom  | 20   | 123  | Joy Dom  | Joy Dom  | 20   |



## 二、DynamicCompile

### 1. 定义

动态编译插件。提供一种可编程的方式来处理行，允许用户自定义任何业务行为，甚至基于现有行字段作为参数的RPC请求，或者通过从其他数据源检索相关数据来扩展字段。

### 2. 属性

| name             | type   | required | default value |
| ---------------- | ------ | -------- | ------------- |
| source_code      | string | no       |               |
| compile_language | Enum   | yes      |               |
| compile_pattern  | Enum   | no       | SOURCE_CODE   |
| absolute_path    | string | no       |               |

① compile_language：GROOVY，JAVA

② compile_pattern：SOURCE_CODE,ABSOLUTE_PATH

③ absolute_path：服务器上Java或Groovy文件的绝对路径

④ source_code：源代码

```
关于source_code, 在代码中，你必须实现两个方法:
Column[] getInlineOutputColumns(CatalogTable inputCatalogTable)
Object[] getInlineOutputFieldValues(SeaTunnelRowAccessor inputRow)

getInlineOutputColumns方法中，入参类型为CatalogTable，返回结果为Column[]。 你可以从入参的CatalogTable获取当前表的表结构。 在返回结果中，如果字段已经存在，则会根据返回结果进行覆盖，如果不存在，则会添加到现有表结构中。

getInlineOutputFieldValues方法，入参类型为SeaTunnelRowAccessor，返回结果为Object[] 你可以从SeaTunnelRowAccessor获取到当前行的数据，进行自己的定制化数据处理逻辑。 返回结果中，数组长度需要与getInlineOutputColumns方法返回的长度一致，并且里面的字段值顺序也需要保持一致。
```

### 3. 例子

源端数据读取的表格如下：

| name     | age  | card |
| -------- | ---- | ---- |
| Joy Ding | 20   | 123  |
| May Ding | 20   | 123  |
| Kin Dom  | 30   | 123  |
| Joy Dom  | 30   | 123  |

我们将使用`DynamicCompile`对数据进行修改，添加一列`compile_language`字段，并且将`age`字段更新，当`age=20`时将其更新为`40`

① 使用groovy

```groovy
transform {
 DynamicCompile {
    plugin_input = "fake"
    plugin_output = "groovy_out"
    compile_language="GROOVY"
    compile_pattern="SOURCE_CODE"
    source_code="""
                 import org.apache.seatunnel.api.table.catalog.Column
                 import org.apache.seatunnel.api.table.type.SeaTunnelRowAccessor
                 import org.apache.seatunnel.api.table.catalog.CatalogTable
                 import org.apache.seatunnel.api.table.catalog.PhysicalColumn;
                 import org.apache.seatunnel.api.table.type.*;
                 import java.util.ArrayList;
                 class demo  {
                    public Column[] getInlineOutputColumns(CatalogTable inputCatalogTable) {
                        PhysicalColumn col1 =
                                PhysicalColumn.of(
                                        "compile_language",
                                        BasicType.STRING_TYPE,
                                        10L,
                                        true,
                                        "",
                                        "");
                        PhysicalColumn col2 =
                                PhysicalColumn.of(
                                        "age",
                                        BasicType.INT_TYPE,
                                        0L,
                                        false,
                                        false,
                                        ""
                                );
                        return new Column[]{
                                col1, col2
                        };
                    }
                
                
                    public Object[] getInlineOutputFieldValues(SeaTunnelRowAccessor inputRow) {
                        Object[] fieldValues = new Object[2];
                        // get age 
                        Object ageField = inputRow.getField(1);
                        fieldValues[0] = "GROOVY";
                        if (Integer.parseInt(ageField.toString()) == 20) {
                            fieldValues[1] = 40;
                        } else {
                            fieldValues[1] = ageField;
                        }
                        return fieldValues;
                    }
                 };"""

  }
}
```

② 使用java

```java
transform {
 DynamicCompile {
    plugin_input = "fake"
    plugin_output = "java_out"
    compile_language="JAVA"
    compile_pattern="SOURCE_CODE"
    source_code="""
                 import org.apache.seatunnel.api.table.catalog.Column;
                 import org.apache.seatunnel.api.table.type.SeaTunnelRowAccessor;
                 import org.apache.seatunnel.api.table.catalog.*;
                 import org.apache.seatunnel.api.table.type.*;
                 import java.util.ArrayList;
                    public Column[] getInlineOutputColumns(CatalogTable inputCatalogTable) {
                        PhysicalColumn col1 =
                                PhysicalColumn.of(
                                        "compile_language",
                                        BasicType.STRING_TYPE,
                                        10L,
                                        true,
                                        "",
                                        "");
                        PhysicalColumn col2 =
                                PhysicalColumn.of(
                                        "age",
                                        BasicType.INT_TYPE,
                                        0L,
                                        false,
                                        false,
                                        ""
                                );
                        return new Column[]{
                                col1, col2
                        };
                    }
                
                
                    public Object[] getInlineOutputFieldValues(SeaTunnelRowAccessor inputRow) {
                        Object[] fieldValues = new Object[2];
                        // get age 
                        Object ageField = inputRow.getField(1);
                        fieldValues[0] = "JAVA";
                        if (Integer.parseInt(ageField.toString()) == 20) {
                            fieldValues[1] = 40;
                        } else {
                            fieldValues[1] = ageField;
                        }
                        return fieldValues;
                    }
                """

  }
 } 
```

③ 指定源码文件路径

```
 transform {
 DynamicCompile {
    plugin_input = "fake"
    plugin_output = "groovy_out"
    compile_language="GROOVY"
    compile_pattern="ABSOLUTE_PATH"
    absolute_path="""/tmp/GroovyFile"""

  }
}
```

那么结果表 `groovy_out` 中的数据将会更新为：

| name     | age  | card | compile_language |
| -------- | ---- | ---- | ---------------- |
| Joy Ding | 40   | 123  | GROOVY           |
| May Ding | 40   | 123  | GROOVY           |
| Kin Dom  | 30   | 123  | GROOVY           |
| Joy Dom  | 30   | 123  | GROOVY           |

那么结果表 `java_out` 中的数据将会更新为：

| name     | age  | card | compile_language |
| -------- | ---- | ---- | ---------------- |
| Joy Ding | 40   | 123  | JAVA             |
| May Ding | 40   | 123  | JAVA             |
| Kin Dom  | 30   | 123  | JAVA             |
| Joy Dom  | 30   | 123  | JAVA             |



## 三、FieldMapper

### 1. 定义

字段映射转换插件。添加输入模式和输出模式映射。

### 2. 属性

| 名称         | 类型   | 是否必须 | 默认值 |
| ------------ | ------ | -------- | ------ |
| field_mapper | Object | yes      |        |

① field_mapper：指定输入和输出之间的字段映射关系

### 3. 例子

源端数据读取的表格如下：

| id   | name     | age  | card |
| ---- | -------- | ---- | ---- |
| 1    | Joy Ding | 20   | 123  |
| 2    | May Ding | 20   | 123  |
| 3    | Kin Dom  | 20   | 123  |
| 4    | Joy Dom  | 20   | 123  |

我们想要删除 `age` 字段，并更新字段顺序为 `id`、`card`、`name`，同时将 `name` 重命名为 `new_name`。我们可以像这样添加 `FieldMapper` 转换：

```
transform {
  FieldMapper {
    plugin_input = "fake"
    plugin_output = "fake1"
    field_mapper = {
        id = id
        card = card
        name = new_name
    }
  }
}
```

那么结果表 `fake1` 中的数据将会像这样：

| id   | card | new_name |
| ---- | ---- | -------- |
| 1    | 123  | Joy Ding |
| 2    | 123  | May Ding |
| 3    | 123  | Kin Dom  |
| 4    | 123  | Joy Dom  |



## 四、Filter

### 1. 定义

过滤器转换插件。用于过滤字段。

### 2. 属性

| 名称           | 类型  | 是否必须 | 默认值 |
| -------------- | ----- | -------- | ------ |
| include_fields | array | no       |        |
| exclude_fields | array | no       |        |

① include_fields：需要保留的字段列表。不在列表中的字段将被删除。

② exclude_fields：需要删除的字段列表。不在列表中的字段将被保留。

注意，`include_fields` 和 `exclude_fields` 两个属性中，必须设置一个且只能设置一个

### 3. 示例

源端数据读取的表格如下：

| name     | age  | card |
| -------- | ---- | ---- |
| Joy Ding | 20   | 123  |
| May Ding | 20   | 123  |
| Kin Dom  | 20   | 123  |
| Joy Dom  | 20   | 123  |

我们想要保留字段 `name`, `card`，我们可以像这样添加 `Filter` 转换:

```
transform {
  Filter {
    plugin_input = "fake"
    plugin_output = "fake1"
    include_fields = [name, card]
  }
}
```

我们也可以通过删除字段 `age` 来实现， 我们可以添加一个 `Filter` 转换，并设置exclude_fields：

```
transform {
  Filter {
    plugin_input = "fake"
    plugin_output = "fake1"
    exclude_fields = [age]
  }
}
```

那么结果表 `fake1` 中的数据将会像这样：

| name     | card |
| -------- | ---- |
| Joy Ding | 123  |
| May Ding | 123  |
| Kin Dom  | 123  |
| Joy Dom  | 123  |



## 五、FilterRowKind

### 1. 定义

行类型转换插件。按行类型过滤数据。

### 2. 属性

| 名称          | 类型  | 是否必须 | 默认值 |
| ------------- | ----- | -------- | ------ |
| include_kinds | array | yes      |        |
| exclude_kinds | array | yes      |        |

① include_kinds：要包含的行类型

② exclude_kinds：要排除的行类型。

您只能配置 `include_kinds` 和 `exclude_kinds` 中的一个。

### 3. 示例

FakeSource 生成的数据的行类型是 `INSERT`。如果我们使用 `FilterRowKink` 转换并排除 `INSERT` 数据，我们将不会向接收器写入任何行。

```

env {
  job.mode = "BATCH"
}

source {
  FakeSource {
    plugin_output = "fake"
    row.num = 100
    schema = {
      fields {
        id = "int"
        name = "string"
        age = "int"
      }
    }
  }
}

transform {
  FilterRowKind {
    plugin_input = "fake"
    plugin_output = "fake1"
    exclude_kinds = ["INSERT"]
  }
}

sink {
  Console {
    plugin_input = "fake1"
  }
}
```



## 六、JsonPath

### 1. 定义

JSONPath 转换插件。支持使用 JSONPath 选择数据

### 2. 属性

| 名称                 | 类型  | 是否必须 | 默认值 |
| -------------------- | ----- | -------- | ------ |
| columns              | Array | Yes      |        |
| row_error_handle_way | Enum  | No       | FAIL   |

① row_error_handle_way：该选项用于指定当该行发生错误时的处理方式，默认值为 `FAIL`。

- FAIL：选择`FAIL`时，数据格式错误会阻塞并抛出异常。
- SKIP：选择`SKIP`时，数据格式错误会跳过该行数据。

② columns：

#### 属性

| 名称                    | 类型   | 是否必须 | 默认值 |
| ----------------------- | ------ | -------- | ------ |
| src_field               | String | Yes      |        |
| dest_field              | String | Yes      |        |
| path                    | String | Yes      |        |
| dest_type               | String | No       | String |
| column_error_handle_way | Enum   | No       |        |

① src_field：要解析的 JSON 源字段

② dest_field：使用 JSONPath 后的输出字段

③ dest_type：目标字段的类型

④ path: Jsonpath

⑤ column_error_handle_way: 该选项用于指定当列发生错误时的处理方式。

- FAIL：选择`FAIL`时，数据格式错误会阻塞并抛出异常。
- SKIP：选择`SKIP`时，数据格式错误会跳过此列数据。
- SKIP_ROW：选择`SKIP_ROW`时，数据格式错误会跳过此行数据。

### 3. 示例

① 读取 JSON

从源读取的数据是像这样的 JSON

```
{
  "data": {
    "c_string": "this is a string",
    "c_boolean": true,
    "c_integer": 42,
    "c_float": 3.14,
    "c_double": 3.14,
    "c_decimal": 10.55,
    "c_date": "2023-10-29",
    "c_datetime": "16:12:43.459",
    "c_array":["item1", "item2", "item3"],
    "c_map_array": [{"c_string_1":"c_string_1","c_string_2":"c_string_2","c_string_3":"c_string_3"},{"c_string_1":"c_string_1","c_string_2":"c_string_2","c_string_3":"c_string_3"}]
  }
}
```

假设我们想要使用 JsonPath 提取属性。

```
transform {
  JsonPath {
    plugin_input = "fake"
    plugin_output = "fake1"
    columns = [
     {
        "src_field" = "data"
        "path" = "$.data.c_string"
        "dest_field" = "c1_string"
     },
     {
        "src_field" = "data"
        "path" = "$.data.c_boolean"
        "dest_field" = "c1_boolean"
        "dest_type" = "boolean"
     },
     {
        "src_field" = "data"
        "path" = "$.data.c_integer"
        "dest_field" = "c1_integer"
        "dest_type" = "int"
     },
     {
        "src_field" = "data"
        "path" = "$.data.c_float"
        "dest_field" = "c1_float"
        "dest_type" = "float"
     },
     {
        "src_field" = "data"
        "path" = "$.data.c_double"
        "dest_field" = "c1_double"
        "dest_type" = "double"
     },
      {
         "src_field" = "data"
         "path" = "$.data.c_decimal"
         "dest_field" = "c1_decimal"
         "dest_type" = "decimal(4,2)"
      },
      {
         "src_field" = "data"
         "path" = "$.data.c_date"
         "dest_field" = "c1_date"
         "dest_type" = "date"
      },
      {
         "src_field" = "data"
         "path" = "$.data.c_datetime"
         "dest_field" = "c1_datetime"
         "dest_type" = "time"
      },
      {
         "src_field" = "data"
         "path" = "$.data.c_array"
         "dest_field" = "c1_array"
         "dest_type" = "array<string>"
      },
      {
        "src_field" = "data"
        "path" = "$.data.c_map_array"
        "dest_field" = "c1_map_array"
        "dest_type" = "array<map<string, string>>"
      }
    ]
  }
}
```

那么数据结果表 `fake1` 将会像这样:

| data                         | c1_string        | c1_boolean | c1_integer | c1_float | c1_double | c1_decimal | c1_date    | c1_datetime  | c1_array                    |
| ---------------------------- | ---------------- | ---------- | ---------- | -------- | --------- | ---------- | ---------- | ------------ | --------------------------- |
| too much content not to show | this is a string | true       | 42         | 3.14     | 3.14      | 10.55      | 2023-10-29 | 16:12:43.459 | ["item1", "item2", "item3"] |

② 读取 SeatunnelRow

假设数据行中的一列的类型是 SeatunnelRow，列的名称为 col

| SeatunnelRow(col) | other |      |
| ----------------- | ----- | ---- |
| name              | age   | .... |
| a                 | 18    | .... |

JsonPath 转换将 seatunnel 的值转换为一个数组。

```
transform {
  JsonPath {
    plugin_input = "fake"
    plugin_output = "fake1"

    row_error_handle_way = FAIL
    columns = [
     {
        "src_field" = "col"
        "path" = "$[0]"
        "dest_field" = "name"
        "dest_type" = "string"
     },
     {
        "src_field" = "col"
        "path" = "$[1]"
        "dest_field" = "age"
        "dest_type" = "int"
     }
    ]
  }
}
```

那么数据结果表 `fake1` 将会像这样:

| name | age  | col      | other |
| ---- | ---- | -------- | ----- |
| a    | 18   | ["a",18] | ...   |

### 4. 配置异常数据处理策略

您可以配置 `row_error_handle_way` 与 `column_error_handle_way` 来处理异常数据，两者都是非必填项。

`row_error_handle_way` 配置对行数据内所有数据异常进行处理，`column_error_handle_way` 配置对某列数据异常进行处理，优先级高于 `row_error_handle_way`。

**① 跳过异常数据行**

配置跳过任意列有异常的整行数据

```hocon
transform {
  JsonPath {

    row_error_handle_way = SKIP
    
    columns = [
     {
        "src_field" = "json_data"
        "path" = "$.f1"
        "dest_field" = "json_data_f1"
     },
     {
        "src_field" = "json_data"
        "path" = "$.f2"
        "dest_field" = "json_data_f2"
     }
    ]
  }
}
```



**② 跳过部分异常数据列**

配置仅对 `json_data_f1` 列数据异常跳过，填充空值，其他列数据异常继续抛出异常中断处理程序

```hocon
transform {
  JsonPath {

    row_error_handle_way = FAIL
    
    columns = [
     {
        "src_field" = "json_data"
        "path" = "$.f1"
        "dest_field" = "json_data_f1"
        
        "column_error_handle_way" = "SKIP"
     },
     {
        "src_field" = "json_data"
        "path" = "$.f2"
        "dest_field" = "json_data_f2"
     }
    ]
  }
}
```



**③ 部分列异常跳过整行**

配置仅对 `json_data_f1` 列数据异常跳过整行数据，其他列数据异常继续抛出异常中断处理程序

```hocon
transform {
  JsonPath {

    row_error_handle_way = FAIL
    
    columns = [
     {
        "src_field" = "json_data"
        "path" = "$.f1"
        "dest_field" = "json_data_f1"
        
        "column_error_handle_way" = "SKIP_ROW"
     },
     {
        "src_field" = "json_data"
        "path" = "$.f2"
        "dest_field" = "json_data_f2"
     }
    ]
  }
}
```



## 七、Metadata

### 1. 定义

元数据转换插件，用于将元数据字段添加到数据中

### 2. 支持的元数据

| Key       | DataType | Description                                 |
| --------- | -------- | ------------------------------------------- |
| Database  | string   | 包含该行的数据库名                          |
| Table     | string   | 包含该行的数表名                            |
| RowKind   | string   | 行类型                                      |
| EventTime | Long     |                                             |
| Delay     | Long     | 数据抽取时间与数据库变更时间的差            |
| Partition | string   | 包含该行对应数表的分区字段，多个使用`,`连接 |

> `Delay` `Partition`目前只适用于cdc系列连接器，除外TiDB-CDC

### 3. 属性

| name            | type | required | default value | Description                        |
| --------------- | ---- | -------- | ------------- | ---------------------------------- |
| metadata_fields | map  | 是       | -             | 元数据字段与输入字段相应的映射关系 |

### 4. 示例

```

env {
    parallelism = 1
    job.mode = "STREAMING"
    checkpoint.interval = 5000
    read_limit.bytes_per_second = 7000000
    read_limit.rows_per_second = 400
}

source {
    MySQL-CDC {
        plugin_output = "customers_mysql_cdc"
        server-id = 5652
        username = "root"
        password = "zdyk_Dev@2024"
        table-names = ["source.user"]
        base-url = "jdbc:mysql://172.16.17.123:3306/source"
    }
}

transform {
  Metadata {
    metadata_fields {
        Database = database
        Table = table
        RowKind = rowKind
        EventTime = ts_ms
        Delay = delay
    }
    plugin_output = "trans_result"
  }
}

sink {
  Console {
  plugin_input = "custom_name"
  }
}

```



## 八、Embedding

### 1. 定义

利用 embedding 模型将文本数据转换为向量化表示。此转换可以应用于各种字段。该插件支持多种模型提供商，并且可以与不同的API集成。

### 2. 属性

| 名称                           | 类型   | 是否必填 | 默认值 | 描述                                                         |
| ------------------------------ | ------ | -------- | ------ | ------------------------------------------------------------ |
| model_provider                 | enum   | 是       | -      | embedding模型的提供商。可选项包括 `QIANFAN`、`OPENAI` 等。   |
| api_key                        | string | 是       | -      | 用于验证embedding服务的API密钥。                             |
| secret_key                     | string | 是       | -      | 用于额外验证的密钥。一些提供商可能需要此密钥进行安全的API请求。 |
| single_vectorized_input_number | int    | 否       | 1      | 单次请求向量化的输入数量。默认值为1。                        |
| vectorization_fields           | map    | 是       | -      | 输入字段和相应的输出向量字段之间的映射。                     |
| model                          | string | 是       | -      | 要使用的具体embedding模型。例如，如果提供商为OPENAI，可以指定 `text-embedding-3-small`。 |
| api_path                       | string | 否       | -      | embedding服务的API。通常由模型提供商提供。                   |
| dimension                      | int    | 否       | 2048   | 向量维度默认为 2048，Embedding-3模型支持自定义向量维度，建议选择256、512、1024或2048维度。 |
| oauth_path                     | string | 否       | -      | oauth 服务的 API 。                                          |
| custom_config                  | map    | 否       |        | 模型的自定义配置。                                           |
| custom_response_parse          | string | 否       |        | 使用 JsonPath 解析模型响应的方式。示例：`$.choices[*].message.content`。 |
| custom_request_headers         | map    | 否       |        | 发送到模型的请求的自定义头信息。                             |
| custom_request_body            | map    | 否       |        | 请求体的自定义配置。支持占位符如 `${model}`、`${input}`。    |

① embedding_model_provider：用于生成 embedding 的模型提供商。常见选项包括 `DOUBAO`、`QIANFAN`、`OPENAI` 等，同时可选择 `CUSTOM` 实现自定义 embedding 模型的请求以及获取。

② api_key：用于验证 embedding 服务请求的API密钥。通常由模型提供商在你注册他们的服务时提供。

③ secret_key：用于额外验证的密钥。一些提供商可能要求此密钥以确保API请求的安全性。

④ single_vectorized_input_number：指定单次请求向量化的输入数量。默认值为1。根据处理能力和模型提供商的API限制进行调整。

⑤ vectorization_fields：输入字段和相应的输出向量字段之间的映射。这使得插件可以理解要向量化的文本字段以及如何存储生成的向量。

```
vectorization_fields {
    book_intro_vector = book_intro
    author_biography_vector  = author_biography
}
```

⑥ model：要使用的具体 embedding 模型。这取决于`embedding_model_provider`。例如，如果使用 OPENAI ，可以指定 `text-embedding-3-small`。

⑦ api_path：用于向 embedding 服务发送请求的API。根据提供商和所用模型的不同可能有所变化。通常由模型提供商提供。

⑧ oauth_path：用于向oauth服务发送请求的API,获取对应的认证信息。根据提供商和所用模型的不同可能有所变化。通常由模型提供商提供。

⑨ custom_config：`custom_config` 选项允许您为模型提供额外的自定义配置。这是一个映射，您可以在其中定义特定模型可能需要的各种设置。

⑩ custom_response_parse：`custom_response_parse` 选项允许您指定如何解析模型的响应。您可以使用 JsonPath 从响应中提取所需的特定数据。例如，使用 `$.data[*].embedding` 提取如下json中的 `embedding` 字段 值,获取 `List` 嵌套 `List` 的结果。JsonPath 的使用请参考

```
{
  "object": "list",
  "data": [
    {
      "object": "embedding",
      "index": 0,
      "embedding": [
        -0.006929283495992422,
        -0.005336422007530928,
        -0.00004547132266452536,
        -0.024047505110502243
      ]
    }
  ],
  "model": "text-embedding-3-small",
  "usage": {
    "prompt_tokens": 5,
    "total_tokens": 5
  }
}
```

⑪ custom_request_headers:`custom_request_headers` 选项允许您定义应包含在发送到模型 API 的请求中的自定义头信息。如果 API 需要标准头信息之外的额外头信息，例如授权令牌、内容类型等，这个选项会非常有用。

⑫ custom_request_body: `custom_request_body` 选项支持占位符：

- `${model}`：用于模型名称的占位符。
- `${input}`：用于确定输入值的占位符,同时根据 body value 的类型定义请求体请求类型。例如：`["${input}"]` -> ["input"] ( list)。

### 3. 示例

```
env {
  job.mode = "BATCH"
}

source {
  FakeSource {
    row.num = 5
    schema = {
      fields {
        book_id = "int"
        book_name = "string"
        book_intro = "string"
        author_biography = "string"
      }
    }
    rows = [
      {fields = [1, "To Kill a Mockingbird",
      "Set in the American South during the 1930s, To Kill a Mockingbird tells the story of young Scout Finch and her brother, Jem, who are growing up in a world of racial inequality and injustice. Their father, Atticus Finch, is a lawyer who defends a black man falsely accused of raping a white woman, teaching his children valuable lessons about morality, courage, and empathy.",
      "Harper Lee (1926–2016) was an American novelist best known for To Kill a Mockingbird, which won the Pulitzer Prize in 1961. Lee was born in Monroeville, Alabama, and the town served as inspiration for the fictional Maycomb in her novel. Despite the success of her book, Lee remained a private person and published only one other novel, Go Set a Watchman, which was written before To Kill a Mockingbird but released in 2015 as a sequel."
      ], kind = INSERT}
      {fields = [2, "1984",
      "1984 is a dystopian novel set in a totalitarian society governed by Big Brother. The story follows Winston Smith, a man who works for the Party rewriting history. Winston begins to question the Party’s control and seeks truth and freedom in a society where individuality is crushed. The novel explores themes of surveillance, propaganda, and the loss of personal autonomy.",
      "George Orwell (1903–1950) was the pen name of Eric Arthur Blair, an English novelist, essayist, journalist, and critic. Orwell is best known for his works 1984 and Animal Farm, both of which are critiques of totalitarian regimes. His writing is characterized by lucid prose, awareness of social injustice, opposition to totalitarianism, and support of democratic socialism. Orwell’s work remains influential, and his ideas have shaped contemporary discussions on politics and society."
      ], kind = INSERT}
      {fields = [3, "Pride and Prejudice",
      "Pride and Prejudice is a romantic novel that explores the complex relationships between different social classes in early 19th century England. The story centers on Elizabeth Bennet, a young woman with strong opinions, and Mr. Darcy, a wealthy but reserved gentleman. The novel deals with themes of love, marriage, and societal expectations, offering keen insights into human behavior.",
      "Jane Austen (1775–1817) was an English novelist known for her sharp social commentary and keen observations of the British landed gentry. Her works, including Sense and Sensibility, Emma, and Pride and Prejudice, are celebrated for their wit, realism, and biting critique of the social class structure of her time. Despite her relatively modest life, Austen’s novels have gained immense popularity, and she is considered one of the greatest novelists in the English language."
      ], kind = INSERT}
      {fields = [4, "The Great GatsbyThe Great Gatsby",
      "The Great Gatsby is a novel about the American Dream and the disillusionment that can come with it. Set in the 1920s, the story follows Nick Carraway as he becomes entangled in the lives of his mysterious neighbor, Jay Gatsby, and the wealthy elite of Long Island. Gatsby's obsession with the beautiful Daisy Buchanan drives the narrative, exploring themes of wealth, love, and the decay of the American Dream.",
      "F. Scott Fitzgerald (1896–1940) was an American novelist and short story writer, widely regarded as one of the greatest American writers of the 20th century. Born in St. Paul, Minnesota, Fitzgerald is best known for his novel The Great Gatsby, which is often considered the quintessential work of the Jazz Age. His works often explore themes of youth, wealth, and the American Dream, reflecting the turbulence and excesses of the 1920s."
      ], kind = INSERT}
      {fields = [5, "Moby-Dick",
      "Moby-Dick is an epic tale of obsession and revenge. The novel follows the journey of Captain Ahab, who is on a relentless quest to kill the white whale, Moby Dick, that once maimed him. Narrated by Ishmael, a sailor aboard Ahab’s ship, the story delves into themes of fate, humanity, and the struggle between man and nature. The novel is also rich with symbolism and philosophical musings.",
      "Herman Melville (1819–1891) was an American novelist, short story writer, and poet of the American Renaissance period. Born in New York City, Melville gained initial fame with novels such as Typee and Omoo, but it was Moby-Dick, published in 1851, that would later be recognized as his masterpiece. Melville’s work is known for its complexity, symbolism, and exploration of themes such as man’s place in the universe, the nature of evil, and the quest for meaning. Despite facing financial difficulties and critical neglect during his lifetime, Melville’s reputation soared posthumously, and he is now considered one of the great American authors."
      ], kind = INSERT}
    ]
    plugin_output = "fake"
  }
}

transform {
  Embedding {
    plugin_input = "fake"
    embedding_model_provider = QIANFAN
    model = bge_large_en
    api_key = xxxxxxxxxx
    secret_key = xxxxxxxxxx
    api_path = xxxxxxxxxx
    vectorization_fields {
        book_intro_vector = book_intro
        author_biography_vector  = author_biography
    }
    plugin_output = "embedding_output"
  }
}

sink {
  Assert {
      plugin_input = "embedding_output"


      rules =
        {
          field_rules = [
            {
              field_name = book_id
              field_type = int
              field_value = [
                {
                  rule_type = NOT_NULL
                }
              ]
            },
            {
              field_name = book_name
              field_type = string
              field_value = [
                {
                  rule_type = NOT_NULL
                }
              ]
            },
            {
              field_name = book_intro
              field_type = string
              field_value = [
                {
                  rule_type = NOT_NULL
                }
              ]
            },
            {
              field_name = author_biography
              field_type = string
              field_value = [
                {
                  rule_type = NOT_NULL
                }
              ]
            },
            {
              field_name = book_intro_vector
              field_type = float_vector
              field_value = [
                {
                  rule_type = NOT_NULL
                }
              ]
            },
            {
              field_name = author_biography_vector
              field_type = float_vector
              field_value = [
                {
                  rule_type = NOT_NULL
                }
              ]
            }
          ]
        }
    }
}
```



## 九、LLM

### 1. 定义

LLM 转换插件。利用大型语言模型 (LLM) 的强大功能来处理数据，方法是将数据发送到 LLM 并接收生成的结果。利用 LLM 的功能来标记、清理、丰富数据、执行数据推理等。

### 2. 属性

| 名称                   | 类型   | 是否必须 | 默认值     |
| ---------------------- | ------ | -------- | ---------- |
| model_provider         | enum   | yes      |            |
| output_data_type       | enum   | no       | String     |
| output_column_name     | string | no       | llm_output |
| prompt                 | string | yes      |            |
| inference_columns      | list   | no       |            |
| model                  | string | yes      |            |
| api_key                | string | yes      |            |
| api_path               | string | no       |            |
| custom_config          | map    | no       |            |
| custom_response_parse  | string | no       |            |
| custom_request_headers | map    | no       |            |
| custom_request_body    | map    | no       |            |

① model_provider：要使用的模型提供者。可用选项为: OPENAI,DOUBAO,DEEPSEEK,KIMIAI,MICROSOFT, ZHIPU, CUSTOM

> tips: 如果使用 Microsoft, 请确保 api_path 配置不能为空

② output_data_type：输出数据的数据类型。可用选项为: STRING,INT,BIGINT,DOUBLE,BOOLEAN. 默认值为 STRING。

③ output_column_name：自定义输出数据字段名称。自定义字段名称与现有字段名称相同时,将替换为`llm_output`。

④ prompt：发送到 LLM 的提示。此参数定义 LLM 将如何处理和返回数据，例如:

从源读取的数据是这样的表格:

| name          | age  |
| ------------- | ---- |
| Jia Fan       | 20   |
| Hailin Wang   | 20   |
| Eric          | 20   |
| Guangdong Liu | 20   |

我们可以使用以下prompt:

```
Determine whether someone is Chinese or American by their name
```

这将返回:

| name          | age  | llm_output |
| ------------- | ---- | ---------- |
| Jia Fan       | 20   | Chinese    |
| Hailin Wang   | 20   | Chinese    |
| Eric          | 20   | American   |
| Guangdong Liu | 20   | Chinese    |

⑤ inference_columns：`inference_columns`选项允许您指定应该将输入数据中的哪些列用作LLM的输入。默认情况下，所有列都将用作输入，例如：

```
transform {
  LLM {
    model_provider = OPENAI
    model = gpt-4o-mini
    api_key = sk-xxx
    inference_columns = ["name", "age"]
    prompt = "Determine whether someone is Chinese or American by their name"
  }
}
```

⑥ model

要使用的模型。不同的模型提供者有不同的模型。例如，OpenAI 模型可以是 `gpt-4o-mini`。 如果使用 OpenAI 模型，请参考 https://platform.openai.com/docs/models/model-endpoint-compatibility 文档的`/v1/chat/completions` 端点。

⑦ api_key

用于模型提供者的 API 密钥。 如果使用 OpenAI 模型，请参考 https://platform.openai.com/docs/api-reference/api-keys 文档的如何获取 API 密钥。

⑧ api_path

用于模型提供者的 API 路径。在大多数情况下，您不需要更改此配置。如果使用 API 代理的服务，您可能需要将其配置为代理的 API 地址。

⑨ custom_config

`custom_config` 选项允许您为模型提供额外的自定义配置。这是一个 Map，您可以在其中定义特定模型可能需要的各种设置。

⑩ custom_response_parse

`custom_response_parse` 选项允许您指定如何解析模型的响应。您可以使用 JsonPath 从响应中提取所需的特定数据。例如，使用 `$.choices[*].message.content` 提取如下json中的 `content` 字段 值。

```
{
  "id": "chatcmpl-9s4hoBNGV0d9Mudkhvgzg64DAWPnx",
  "object": "chat.completion",
  "created": 1722674828,
  "model": "gpt-4o-mini",
  "choices": [
    {
      "index": 0,
      "message": {
        "role": "assistant",
        "content": "[\"Chinese\"]"
      },
      "logprobs": null,
      "finish_reason": "stop"
    }
  ],
  "usage": {
    "prompt_tokens": 107,
    "completion_tokens": 3,
    "total_tokens": 110
  },
  "system_fingerprint": "fp_0f03d4f0ee",
  "code": 0,
  "msg": "ok"
}
```

⑪ custom_request_headers

`custom_request_headers` 选项允许您定义应包含在发送到模型 API 的请求中的自定义头信息。如果 API 需要标准头信息之外的额外头信息，例如授权令牌、内容类型等，这个选项会非常有用。

⑫ custom_request_body

`custom_request_body` 选项支持占位符：

- `${model}`：用于模型名称的占位符。
- `${input}`：用于确定输入值的占位符,同时根据 body value 的类型定义请求体请求类型。例如：`"${input}"` -> "input"。
- `${prompt}`：用于 LLM 模型提示的占位符。

### 3. tips

大模型API接口通常会有速率限制，可以配合Seatunnel的限速配置，已确保任务顺利运行。 Seatunnel限速配置,请参考[speed-limit](https://seatunnel.apache.org/zh-CN/docs/2.3.11/concept/speed-limit)了解详情

### 4. 示例

① OPENAI

通过 LLM 确定用户所在的国家。

```
env {
  parallelism = 1
  job.mode = "BATCH"
  read_limit.rows_per_second = 10
}

source {
  FakeSource {
    row.num = 5
    schema = {
      fields {
        id = "int"
        name = "string"
      }
    }
    rows = [
      {fields = [1, "Jia Fan"], kind = INSERT}
      {fields = [2, "Hailin Wang"], kind = INSERT}
      {fields = [3, "Tomas"], kind = INSERT}
      {fields = [4, "Eric"], kind = INSERT}
      {fields = [5, "Guangdong Liu"], kind = INSERT}
    ]
  }
}

transform {
  LLM {
    model_provider = OPENAI
    model = gpt-4o-mini
    api_key = sk-xxx
    prompt = "Determine whether someone is Chinese or American by their name"
  }
}

sink {
  console {
  }
}
```

② KIMIAI

通过 LLM 判断人名是否中国历史上的帝王

```
env {
  parallelism = 1
  job.mode = "BATCH"
  read_limit.rows_per_second = 10
}

source {
  FakeSource {
    row.num = 5
    schema = {
      fields {
        id = "int"
        name = "string"
      }
    }
    rows = [
      {fields = [1, "诸葛亮"], kind = INSERT}
      {fields = [2, "李世民"], kind = INSERT}
      {fields = [3, "孙悟空"], kind = INSERT}
      {fields = [4, "朱元璋"], kind = INSERT}
      {fields = [5, "乔治·华盛顿"], kind = INSERT}
    ]
  }
}

transform {
  LLM {
    model_provider = KIMIAI
    model = moonshot-v1-8k
    api_key = sk-xxx
    prompt = "判断是否是中国历史上的帝王"
    output_data_type = boolean
  }
}

sink {
  console {
  }
}
```

③ Customize the LLM model

```
env {
  job.mode = "BATCH"
}

source {
  FakeSource {
    row.num = 5
    schema = {
      fields {
        id = "int"
        name = "string"
      }
    }
    rows = [
      {fields = [1, "Jia Fan"], kind = INSERT}
      {fields = [2, "Hailin Wang"], kind = INSERT}
      {fields = [3, "Tomas"], kind = INSERT}
      {fields = [4, "Eric"], kind = INSERT}
      {fields = [5, "Guangdong Liu"], kind = INSERT}
    ]
    plugin_output = "fake"
  }
}

transform {
  LLM {
    plugin_input = "fake"
    model_provider = CUSTOM
    model = gpt-4o-mini
    api_key = sk-xxx
    prompt = "Determine whether someone is Chinese or American by their name"
    openai.api_path = "http://mockserver:1080/v1/chat/completions"
    custom_config={
            custom_response_parse = "$.choices[*].message.content"
            custom_request_headers = {
                Content-Type = "application/json"
                Authorization = "Bearer xxxxxxxx"            
            }
            custom_request_body ={
                model = "${model}"
                messages = [
                {
                    role = "system"
                    content = "${prompt}"
                },
                {
                    role = "user"
                    content = "${input}"
                }]
            }
        }
    plugin_output = "llm_output"
  }
}

sink {
  Assert {
    plugin_input = "llm_output"
    rules =
      {
        field_rules = [
          {
            field_name = llm_output
            field_type = string
            field_value = [
              {
                rule_type = NOT_NULL
              }
            ]
          }
        ]
      }
  }
}
```



## 十、FieldRename

### 1. 定义

用于重命名字段。

### 2. 属性

| name                    | type   | required | default value | Description                                                  |
| ----------------------- | ------ | -------- | ------------- | ------------------------------------------------------------ |
| convert_case            | string | no       |               | The case conversion type. The options can be `UPPER`, `LOWER` |
| prefix                  | string | no       |               | The prefix to be added to the field name                     |
| suffix                  | string | no       |               | The suffix to be added to the field name                     |
| replacements_with_regex | array  | no       |               | The array of replacement rules with regex. The replacement rule is a map with `replace_from` and `replace_to` fields. |

### 3. 示例

① 将字段转成大写

```
env {
    parallelism = 1
    job.mode = "STREAMING"
}

source {
    MySQL-CDC {
        plugin_output = "customers_mysql_cdc"
        
        username = "root"
        password = "123456"
        table-names = ["source.user_shop", "source.user_order"]
        base-url = "jdbc:mysql://localhost:3306/source"
    }
}

transform {
  FieldRename {
    plugin_input = "customers_mysql_cdc"
    plugin_output = "trans_result"
    
    convert_case = "UPPER"
    prefix = "F_"
    suffix = "_S"
    replacements_with_regex = [
      {
        replace_from = "create_time"
        replace_to = "SOURCE_CREATE_TIME"
      }
    ]
  }
}

sink {
  Jdbc {
    plugin_input = "trans_result"
    
    driver="oracle.jdbc.OracleDriver"
    url="jdbc:oracle:thin:@oracle-host:1521/ORCLCDB"
    user="myuser"
    password="mypwd"
    
    generate_sink_sql = true
    database = "ORCLCDB"
    table = "${database_name}.${table_name}"
    primary_keys = ["${primary_key}"]
    
    schema_save_mode = "CREATE_SCHEMA_WHEN_NOT_EXIST"
    data_save_mode = "APPEND_DATA"
  }
}
```

② 将字段转为小写

```
env {
    parallelism = 1
    job.mode = "STREAMING"
}

source {
  Oracle-CDC {
    plugin_output = "customers_oracle_cdc"
    
    base-url = "jdbc:oracle:thin:@localhost:1521/ORCLCDB"
    username = "dbzuser"
    password = "dbz"
    database-names = ["ORCLCDB"]
    schema-names = ["DEBEZIUM"]
    table-names = ["SOURCE.USER_SHOP", "SOURCE.USER_ORDER"]
  }
}

transform {
  FieldRename {
    plugin_input = "customers_oracle_cdc"
    plugin_output = "trans_result"
    
    convert_case = "LOWER"
    prefix = "f_"
    suffix = "_s"
    replacements_with_regex = [
      {
        replace_from = "CREATE_TIME"
        replace_to = "source_create_time"
      }
    ]
  }
}

sink {
  Jdbc {
    plugin_input = "trans_result"
    
    url = "jdbc:mysql://localhost:3306/test"
    driver = "com.mysql.cj.jdbc.Driver"
    user = "st_user_sink"
    password = "mysqlpw"
    
    generate_sink_sql = true
    database = "${schema_name}"
    table = "${table_name}"
    primary_keys = ["${primary_key}"]
    
    schema_save_mode = "CREATE_SCHEMA_WHEN_NOT_EXIST"
    data_save_mode = "APPEND_DATA"
  }
}
```



## 十一、TableRename

### 1. 定义

重命名表名

### 2. 属性

| name                    | type   | required | default value | Description                                                  |
| ----------------------- | ------ | -------- | ------------- | ------------------------------------------------------------ |
| convert_case            | string | no       |               | The case conversion type. The options can be `UPPER`, `LOWER` |
| prefix                  | string | no       |               | The prefix to be added to the table name                     |
| suffix                  | string | no       |               | The suffix to be added to the table name                     |
| replacements_with_regex | array  | no       |               | The array of replacement rules with regex. The replacement rule is a map with `replace_from` and `replace_to` fields. |

### 3. 示例

① 将表名转为大写

```
env {
    parallelism = 1
    job.mode = "STREAMING"
}

source {
    MySQL-CDC {
        plugin_output = "customers_mysql_cdc"
        
        username = "root"
        password = "123456"
        table-names = ["source.user_shop", "source.user_order"]
        base-url = "jdbc:mysql://localhost:3306/source"
    }
}

transform {
  TableRename {
    plugin_input = "customers_mysql_cdc"
    plugin_output = "trans_result"
    
    convert_case = "UPPER"
    prefix = "CDC_"
    suffix = "_TABLE"
    replacements_with_regex = [
      {
        replace_from = "user"
        replace_to = "U"
      }
    ]
  }
}

sink {
  Jdbc {
    plugin_input = "trans_result"
    
    driver="oracle.jdbc.OracleDriver"
    url="jdbc:oracle:thin:@oracle-host:1521/ORCLCDB"
    user="myuser"
    password="mypwd"
    
    generate_sink_sql = true
    database = "ORCLCDB"
    table = "${database_name}.${table_name}"
    primary_keys = ["${primary_key}"]
    
    schema_save_mode = "CREATE_SCHEMA_WHEN_NOT_EXIST"
    data_save_mode = "APPEND_DATA"
  }
}
```



## 十二、Replace

### 1. 定义

替换转换插件。检查给定字段中的字符串值，并用给定的替换项替换与给定字符串字面量或正则表达式匹配的字符串值的子字符串。

### 2. 属性

| 名称          | 类型    | 是否必须 | 默认值 |
| ------------- | ------- | -------- | ------ |
| replace_field | string  | yes      |        |
| pattern       | string  | yes      | -      |
| replacement   | string  | yes      | -      |
| is_regex      | boolean | no       | false  |
| replace_first | boolean | no       | false  |

① replace_field

需要替换的字段

② pattern

将被替换的旧字符串

③ replacement

用于替换的新字符串

④ is_regex

使用正则表达式进行字符串匹配

⑤ replace_first

是否替换第一个匹配字符串。仅在 `is_regex = true` 时使用。

### 3. 示例

源端数据读取的表格如下：

| name     | age  | card |
| -------- | ---- | ---- |
| Joy Ding | 20   | 123  |
| May Ding | 20   | 123  |
| Kin Dom  | 20   | 123  |
| Joy Dom  | 20   | 123  |

我们想要将 `name` 字段中的字符 ``替换为 `_`。然后我们可以添加一个 `Replace` 转换，像这样：

```
transform {
  Replace {
    plugin_input = "fake"
    plugin_output = "fake1"
    replace_field = "name"
    pattern = " "
    replacement = "_"
    is_regex = true
  }
}
```

那么结果表 `fake1` 中的数据将会更新为：

| name     | age  | card |
| -------- | ---- | ---- |
| Joy_Ding | 20   | 123  |
| May_Ding | 20   | 123  |
| Kin_Dom  | 20   | 123  |
| Joy_Dom  | 20   | 123  |

## 十三、RowKindExtractor

### 1. 定义

将CDC Row 转换为 Append only Row, 转换后的行扩展了RowKind字段
Example:
CDC row: -D 1, test1, test2
transformed Row: +I 1,test1,test2,DELETE

### 2. 属性

| name              | type   | required | default value |
| ----------------- | ------ | -------- | ------------- |
| custom_field_name | string | yes      | row_kind      |
| transform_type    | enum   | yes      | SHORT         |

① custom_field_name

RowKind列的自定义名

② transform_type

格式化RowKind值 , 配置为 `SHORT` 或 `FULL`

`SHORT` : +I, -U , +U, -D `FULL` : INSERT, UPDATE_BEFORE, UPDATE_AFTER , DELETE

### 3. 示例

```

env {
    parallelism = 1
    job.mode = "BATCH"
}

source {
    FakeSource {
        schema = {
            fields {
                pk_id = bigint
                name = string
                score = int
            }
            primaryKey {
                name = "pk_id"
                columnNames = [pk_id]
            }
        }
        rows = [
            {
                kind = INSERT
                fields = [1, "A", 100]
            },
            {
                kind = INSERT
                fields = [2, "B", 100]
            },
            {
                kind = INSERT
                fields = [3, "C", 100]
            },
            {
                kind = INSERT
                fields = [4, "D", 100]
            },
            {
                kind = UPDATE_BEFORE
                fields = [1, "A", 100]
            },
            {
                kind = UPDATE_AFTER
                fields = [1, "F", 100]
            }
            {
                kind = UPDATE_BEFORE
                fields = [2, "B", 100]
            },
            {
                kind = UPDATE_AFTER
                fields = [2, "G", 100]
            },
            {
                kind = DELETE
                fields = [3, "C", 100]
            },
            {
                kind = DELETE
                fields = [4, "D", 100]
            }
        ]
    }
}

transform {
  RowKindExtractor {
        custom_field_name = "custom_name"
        transform_type = FULL
        plugin_output = "trans_result"
    }
}

sink {
  Console {
    plugin_input = "custom_name"
  }
}
```



## 十四、Split

### 1. 定义

拆分转换插件。拆分一个字段为多个字段。

### 2. 属性

| 名称          | 类型   | 是否必须 | 默认值 |
| ------------- | ------ | -------- | ------ |
| separator     | string | yes      |        |
| split_field   | string | yes      |        |
| output_fields | array  | yes      |        |

① separator

拆分内容的分隔符

② split_field

需要拆分的字段

③ output_fields

拆分后的结果字段

### 3. 示例

源端数据读取的表格如下：

| name     | age  | card |
| -------- | ---- | ---- |
| Joy Ding | 20   | 123  |
| May Ding | 20   | 123  |
| Kin Dom  | 20   | 123  |
| Joy Dom  | 20   | 123  |

我们想要将 `name` 字段拆分为 `first_name` 和 `second_name`，我们可以像这样添加 `Split` 转换：

```
transform {
  Split {
    plugin_input = "fake"
    plugin_output = "fake1"
    separator = " "
    split_field = "name"
    output_fields = [first_name, second_name]
  }
}
```

那么结果表 `fake1` 中的数据将会像这样：

| name     | age  | card | first_name | last_name |
| -------- | ---- | ---- | ---------- | --------- |
| Joy Ding | 20   | 123  | Joy        | Ding      |
| May Ding | 20   | 123  | May        | Ding      |
| Kin Dom  | 20   | 123  | Kin        | Dom       |
| Joy Dom  | 20   | 123  | Joy        | Dom       |



## 十五、Sql

### 1. 定义

SQL转换插件。使用 SQL 来转换给定的输入行。SQL 转换使用内存中的 SQL 引擎，我们可以通过 SQL 函数和 SQL 引擎的能力来实现转换任务。

### 2. 属性

| 名称          | 类型   | 是否必须 | 默认值 |
| ------------- | ------ | -------- | ------ |
| plugin_input  | string | yes      | -      |
| plugin_output | string | yes      | -      |
| query         | string | yes      | -      |

① plugin_input

源表名称，查询 SQL 表名称必须与此字段匹配。

② query

查询 SQL，它是一个简单的 SQL，支持基本的函数和条件过滤操作。但是，复杂的 SQL 尚不支持，包括：多源表/行连接和聚合操作等。

查询表达式可以是`select [table_name.]column_a`，这时会去查询列为`column_a`的列，`table_name`为可选项 也可以是`select c_row.c_inner_row.column_b`，这时会去查询列`c_row`下的`c_inner_row`的`column_b`。**嵌套结构查询中，不能存在`table_name`**

### 3. 示例

源端数据读取的表格如下：

| id   | name     | age  |
| ---- | -------- | ---- |
| 1    | Joy Ding | 20   |
| 2    | May Ding | 21   |
| 3    | Kin Dom  | 24   |
| 4    | Joy Dom  | 22   |

我们使用 SQL 查询来转换源数据，类似这样：

```
transform {
  Sql {
    plugin_input = "fake"
    plugin_output = "fake1"
    query = "select id, concat(name, '_') as name, age+1 as age from dual where id>0"
  }
}
```

那么结果表 `fake1` 中的数据将会更新为：

| id   | name      | age  |
| ---- | --------- | ---- |
| 1    | Joy Ding_ | 21   |
| 2    | May Ding_ | 22   |
| 3    | Kin Dom_  | 25   |
| 4    | Joy Dom_  | 23   |



#### 嵌套结构查询

例如你的上游数据结构是这样：

```
source {
  FakeSource {
    plugin_output = "fake"
    row.num = 100
    string.template = ["innerQuery"]
    schema = {
      fields {
        name = "string"
        c_date = "date"
        c_row = {
          c_inner_row = {
            c_inner_int = "int"
            c_inner_string = "string"
            c_inner_timestamp = "timestamp"
            c_map_1 = "map<string, string>"
            c_map_2 = "map<string, map<string,string>>"
          }
          c_string = "string"
        }
      }
    }
  }
}
```

那么下列所有的查询表达式都是有效的

```
select 
name,
c_date,
c_row,
c_row.c_inner_row,
c_row.c_string,
c_row.c_inner_row.c_inner_int,
c_row.c_inner_row.c_inner_string,
c_row.c_inner_row.c_inner_timestamp,
c_row.c_inner_row.c_map_1,
c_row.c_inner_row.c_map_1.some_key
```

但是这个查询语句是无效的

```
select 
c_row.c_inner_row.c_map_2.some_key.inner_map_key
```

当查询map结构时，map结构应该为最后一个数据结构，不能查询嵌套map

#### 作业配置示例

```
env {
  job.mode = "BATCH"
}

source {
  FakeSource {
    plugin_output = "fake"
    row.num = 100
    schema = {
      fields {
        id = "int"
        name = "string"
        age = "int"
      }
    }
  }
}

transform {
  Sql {
    plugin_input = "fake"
    plugin_output = "fake1"
    query = "select id, concat(name, '_') as name, age+1 as age from dual where id>0"
  }
}

sink {
  Console {
    plugin_input = "fake1"
  }
}
```



## 十六、TableMerge

### 1. 定义

表合并插件，用于分库分表合并为一个表。

### 2. 属性

| name     | type   | required | default value | Description            |
| -------- | ------ | -------- | ------------- | ---------------------- |
| database | string | no       |               | 指定新的 database 名称 |
| schema   | string | no       |               | 指定新的 schema 名称   |
| table    | string | yes      |               | 指定新的 table 名称    |

### 3. 示例

① 合并分库分表为一个表

```
env {
    parallelism = 1
    job.mode = "STREAMING"
}

source {
    MySQL-CDC {
        plugin_output = "customers_mysql_cdc"
        
        username = "root"
        password = "123456"
        table-names = ["source.user_1", "source.user_2", "source.shop"]
        base-url = "jdbc:mysql://localhost:3306/source"
    }
}

transform {
  TableMerge {
    plugin_input = "customers_mysql_cdc"
    plugin_output = "trans_result"
    
    table_match_regex = "source.user_.*"
    database = "user_db"
    table = "user_all"
  }
}

sink {
  Jdbc {
    plugin_input = "trans_result"
    
    driver="com.mysql.cj.jdbc.Driver"
    url="jdbc:mysql://localhost:3306/sink"
    user="myuser"
    password="mypwd"
    
    generate_sink_sql = true
    database = "${database_name}"
    table = "${table_name}"
    primary_keys = ["${primary_key}"]
    
    schema_save_mode = "CREATE_SCHEMA_WHEN_NOT_EXIST"
    data_save_mode = "APPEND_DATA"
  }
}
```

