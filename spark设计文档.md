# 说明

spark数据开发分为spark任务定义和spark任务实例两部分，spark任务定义将管理spark任务的配置；每运行一次任务都会产生一个实例，以便记录历史执行记录。

# 一、表结构

## 1. 任务表

```
CREATE TABLE `spark_job` (
  `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `job_name` varchar(100) NOT NULL COMMENT '任务名称',
  `app_id` varchar(100) DEFAULT NULL COMMENT 'application id',
  `job_type` varchar(100) DEFAULT NULL COMMENT '任务类型：JAR, BATCH_SQL',
  `deploy_mode` varchar(100) NOT NULL COMMENT '部署模式：local, yarn-client, yarn-cluster, k8s-client, k8s-cluster',
  `driver_cores` int NOT NULL DEFAULT '1' COMMENT 'driver核心数',
  `driver_memory` int NOT NULL DEFAULT '1' COMMENT 'driver内存',
  `num_executors` int NOT NULL DEFAULT '1' COMMENT 'executors个数',
  `executor_cores` int NOT NULL DEFAULT '1' COMMENT '每个executor的核心数',
  `executor_memory` int NOT NULL DEFAULT '1' COMMENT '每个executor的内存',
  `other_spark_conf` longtext DEFAULT NULL COMMENT '其他配置',
  `main_jar_path` int DEFAULT NULL COMMENT '主jar包路径',
  `main_class_name` varchar(100) DEFAULT NULL COMMENT '主类名',
  `main_args` varchar(200) DEFAULT NULL COMMENT '主类参数',
  `sql_content` longtext DEFAULT NULL COMMENT 'spark sql',
  `queue` varchar(100) DEFAULT NULL COMMENT '队列',
  `namespace` varchar(100) DEFAULT NULL COMMENT 'k8s命名空间',
  `env_id` bigint DEFAULT NULL COMMENT '环境id',
  `job_status` varchar(100) DEFAULT NULL COMMENT '任务状态：1 提交成功 ，2 运行中，3 成功，4 失败',
  `remark` varchar(100) DEFAULT NULL COMMENT '任务描述',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='spark任务表';
```



## 2. 任务实例表

```
CREATE TABLE `spark_job_instance` (
  `instance_id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `job_id` bigint NOT NULL COMMENT '任务id',
  `instance_name` varchar(100) NOT NULL COMMENT '任务实例名称',
  `deploy_mode` varchar(100) NOT NULL COMMENT '部署模式：local, yarn-client, yarn-cluster, k8s-client, k8s-cluster',
  `app_id` varchar(100) DEFAULT NULL COMMENT 'application id',
  `job_type` varchar(100) DEFAULT NULL COMMENT '任务类型：JAR, BATCH_SQL',
  `job_script` varchar(100) DEFAULT NULL COMMENT '任务执行脚本',
  `sql_content` longtext DEFAULT NULL COMMENT 'spark sql',
  `env_id` bigint DEFAULT NULL COMMENT '环境id',
  `job_status` varchar(100) DEFAULT NULL COMMENT '任务状态：1 提交成功 ，2 运行中，3 成功，4 失败',
  `submit_time` datetime NOT NULL COMMENT '提交时间',
  `finished_time` datetime NOT NULL COMMENT '完成时间',
  `error_info` varchar(5000) NOT NULL COMMENT '错误信息',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`instance_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='spark任务实例表';
```



# 二、代码结构

```
lacus
├── lacus-admin  -- api接口模块
├── lacus-common -- 公共模块
├── lacus-core  -- 核心基础模块
├── lacus-dao  -- 数据库交互模块
├── lacus-dist  -- 打包模块
├── lacus-domain  -- 业务领域模块
├── lacus-service  -- 服务层
├── lacus-rtc-engine  -- 实时采集引擎
└── sql  -- sql脚本
```



## 1. controller

```
lacus-admin/src/main/java/com/lacus/admin/controller/spark/SparkJobController.java
lacus-admin/src/main/java/com/lacus/admin/controller/spark/SparkJobInstanceController.java
```

## 2. domain

```
lacus-domain/src/main/java/com/lacus/domain/spark/job/SparkJobBusiness.java
lacus-domain/src/main/java/com/lacus/domain/spark/instance/SparkJobInstanceBusiness.java
```

## 3. service

```
lacus-service/src/main/java/com/lacus/service/spark/ISparkJobService.java
lacus-service/src/main/java/com/lacus/service/spark/ISparkJobInstanceService.java
lacus-service/src/main/java/com/lacus/service/spark/impl/SparkJobServiceImpl.java
lacus-service/src/main/java/com/lacus/service/spark/impl/SparkJobInstanceImpl.java
```

## 4. dao

```
lacus-dao/src/main/java/com/lacus/dao/spark/entity/SparkJobEntity.java
lacus-dao/src/main/java/com/lacus/dao/spark/entity/SparkJobInstanceEntity.java
lacus-dao/src/main/java/com/lacus/dao/spark/mapper/SparkJobMapper.java
lacus-dao/src/main/java/com/lacus/dao/spark/mapper/SparkJobInstanceMapper.java
```

## 三、接口

以下的spark sql和spark jar任务展示的字段有所不同，区别是：

spark sql独有字段：sqlContent

spark jar独有字段：mainJar，mainClass，mainArgs

部署模式的区别：

yarn独有字段：queue

k8s独有字段：namespace

## 1. 任务分页列表

展示的列：job_name，app_id，job_type，deploy_mode，env_id，job_status，remark，create_time

搜索列：job_name模糊搜索，job_type精确搜索，deploy_mode精确搜索，job_status精确搜索，create_time范围搜索

## 2. 新建spark sql任务

## 3. 新建spark jar类型任务

## 4. 编辑spark sql类型任务

## 5. 编辑spark jar任务

## 6. 查看任务详情

根据id查看任务详情，jar和sql的显示的字段不一样

## 7. 删除spark任务

根据id删除spark任务

## 8. 启动任务

使用sparkLauncher

## 9. 停止任务

使用sparkLauncher

## 10. 任务实例分页列表

除了展示任务实例信息，也需要展示任务的名称，任务类型。

## 11. 任务实例详情

除了展示任务实例信息，也需要展示任务的名称，任务类型。



