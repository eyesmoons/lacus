<p align="center">
  <img src="https://img.shields.io/badge/Release-V1.5.0-green.svg" alt="Downloads">
  <img src="https://img.shields.io/badge/JDK-1.8+-green.svg" alt="Build Status">
  <img src="https://img.shields.io/badge/license-MIT-blue.svg" alt="Build Status">
   <img src="https://img.shields.io/badge/Spring%20Boot-2.7.1-blue.svg" alt="Downloads">
 </p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">Lacus v1.0.0</h1>
<h4 align="center">基于SpringBoot+Vue3前后端分离的开源大数据中台项目</h4>
<p align="center">
</p>



## 平台简介

Lacus是一个开源大数据中台项目，致力于让数据接入变得更简单、好用。

* 前端采用Vue3、Element UI。对应前端仓库 [lacus-ui](https://github.com/eyesmoons/lacus-ui)
* 后端采用Spring Boot、Spring Security、Redis & Jwt、Mybatis Plus、MySql。
* 权限认证使用Jwt。
* 支持加载动态权限菜单。

## 使用

### 开发环境

- JDK
- Mysql
- Redis
- Node.js

### 技术栈

| 技术             | 说明              | 版本                |
|----------------|-----------------|-------------------|
| `springboot`   | Java项目必备框架      | 2.7               |
| `druid`        | alibaba数据库连接池   | 1.2.8             |
| `swagger`      | 文档生成            | 3.0.0             |
| `mybatis-plus` | 数据库框架           | 3.5.2             |
| `hutool`       | 国产工具包（简单易用）     | 3.5.2             |
| `mockito`      | 单元测试模拟          | 1.10.19           |
| `guava`        | 谷歌工具包（提供简易缓存实现） | 31.0.1-jre        |
| `junit`        | 单元测试            | 1.10.19           |
| `h2`           | 内存数据库           | 1.10.19           |
| `jackson`      | 比较安全的Json框架     | follow springboot |

### 启动说明

#### 前置准备： 下载前后端代码

```
git clone https://github.com/eyesmoons/lacus
git clone https://github.com/eyesmoons/lacus-ui
```

#### 安装好Mysql和Redis

#### 后端启动
```
1. 生成所需的数据库表
找到后端项目根目录下的sql目录中的lacus_xxxxx.sql脚本文件。 导入到你新建的数据库中。

2. 在core模块底下，找到resource目录下的application-dev.yml文件
配置数据库以及Redis的 地址、端口、账号密码

3. 在根目录执行mvn install

4. 找到lacus-admin模块中的LacusApplication启动类，直接启动即可
```

#### 前端启动
```
1. npm install

2. npm run dev

3. 当出现以下字样时即为启动成功

vite v2.6.14 dev server running at:

> Local: http://127.0.0.1:80/

ready in 4376ms.

```
> 对于想要尝试全栈项目的前端人员，这边提供更简便的后端启动方式，无需配置Mysql和Redis直接启动
#### 无Mysql/Redis 后端启动
```
1. 找到agilboot-admin模块下的resource文件中的application.yml文件

2. 配置以下两个值
spring.profiles.active: basic,dev
改为
spring.profiles.active: basic,test

lacus.embedded.mysql: false
lacus.embedded.redis: false
改为
lacus.embedded.mysql: true
lacus.embedded.redis: true

3. 找到lacus-admin模块中的LacusApplication启动类，直接启动即可
```
## 系统内置功能

| 功能    | 描述                             |
|-------|--------------------------------|
| 元数据模块  | 根据源库表管理所有元数据信息      |
| 数据服务模块 | 通过API接口,对外提供获取数据能力|
| 数据同步模块| 通过可视化配置，一键部署接入任务|
| 数据质量模块| 敬请期待...|
| 部门管理  | 配置系统组织机构（公司、部门、小组），树结构展现支持数据权限 |
| 岗位管理  | 配置系统用户所属担任职务                   |
| 菜单管理  | 配置系统菜单、操作权限、按钮权限标识等，本地缓存提供性能   |
| 角色管理  | 角色菜单权限分配、设置角色按机构进行数据范围权限划分    |
| 参数管理  | 对系统动态配置常用参数                    |
| 通知公告  | 系统通知公告信息发布维护                   |
 操作日志  | 系统正常操作日志记录和查询；系统异常信息日志记录和查询   |
| 登录日志  | 系统登录日志记录查询包含登录异常                   |
| 在线用户  | 当前系统中活跃用户状态监控                   |
| 服务监控  | 监视当前系统CPU、内存、磁盘、堆栈等相关信息             |
| 缓存监控  | 对系统的缓存信息查询，命令统计等                   |
| 连接池监视  | 监视当前系统数据库连接池状态，可进行分析SQL找出系统性能瓶颈     |

## 工程结构

``` 
lacus
├── lacus-admin -- 管理后台接口模块
│
├── lacus-api -- 开放接口模块
│
├── lacus-common -- 工具模块
│
├── lacus-core -- 核心基础模块
│
├── lacus-domain -- 业务模块
├    ├── user -- 用户模块（举例）
├         ├── command -- 命令参数接收模型（命令）
├         ├── dto -- 返回数据类
├         ├── model -- 领域模型类
├         ├── query -- 查询参数模型（查询）
│         ├────── UserApplicationService -- 应用服务（事务层，操作领域模型类完成业务逻辑）
│
├── lacus-dao -- 数据映射模块（仅包含数据相关逻辑）
├    ├── entiy -- 实体类
├    ├── enums -- 数据相关枚举
├    ├── mapper -- DAO
├    ├── query -- 封装查询对象
├    ├── result -- 封装多表查询对象
└──  └── service -- 服务层
```

## 注意事项
- IDEA会自动将.properties文件的编码设置为ISO-8859-1,请在Settings > Editor > File Encodings > Properties Files > 设置为UTF-8
- 请导入统一的代码格式化模板（Google）: Settings > Editor > Code Style > Java > 设置按钮 > import schema > 选择项目根目录下的GoogleStyle.xml文件
- 如需要生成新的表，请使用CodeGenerator类进行生成。
  - 填入数据库地址，账号密码，库名。然后填入所需的表名执行代码即可。
- 项目基础环境搭建，请参考docker目录下的指南搭建。保姆级启动说明：