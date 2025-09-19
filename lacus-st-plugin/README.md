# ST组件框架

基于SPI机制的ST（Stream Transform）组件框架，支持动态加载和配置组件，用于构建数据流处理管道。

## 框架特性

- **SPI自动发现**: 基于Java SPI机制，项目启动时自动发现和加载所有组件
- **注解驱动**: 使用注解定义组件和字段属性，简化开发
- **动态表单**: 根据组件字段配置自动生成前端表单配置
- **类型安全**: 强类型接口设计，确保组件实现的正确性
- **扩展性强**: 支持source、sink、transform三种组件类型

## 项目结构

```
lacus-st-plugin/
├── lacus-st-source/          # 数据源组件模块
├── lacus-st-sink/            # 数据输出组件模块
├── lacus-st-transform/       # 数据转换组件模块
└── README.md
```

## 核心组件

### 1. 注解系统

#### @StComponent
用于标识ST组件，定义组件的基本信息：
```java
@StComponent(
    type = StComponent.ComponentType.SOURCE,
    name = "mysql_source",
    displayName = "MySQL数据源",
    description = "从MySQL数据库读取数据的组件",
    version = "1.0.0",
    author = "lacus"
)
```

#### @StField
用于定义组件字段属性，支持前端动态构建表单：
```java
@StField(
    tag = "基本信息",
    required = true,
    enName = "host",
    cnName = "主机地址",
    placeHolder = "请输入MySQL服务器地址",
    formType = StField.FormType.TEXT
)
private String host;
```

### 2. 接口系统

#### StComponentInterface
所有ST组件的基接口，定义通用方法：
- `initialize()`: 初始化组件
- `start()`: 启动组件
- `stop()`: 停止组件
- `validateConfig()`: 验证配置
- `getMetadata()`: 获取元数据

#### 专用接口
- `StSourceInterface`: 数据源组件接口
- `StSinkInterface`: 数据输出组件接口
- `StTransformInterface`: 数据转换组件接口

### 3. 抽象基类

- `AbstractStComponent`: 组件通用功能实现
- `AbstractStSource`: 数据源组件基类
- `AbstractStSink`: 数据输出组件基类
- `AbstractStTransform`: 数据转换组件基类

## 使用指南

### 1. 创建数据源组件

```java
@StComponent(
    type = StComponent.ComponentType.SOURCE,
    name = "mysql_source",
    displayName = "MySQL数据源",
    description = "从MySQL数据库读取数据"
)
@AutoService(StComponentInterface.class)
public class MysqlSource extends AbstractStSource {
    
    @StField(
        tag = "基本信息",
        required = true,
        enName = "host",
        cnName = "主机地址",
        formType = StField.FormType.TEXT
    )
    private String host;
    
    @StField(
        tag = "基本信息",
        required = true,
        enName = "port",
        cnName = "端口号",
        defaultValue = "3306",
        formType = StField.FormType.NUMBER
    )
    private Integer port;
    
    // 实现抽象方法
    @Override
    protected Object doReadData() {
        // 实现数据读取逻辑
        return null;
    }
    
    @Override
    protected Object doReadBatchData(int batchSize) {
        // 实现批量数据读取逻辑
        return null;
    }
    
    @Override
    protected boolean doCheckConnection() {
        // 实现连接检查逻辑
        return true;
    }
}
```

### 2. 注册组件

在 `META-INF/services/com.lacus.st.interfaces.StComponentInterface` 文件中注册组件：

```
com.lacus.st.source.JdbcSource
```

### 3. 使用组件加载器

```java
// 获取组件实例
StComponentInterface component = StComponentLoader.getComponent("mysql_source");

// 创建新组件实例
StComponentInterface newComponent = StComponentLoader.createComponent("mysql_source");

// 获取组件元数据
Map<String, Object> metadata = StComponentLoader.getComponentMetadata("mysql_source");

// 获取所有组件
Set<String> allComponents = StComponentLoader.getAllComponentNames();
```

### 4. 前端表单构建

```java
// 生成表单配置
Map<String, Object> formConfig = FormBuilder.buildFormConfig("mysql_source");

// 生成验证规则
Map<String, Object> validationRules = FormBuilder.buildValidationRules("mysql_source");

// 生成默认值
Map<String, Object> defaultValues = FormBuilder.buildDefaultValues("mysql_source");
```

## API接口

框架提供了REST API接口用于组件管理：

- `GET /api/st/components/list` - 获取所有组件列表
- `GET /api/st/components/list/{type}` - 根据类型获取组件列表
- `GET /api/st/components/{componentName}` - 获取组件详情
- `GET /api/st/components/{componentName}/fields` - 获取组件字段配置
- `POST /api/st/components/{componentName}/validate` - 验证组件配置
- `GET /api/st/components/stats` - 获取组件统计信息

## 字段属性说明

### 表单类型 (formType)

- `text`: 文本输入框
- `text_area`: 多行文本输入框
- `number`: 数字输入框
- `positive_number`: 正数输入框
- `date`: 日期选择器
- `single_select`: 单选下拉框
- `multi_select`: 多选下拉框
- `radio`: 单选按钮
- `checkbox`: 复选框
- `password`: 密码输入框

### 字典类型 (dictType)

- `none`: 无字典
- `url`: URL数据源
- `enum`: 枚举值

## 扩展开发

### 1. 添加新的组件类型

1. 创建新的接口继承 `StComponentInterface`
2. 创建对应的抽象基类
3. 在 `StComponent.ComponentType` 枚举中添加新类型

### 2. 添加新的表单类型

1. 在 `StField.FormType` 枚举中添加新类型
2. 在前端表单构建器中处理新类型

### 3. 添加新的字典类型

1. 在 `StField.DictType` 枚举中添加新类型
2. 在数据源处理逻辑中实现新类型

## 注意事项

1. 所有组件类必须添加 `@StComponent` 和 `@AutoService` 注解
2. 组件字段必须使用 `@StField` 注解定义属性
3. 必须在SPI配置文件中注册组件类
4. 组件类必须有无参构造函数
5. 字段类型必须是基本类型或常用类型，便于序列化

## 示例项目

参考 `MysqlSource` 类了解完整的组件实现示例。
