/*
 Navicat Premium Data Transfer

 Source Server Type    : MySQL
 Source Server Version : 50744 (5.7.44-log)
 Source Host           : 120.46.65.219:3306
 Source Schema         : lacus

 Target Server Type    : MySQL
 Target Server Version : 50744 (5.7.44-log)
 File Encoding         : 65001

 Date: 14/04/2024 18:49:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for data_sync_column_mapping
-- ----------------------------
DROP TABLE IF EXISTS `data_sync_column_mapping`;
CREATE TABLE `data_sync_column_mapping` (
  `column_mapping_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `job_id` varchar(64) NOT NULL COMMENT '任务ID',
  `source_column_id` bigint(20) NOT NULL COMMENT '输入源表字段ID',
  `sink_column_id` bigint(20) NOT NULL COMMENT '输出源表字段ID',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`column_mapping_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字段映射信息';

-- ----------------------------
-- Table structure for data_sync_job
-- ----------------------------
DROP TABLE IF EXISTS `data_sync_job`;
CREATE TABLE `data_sync_job` (
  `job_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `job_name` varchar(100) NOT NULL DEFAULT '' COMMENT '任务名称',
  `catalog_id` varchar(64) NOT NULL COMMENT '分组ID',
  `source_datasource_id` bigint(20) NOT NULL COMMENT '输入源ID',
  `sink_datasource_id` bigint(20) NOT NULL COMMENT '输出源ID',
  `job_manager` bigint(20) DEFAULT NULL COMMENT 'jobManager内存，单位为GB',
  `task_manager` bigint(20) DEFAULT NULL COMMENT 'taskManager内存，单位为GB',
  `window_size` int(11) NOT NULL COMMENT '窗口大小(秒)',
  `max_size` int(11) NOT NULL COMMENT '最大数据量(MB)',
  `max_count` int(11) NOT NULL COMMENT '最大数据条数(万条)',
  `remark` varchar(500) NOT NULL DEFAULT '' COMMENT '任务描述',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据采集任务主表';

-- ----------------------------
-- Table structure for data_sync_job_catalog
-- ----------------------------
DROP TABLE IF EXISTS `data_sync_job_catalog`;
CREATE TABLE `data_sync_job_catalog` (
  `catalog_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `catalog_name` varchar(50) NOT NULL DEFAULT '' COMMENT '分组名称',
  `job_manager` bigint(20) DEFAULT NULL COMMENT 'jobManager内存，单位为GB',
  `task_manager` bigint(20) DEFAULT NULL COMMENT 'taskManager内存，单位为GB',
  `remark` varchar(300) NOT NULL DEFAULT '' COMMENT '分组描述',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`catalog_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据采集任务分组表';

-- ----------------------------
-- Table structure for data_sync_job_instance
-- ----------------------------
DROP TABLE IF EXISTS `data_sync_job_instance`;
CREATE TABLE `data_sync_job_instance` (
  `instance_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `job_id` bigint(20) NOT NULL COMMENT '任务ID',
  `instance_name` varchar(150) NOT NULL DEFAULT '' COMMENT '实例名称',
  `application_id` varchar(100) DEFAULT NULL COMMENT 'flink任务ID',
  `flink_job_id` varchar(100) DEFAULT NULL COMMENT 'flink任务ID',
  `job_script` longtext COMMENT '任务脚本',
  `sync_type` varchar(50) DEFAULT NULL COMMENT '同步方式：INITIAL，TIMESTAMP，RESUME',
  `time_stamp` varchar(100) DEFAULT NULL COMMENT '指定时间戳',
  `submit_time` datetime DEFAULT NULL COMMENT '任务提交时间',
  `finished_time` datetime DEFAULT NULL COMMENT '任务结束时间',
  `save_point` varchar(200) DEFAULT NULL COMMENT 'savepoint地址',
  `status` varchar(10) NOT NULL DEFAULT '1' COMMENT '任务状态 RUNNING, KILL, FAILED',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`instance_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='数据采集实例';

-- ----------------------------
-- Table structure for data_sync_sink_column
-- ----------------------------
DROP TABLE IF EXISTS `data_sync_sink_column`;
CREATE TABLE `data_sync_sink_column` (
  `sink_column_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `job_id` varchar(64) NOT NULL COMMENT '任务ID',
  `sink_table_id` bigint(20) NOT NULL COMMENT '输出源表ID',
  `sink_column_name` varchar(100) NOT NULL COMMENT '输出源字段名称',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`sink_column_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='输出源字段信息';

-- ----------------------------
-- Table structure for data_sync_sink_table
-- ----------------------------
DROP TABLE IF EXISTS `data_sync_sink_table`;
CREATE TABLE `data_sync_sink_table` (
  `sink_table_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `job_id` varchar(64) NOT NULL COMMENT '任务ID',
  `sink_db_name` varchar(100) NOT NULL COMMENT '输出源库名称',
  `sink_table_name` varchar(100) NOT NULL COMMENT '输出源表名称',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`sink_table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='输出源表信息';

-- ----------------------------
-- Table structure for data_sync_source_column
-- ----------------------------
DROP TABLE IF EXISTS `data_sync_source_column`;
CREATE TABLE `data_sync_source_column` (
  `source_column_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `job_id` varchar(64) NOT NULL COMMENT '任务ID',
  `source_table_id` bigint(20) NOT NULL COMMENT '输入源表ID',
  `source_column_name` varchar(100) NOT NULL COMMENT '输入源字段名称',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`source_column_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='输入源字段信息';

-- ----------------------------
-- Table structure for data_sync_source_table
-- ----------------------------
DROP TABLE IF EXISTS `data_sync_source_table`;
CREATE TABLE `data_sync_source_table` (
  `source_table_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `job_id` varchar(64) NOT NULL COMMENT '任务ID',
  `source_db_name` varchar(100) NOT NULL COMMENT '输入源库名称',
  `source_table_name` varchar(100) NOT NULL COMMENT '输入源表名称',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`source_table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='输入源表信息';

-- ----------------------------
-- Table structure for data_sync_table_mapping
-- ----------------------------
DROP TABLE IF EXISTS `data_sync_table_mapping`;
CREATE TABLE `data_sync_table_mapping` (
  `table_mapping_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `job_id` varchar(64) NOT NULL COMMENT '任务ID',
  `source_table_id` bigint(20) NOT NULL COMMENT '输入源表ID',
  `sink_table_id` bigint(20) NOT NULL COMMENT '输出源表ID',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`table_mapping_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表映射信息';

-- ----------------------------
-- Table structure for meta_column
-- ----------------------------
DROP TABLE IF EXISTS `meta_column`;
CREATE TABLE `meta_column` (
  `column_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `table_id` bigint(20) NOT NULL COMMENT '表id',
  `column_name` varchar(100) DEFAULT NULL COMMENT '字段名称',
  `data_type` varchar(100) DEFAULT NULL COMMENT '数据类型',
  `column_type` varchar(150) DEFAULT NULL COMMENT '字段类型',
  `numeric_precision` bigint(20) unsigned DEFAULT NULL,
  `numeric_scale` bigint(20) unsigned DEFAULT NULL,
  `column_length` int(11) DEFAULT NULL COMMENT '字段长度',
  `comment` varchar(500) DEFAULT NULL COMMENT '字段描述',
  `is_nullable` varchar(30) NOT NULL COMMENT '是否非空',
  `column_default` varchar(60) DEFAULT NULL COMMENT '字段默认值',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`column_id`),
  KEY `unique_key_column` (`table_id`,`column_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字段';

-- ----------------------------
-- Table structure for meta_datasource_plugin
-- ----------------------------
CREATE TABLE `meta_datasource_plugin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '插件名称',
  `type` int NOT NULL COMMENT '插件类型：1-关系型数据库，2-非关系型数据库，3-OLAP数据库',
  `driver_name` varchar(100) DEFAULT NULL COMMENT '驱动类名',
  `icon` varchar(200) DEFAULT NULL COMMENT '图标URL',
  `connection_params` text COMMENT '连接参数模板',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注说明',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`) COMMENT '插件名称唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据源插件信息表';

-- ----------------------------
-- Table structure for meta_datasource
-- ----------------------------
DROP TABLE IF EXISTS `meta_datasource`;
CREATE TABLE `meta_datasource` (
  `datasource_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `datasource_name` varchar(50) NOT NULL DEFAULT '' COMMENT '数据源名称',
  `type` varchar(10) NOT NULL DEFAULT '' COMMENT '数据源类型。(mysql,doris)',
  `source_type` tinyint(4) DEFAULT NULL COMMENT '同步类型：1输入源，2输出源',
  `remark` varchar(100) NOT NULL DEFAULT '' COMMENT '数据源描述',
  `connection_params` varchar(300) DEFAULT NULL COMMENT '连接参数',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '数据源状态：启用 1，禁用 0',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`datasource_id`),
  KEY `unique_key_datasourcename` (`datasource_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据源';

-- ----------------------------
-- Table structure for meta_datasource_type
-- ----------------------------
DROP TABLE IF EXISTS `meta_datasource_type`;
CREATE TABLE `meta_datasource_type` (
  `type_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type_name` varchar(100) NOT NULL DEFAULT '' COMMENT '名称',
  `type_code` varchar(64) NOT NULL COMMENT '编码',
  `type_catalog` varchar(64) NOT NULL COMMENT '类型分类：关系型数据库，非关系型数据库，图数据库，分析型数据库',
  `driver_name` varchar(200) NOT NULL COMMENT '驱动名称',
  `icon` varchar(200) DEFAULT NULL COMMENT '图标',
  `jdbc_url` varchar(200) DEFAULT NULL COMMENT 'jdbc连接串',
  `remark` varchar(500) DEFAULT '' COMMENT '任务描述',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据源类型';

-- ----------------------------
-- Records of meta_datasource_type
-- ----------------------------
BEGIN;
INSERT INTO `meta_datasource_type` (`type_id`, `type_name`, `type_code`, `type_catalog`, `driver_name`, `icon`, `jdbc_url`, `remark`, `deleted`, `creator_id`, `create_time`, `updater_id`, `update_time`) VALUES (1, 'MYSQL', 'MYSQL', 'SQL', 'com.mysql.cj.jdbc.Driver', 'MySQL.png', 'jdbc:mysql://%s:%s/%s', '', 0, '0', '2024-03-04 11:40:07', '1', '2024-03-16 19:34:26');
INSERT INTO `meta_datasource_type` (`type_id`, `type_name`, `type_code`, `type_catalog`, `driver_name`, `icon`, `jdbc_url`, `remark`, `deleted`, `creator_id`, `create_time`, `updater_id`, `update_time`) VALUES (2, 'ORACLE', 'ORACLE', 'SQL', 'oracle.jdbc.driver.OracleDriver', 'Oracle.png', 'jdbc:oracle:thin:@%s:%s:%s', '', 0, '0', '2024-03-04 11:46:40', '1', '2024-03-17 16:42:27');
INSERT INTO `meta_datasource_type` (`type_id`, `type_name`, `type_code`, `type_catalog`, `driver_name`, `icon`, `jdbc_url`, `remark`, `deleted`, `creator_id`, `create_time`, `updater_id`, `update_time`) VALUES (3, 'SQLSERVER', 'SQLSERVER', 'SQL', 'com.microsoft.sqlserver.jdbc.SQLServerDriver', 'SQLServer.png', 'jdbc:sqlserver://%s:%s;DatabaseName=%s', '', 0, '0', '2024-03-04 11:46:46', '1', '2024-03-16 19:40:29');
INSERT INTO `meta_datasource_type` (`type_id`, `type_name`, `type_code`, `type_catalog`, `driver_name`, `icon`, `jdbc_url`, `remark`, `deleted`, `creator_id`, `create_time`, `updater_id`, `update_time`) VALUES (9, 'DORIS', 'DORIS', 'OLAP', 'com.mysql.cj.jdbc.Driver', 'Doris.png', 'jdbc:mysql://%s:%s/%s', '', 0, '1', '2024-03-10 16:45:56', '1', '2024-03-16 19:34:26');
INSERT INTO `meta_datasource_type` (`type_id`, `type_name`, `type_code`, `type_catalog`, `driver_name`, `icon`, `jdbc_url`, `remark`, `deleted`, `creator_id`, `create_time`, `updater_id`, `update_time`) VALUES (10, 'CLICKHOUSE', 'ClickHouse', 'OLAP', 'ru.yandex.clickhouse.ClickHouseDriver', 'ClickHouse.png', 'jdbc:clickhouse://%s:%s/%s', '', 0, '1', '2024-03-17 16:56:20', '1', '2024-03-17 20:11:42');
COMMIT;

-- ----------------------------
-- Table structure for meta_db
-- ----------------------------
DROP TABLE IF EXISTS `meta_db`;
CREATE TABLE `meta_db` (
  `db_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `db_name` varchar(80) NOT NULL COMMENT '数据库名称',
  `datasource_id` bigint(20) NOT NULL COMMENT '数据源ID',
  `comment` varchar(500) DEFAULT NULL COMMENT '备注',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`db_id`),
  KEY `unique_key_db` (`datasource_id`,`db_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据库';

-- ----------------------------
-- Table structure for meta_table
-- ----------------------------
DROP TABLE IF EXISTS `meta_table`;
CREATE TABLE `meta_table` (
  `table_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `db_id` bigint(20) NOT NULL COMMENT '数据库id',
  `table_name` varchar(300) NOT NULL COMMENT '表名称',
  `comment` varchar(500) DEFAULT NULL COMMENT '表描述',
  `type` varchar(64) DEFAULT NULL COMMENT '表类型',
  `engine` varchar(64) DEFAULT NULL COMMENT '引擎',
  `table_create_time` datetime DEFAULT NULL COMMENT '表创建时间',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`table_id`),
  KEY `unique_key_tbl` (`db_id`,`table_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据表';

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `config_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(128) NOT NULL DEFAULT '' COMMENT '配置名称',
  `config_key` varchar(128) NOT NULL DEFAULT '' COMMENT '配置键名',
  `config_options` varchar(1024) NOT NULL DEFAULT '' COMMENT '可选的选项',
  `config_value` varchar(256) NOT NULL DEFAULT '' COMMENT '配置值',
  `is_allow_change` tinyint(1) NOT NULL COMMENT '是否允许修改',
  `creator_id` bigint(20) DEFAULT NULL COMMENT '创建者ID',
  `updater_id` bigint(20) DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(128) DEFAULT NULL COMMENT '备注',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`config_id`),
  UNIQUE KEY `config_key_uniq_idx` (`config_key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='参数配置表';

-- ----------------------------
-- Records of sys_config
-- ----------------------------
BEGIN;
INSERT INTO `sys_config` (`config_id`, `config_name`, `config_key`, `config_options`, `config_value`, `is_allow_change`, `creator_id`, `updater_id`, `update_time`, `create_time`, `remark`, `deleted`) VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', '[\"skin-blue\",\"skin-green\",\"skin-purple\",\"skin-red\",\"skin-yellow\"]', 'skin-blue', 1, NULL, 1, '2023-02-17 16:46:50', '2022-05-21 08:30:55', '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow', 0);
INSERT INTO `sys_config` (`config_id`, `config_name`, `config_key`, `config_options`, `config_value`, `is_allow_change`, `creator_id`, `updater_id`, `update_time`, `create_time`, `remark`, `deleted`) VALUES (2, '用户管理-账号初始密码', 'sys.user.initPassword', '', '1234567', 1, NULL, NULL, '2022-08-28 21:54:19', '2022-05-21 08:30:55', '初始化密码 123456', 0);
INSERT INTO `sys_config` (`config_id`, `config_name`, `config_key`, `config_options`, `config_value`, `is_allow_change`, `creator_id`, `updater_id`, `update_time`, `create_time`, `remark`, `deleted`) VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', '[\"theme-dark\",\"theme-light\"]', 'theme-dark', 1, NULL, NULL, '2022-08-28 22:12:15', '2022-08-20 08:30:55', '深色主题theme-dark，浅色主题theme-light', 0);
INSERT INTO `sys_config` (`config_id`, `config_name`, `config_key`, `config_options`, `config_value`, `is_allow_change`, `creator_id`, `updater_id`, `update_time`, `create_time`, `remark`, `deleted`) VALUES (4, '账号自助-验证码开关', 'sys.account.captchaOnOff', '[\"true\",\"false\"]', 'false', 0, NULL, 1, '2023-05-06 14:20:47', '2022-05-21 08:30:55', '是否开启验证码功能（true开启，false关闭）', 0);
INSERT INTO `sys_config` (`config_id`, `config_name`, `config_key`, `config_options`, `config_value`, `is_allow_change`, `creator_id`, `updater_id`, `update_time`, `create_time`, `remark`, `deleted`) VALUES (5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', '[\"true\",\"false\"]', 'true', 0, NULL, 1, '2022-10-05 22:18:57', '2022-05-21 08:30:55', '是否开启注册用户功能（true开启，false关闭）', 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `dept_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '父部门id',
  `ancestors` text NOT NULL COMMENT '祖级列表',
  `dept_name` varchar(64) NOT NULL DEFAULT '' COMMENT '部门名称',
  `order_num` int(11) NOT NULL DEFAULT '0' COMMENT '显示顺序',
  `leader_id` bigint(20) DEFAULT NULL,
  `leader_name` varchar(64) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(16) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `status` smallint(6) NOT NULL DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `creator_id` bigint(20) DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater_id` bigint(20) DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
BEGIN;
INSERT INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader_id`, `leader_name`, `phone`, `email`, `status`, `creator_id`, `create_time`, `updater_id`, `update_time`, `deleted`) VALUES (1, 0, '0', 'lacus', 0, NULL, 'casey', '15800001223', 'casey@163.com', 1, NULL, '2022-05-21 08:30:54', 1, '2023-05-06 14:02:18', 0);
INSERT INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader_id`, `leader_name`, `phone`, `email`, `status`, `creator_id`, `create_time`, `updater_id`, `update_time`, `deleted`) VALUES (2, 1, '0,1', '西安分公司', 1, NULL, 'casey', '15888888888', 'casey@163.com', 1, NULL, '2022-05-21 08:30:54', 1, '2023-05-06 14:02:43', 0);
INSERT INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader_id`, `leader_name`, `phone`, `email`, `status`, `creator_id`, `create_time`, `updater_id`, `update_time`, `deleted`) VALUES (3, 1, '0,1', '长沙分公司', 2, NULL, 'valarchie', '15888888888', 'valarchie@163.com', 1, NULL, '2022-05-21 08:30:54', 1, '2023-03-15 15:14:33', 1);
INSERT INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader_id`, `leader_name`, `phone`, `email`, `status`, `creator_id`, `create_time`, `updater_id`, `update_time`, `deleted`) VALUES (4, 2, '0,1,2', '研发部门', 1, NULL, 'valarchie', '15888888888', 'valarchie@163.com', 1, NULL, '2022-05-21 08:30:54', NULL, NULL, 0);
INSERT INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader_id`, `leader_name`, `phone`, `email`, `status`, `creator_id`, `create_time`, `updater_id`, `update_time`, `deleted`) VALUES (5, 2, '0,1,2', '市场部门', 2, NULL, 'valarchie', '15888888888', 'valarchie@163.com', 1, NULL, '2022-05-21 08:30:54', 1, '2023-05-06 14:04:19', 1);
INSERT INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader_id`, `leader_name`, `phone`, `email`, `status`, `creator_id`, `create_time`, `updater_id`, `update_time`, `deleted`) VALUES (6, 2, '0,1,2', '测试部门', 3, NULL, 'valarchie', '15888888888', 'valarchie@163.com', 1, NULL, '2022-05-21 08:30:54', 1, '2023-05-06 14:04:13', 1);
INSERT INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader_id`, `leader_name`, `phone`, `email`, `status`, `creator_id`, `create_time`, `updater_id`, `update_time`, `deleted`) VALUES (7, 2, '0,1,2', '财务部门', 4, NULL, 'valarchie', '15888888888', 'valarchie@163.com', 1, NULL, '2022-05-21 08:30:54', 1, '2023-05-06 14:04:16', 1);
INSERT INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader_id`, `leader_name`, `phone`, `email`, `status`, `creator_id`, `create_time`, `updater_id`, `update_time`, `deleted`) VALUES (8, 2, '0,1,2', '运维部门', 5, NULL, 'valarchie', '15888888888', 'valarchie@163.com', 1, NULL, '2022-05-21 08:30:54', NULL, NULL, 0);
INSERT INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader_id`, `leader_name`, `phone`, `email`, `status`, `creator_id`, `create_time`, `updater_id`, `update_time`, `deleted`) VALUES (9, 3, '0,1,3', '市场部门', 1, NULL, 'valarchie', '15888888888', 'valarchie@163.com', 1, NULL, '2022-05-21 08:30:54', 1, '2023-03-15 15:14:31', 1);
INSERT INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader_id`, `leader_name`, `phone`, `email`, `status`, `creator_id`, `create_time`, `updater_id`, `update_time`, `deleted`) VALUES (10, 3, '0,1,3', '财务部门', 2, NULL, 'valarchie', '15888888888', 'valarchie@163.com', 0, NULL, '2022-05-21 08:30:54', 1, '2023-03-15 15:14:29', 1);
INSERT INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader_id`, `leader_name`, `phone`, `email`, `status`, `creator_id`, `create_time`, `updater_id`, `update_time`, `deleted`) VALUES (203, 1, '0,1', '北京分公司', 0, NULL, NULL, NULL, NULL, 1, 1, '2023-05-06 14:03:09', 1, '2023-05-06 14:03:14', 0);
INSERT INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader_id`, `leader_name`, `phone`, `email`, `status`, `creator_id`, `create_time`, `updater_id`, `update_time`, `deleted`) VALUES (204, 203, '0,1,203', '测试部门', 0, NULL, NULL, NULL, NULL, 0, 1, '2023-05-06 14:03:40', NULL, NULL, 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_login_info
-- ----------------------------
DROP TABLE IF EXISTS `sys_login_info`;
CREATE TABLE `sys_login_info` (
  `info_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `username` varchar(50) NOT NULL DEFAULT '' COMMENT '用户账号',
  `ip_address` varchar(128) NOT NULL DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) NOT NULL DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) NOT NULL DEFAULT '' COMMENT '浏览器类型',
  `operation_system` varchar(50) NOT NULL DEFAULT '' COMMENT '操作系统',
  `status` smallint(6) NOT NULL DEFAULT '0' COMMENT '登录状态（1成功 0失败）',
  `msg` varchar(255) NOT NULL DEFAULT '' COMMENT '提示消息',
  `login_time` datetime DEFAULT NULL COMMENT '访问时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`info_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统访问记录';

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(64) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int(11) NOT NULL DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(255) DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) DEFAULT NULL COMMENT '路由参数',
  `is_external` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否为外链（1是 0否）',
  `is_cache` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否缓存（1缓存 0不缓存）',
  `menu_type` smallint(6) NOT NULL DEFAULT '0' COMMENT '菜单类型（M=1目录 C=2菜单 F=3按钮）',
  `is_visible` tinyint(1) NOT NULL DEFAULT '0' COMMENT '菜单状态（1显示 0隐藏）',
  `status` smallint(6) NOT NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(128) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(128) DEFAULT '#' COMMENT '菜单图标',
  `creator_id` bigint(20) DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater_id` bigint(20) DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(512) DEFAULT '' COMMENT '备注',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (1, '平台管理', 0, 7, 'system', NULL, '', 0, 1, 1, 1, 1, '', 'system', 0, '2022-05-21 08:30:54', 1, '2025-03-23 16:06:19', '系统管理目录', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2, '系统监控', 0, 9, 'monitor', NULL, '', 0, 1, 1, 1, 1, '', 'monitor', 0, '2022-05-21 08:30:54', 1, '2025-03-23 16:06:11', '系统监控目录', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (3, '系统工具', 0, 4, 'tool', NULL, '', 0, 1, 1, 1, 1, '', 'tool', 0, '2022-05-21 08:30:54', 1, '2023-04-24 21:51:30', '系统工具目录', 1);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (4, '官网文档', 0, 5, 'https://juejin.cn/column/7159946528827080734', NULL, '', 1, 1, 1, 1, 1, '', 'guide', 0, '2022-05-21 08:30:54', 1, '2023-04-26 09:14:48', 'pandora官网地址', 1);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (5, '用户管理', 1, 1, 'user', 'system/user/index', '', 0, 1, 2, 1, 1, 'system:user:list', 'user', 0, '2022-05-21 08:30:54', NULL, NULL, '用户管理菜单', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (6, '角色管理', 1, 2, 'role', 'system/role/index', '', 0, 1, 2, 1, 1, 'system:role:list', 'email', 0, '2022-05-21 08:30:54', 1, '2023-09-04 10:58:41', '角色管理菜单', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (7, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', 0, 1, 2, 1, 1, 'system:menu:list', 'tree-table', 0, '2022-05-21 08:30:54', NULL, NULL, '菜单管理菜单', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (8, '部门管理', 1, 4, 'dept', 'system/dept/index', '', 0, 1, 2, 1, 1, 'system:dept:list', 'tree', 0, '2022-05-21 08:30:54', NULL, NULL, '部门管理菜单', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (9, '岗位管理', 1, 5, 'post', 'system/post/index', '', 0, 1, 2, 1, 1, 'system:post:list', 'post', 0, '2022-05-21 08:30:54', NULL, NULL, '岗位管理菜单', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (10, '参数设置', 1, 7, 'config', 'system/config/index', '', 0, 1, 2, 1, 1, 'system:config:list', 'edit', 0, '2022-05-21 08:30:54', NULL, NULL, '参数设置菜单', 1);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (11, '通知公告', 2032, 8, 'notice', 'system/notice/index', '', 0, 1, 2, 1, 1, 'system:notice:list', 'message', 0, '2022-05-21 08:30:54', 1, '2024-05-02 17:53:42', '通知公告菜单', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (12, '日志管理', 2, 9, 'log', '', '', 0, 1, 1, 1, 1, '', 'log', 0, '2022-05-21 08:30:54', 1, '2024-05-02 17:52:06', '日志管理菜单', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (13, '在线用户', 2, 1, 'online', 'monitor/online/index', '', 0, 1, 2, 1, 1, 'monitor:online:list', 'online', 0, '2022-05-21 08:30:54', NULL, NULL, '在线用户菜单', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (14, '数据监控', 2, 3, 'druid', 'monitor/druid/index', '', 0, 1, 2, 1, 1, 'monitor:druid:list', 'druid', 0, '2022-05-21 08:30:54', NULL, NULL, '数据监控菜单', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (15, '服务监控', 2, 4, 'server', 'monitor/server/index', '', 0, 1, 2, 1, 1, 'monitor:server:list', 'server', 0, '2022-05-21 08:30:54', NULL, NULL, '服务监控菜单', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (16, '缓存监控', 2, 5, 'cache', 'monitor/cache/index', '', 0, 1, 2, 1, 1, 'monitor:cache:list', 'redis', 0, '2022-05-21 08:30:54', NULL, NULL, '缓存监控菜单', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (17, '系统接口', 3, 3, 'swagger', 'tool/swagger/index', '', 0, 1, 2, 1, 1, 'tool:swagger:list', 'swagger', 0, '2022-05-21 08:30:54', NULL, NULL, '系统接口菜单', 1);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (18, '操作日志', 12, 1, 'operlog', 'monitor/operlog/index', '', 0, 1, 2, 1, 1, 'monitor:operlog:list', 'form', 0, '2022-05-21 08:30:54', NULL, NULL, '操作日志菜单', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (19, '登录日志', 12, 2, 'logininfor', 'monitor/logininfor/index', '', 0, 1, 2, 1, 1, 'monitor:logininfor:list', 'logininfor', 0, '2022-05-21 08:30:54', NULL, NULL, '登录日志菜单', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (20, '用户查询', 5, 1, '', '', '', 0, 1, 3, 1, 1, 'system:user:query', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (21, '用户新增', 5, 2, '', '', '', 0, 1, 3, 1, 1, 'system:user:add', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (22, '用户修改', 5, 3, '', '', '', 0, 1, 3, 1, 1, 'system:user:edit', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (23, '用户删除', 5, 4, '', '', '', 0, 1, 3, 1, 1, 'system:user:remove', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (24, '用户导出', 5, 5, '', '', '', 0, 1, 3, 1, 1, 'system:user:export', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (25, '用户导入', 5, 6, '', '', '', 0, 1, 3, 1, 1, 'system:user:import', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (26, '重置密码', 5, 7, '', '', '', 0, 1, 3, 1, 1, 'system:user:resetPwd', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (27, '角色查询', 6, 1, '', '', '', 0, 1, 3, 1, 1, 'system:role:query', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (28, '角色新增', 6, 2, '', '', '', 0, 1, 3, 1, 1, 'system:role:add', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (29, '角色修改', 6, 3, '', '', '', 0, 1, 3, 1, 1, 'system:role:edit', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (30, '角色删除', 6, 4, '', '', '', 0, 1, 3, 1, 1, 'system:role:remove', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (31, '角色导出', 6, 5, '', '', '', 0, 1, 3, 1, 1, 'system:role:export', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (32, '菜单查询', 7, 1, '', '', '', 0, 1, 3, 1, 1, 'system:menu:query', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (33, '菜单新增', 7, 2, '', '', '', 0, 1, 3, 1, 1, 'system:menu:add', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (34, '菜单修改', 7, 3, '', '', '', 0, 1, 3, 1, 1, 'system:menu:edit', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (35, '菜单删除', 7, 4, '', '', '', 0, 1, 3, 1, 1, 'system:menu:remove', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (36, '部门查询', 8, 1, '', '', '', 0, 1, 3, 1, 1, 'system:dept:query', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (37, '部门新增', 8, 2, '', '', '', 0, 1, 3, 1, 1, 'system:dept:add', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (38, '部门修改', 8, 3, '', '', '', 0, 1, 3, 1, 1, 'system:dept:edit', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (39, '部门删除', 8, 4, '', '', '', 0, 1, 3, 1, 1, 'system:dept:remove', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (40, '岗位查询', 9, 1, '', '', '', 0, 1, 3, 1, 1, 'system:post:query', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (41, '岗位新增', 9, 2, '', '', '', 0, 1, 3, 1, 1, 'system:post:add', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (42, '岗位修改', 9, 3, '', '', '', 0, 1, 3, 1, 1, 'system:post:edit', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (43, '岗位删除', 9, 4, '', '', '', 0, 1, 3, 1, 1, 'system:post:remove', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (44, '岗位导出', 9, 5, '', '', '', 0, 1, 3, 1, 1, 'system:post:export', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (45, '参数查询', 10, 1, '#', '', '', 0, 1, 3, 1, 1, 'system:config:query', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 1);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (46, '参数新增', 10, 2, '#', '', '', 0, 1, 3, 1, 1, 'system:config:add', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 1);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (47, '参数修改', 10, 3, '#', '', '', 0, 1, 3, 1, 1, 'system:config:edit', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 1);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (48, '参数删除', 10, 4, '#', '', '', 0, 1, 3, 1, 1, 'system:config:remove', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 1);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (49, '参数导出', 10, 5, '#', '', '', 0, 1, 3, 1, 1, 'system:config:export', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 1);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (50, '公告查询', 11, 1, '#', '', '', 0, 1, 3, 1, 1, 'system:notice:query', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (51, '公告新增', 11, 2, '#', '', '', 0, 1, 3, 1, 1, 'system:notice:add', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (52, '公告修改', 11, 3, '#', '', '', 0, 1, 3, 1, 1, 'system:notice:edit', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (53, '公告删除', 11, 4, '#', '', '', 0, 1, 3, 1, 1, 'system:notice:remove', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (54, '操作查询', 18, 1, '#', '', '', 0, 1, 3, 1, 1, 'monitor:operlog:query', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (55, '操作删除', 18, 2, '#', '', '', 0, 1, 3, 1, 1, 'monitor:operlog:remove', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (56, '日志导出', 18, 4, '#', '', '', 0, 1, 3, 1, 1, 'monitor:operlog:export', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (57, '登录查询', 19, 1, '#', '', '', 0, 1, 3, 1, 1, 'monitor:logininfor:query', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (58, '登录删除', 19, 2, '#', '', '', 0, 1, 3, 1, 1, 'monitor:logininfor:remove', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (59, '日志导出', 19, 3, '#', '', '', 0, 1, 3, 1, 1, 'monitor:logininfor:export', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (60, '在线查询', 13, 1, '#', '', '', 0, 1, 3, 1, 1, 'monitor:online:query', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (61, '批量强退', 13, 2, '#', '', '', 0, 1, 3, 1, 1, 'monitor:online:batchLogout', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (62, '单条强退', 13, 3, '#', '', '', 0, 1, 3, 1, 1, 'monitor:online:forceLogout', '#', 0, '2022-05-21 08:30:54', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2009, '元数据', 0, 1, 'metadata', NULL, NULL, 0, 0, 1, 1, 1, NULL, 'redis', 1, '2023-04-20 18:11:10', 1, '2024-05-02 17:56:30', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2010, '数据源定义', 2009, 1, 'datasource', 'metadata/datasource/index', NULL, 0, 0, 2, 1, 1, 'metadata:datasource:list', 'dict', 1, '2023-04-20 18:13:53', 1, '2024-03-23 19:58:29', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2011, '数据表查询', 2009, 2, 'table', 'metadata/table/index', NULL, 0, 0, 2, 1, 1, 'metadata:table:list', 'table', 1, '2023-04-24 17:58:27', 1, '2024-03-23 19:58:36', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2012, '数据采集', 0, 2, 'datasync', NULL, NULL, 0, 0, 1, 1, 1, NULL, 'example', 1, '2023-05-10 17:36:09', 1, '2024-05-02 17:56:37', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2013, '分组管理', 2033, 1, 'catalog', 'datasync/catalog/index', NULL, 0, 0, 2, 1, 1, 'datasync:catalog:list', 'tool', 1, '2023-05-10 17:43:08', 1, '2025-03-23 15:32:45', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2014, '任务定义', 2012, 2, 'job', 'datasync/job/index', NULL, 0, 0, 2, 1, 1, 'datasync:job:list', 'select', 1, '2023-05-11 18:14:36', 1, '2024-02-26 20:25:02', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2015, '接口文档', 0, 10, 'tool', NULL, NULL, 0, 0, 1, 1, 1, NULL, 'tool', 1, '2023-07-06 16:27:09', 1, '2025-03-23 16:06:07', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2016, '系统接口', 2015, 1, 'swagger', 'tool/swagger/index', NULL, 0, 1, 2, 1, 1, 'tool:swagger:list', 'swagger', 1, '2023-07-06 16:28:21', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2017, '新增数据源', 2010, 1, '', NULL, NULL, 0, 0, 3, 0, 1, 'metadata:datasource:add', 'build', 1, '2024-02-21 21:42:56', 1, '2024-02-21 21:46:00', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2018, '编辑数据源', 2010, 2, '', NULL, NULL, 0, 0, 3, 0, 1, 'metadata:datasource:edit', 'log', 1, '2024-02-21 21:47:12', 1, '2024-03-23 19:59:18', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2019, '删除数据源', 2010, 3, '', NULL, NULL, 0, 0, 3, 0, 1, 'metadata:datasource:remove', 'checkbox', 1, '2024-02-21 21:48:31', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2020, '表详情', 2011, 1, '', NULL, NULL, 0, 0, 3, 0, 1, 'metadata:table:query', 'eye-open', 1, '2024-02-21 21:49:57', 1, '2024-03-23 19:59:31', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2021, '新增分组', 2013, 1, '', NULL, NULL, 0, 0, 3, 0, 1, 'datasync:catalog:add', 'button', 1, '2024-02-21 21:51:18', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2022, '编辑分组', 2013, 2, '', NULL, NULL, 0, 0, 3, 0, 1, 'datasync:catalog:edit', 'checkbox', 1, '2024-02-21 21:51:37', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2023, '删除分组', 2013, 3, '', NULL, NULL, 0, 0, 3, 0, 1, 'datasync:catalog:remove', 'checkbox', 1, '2024-02-21 21:51:56', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2024, '新建任务', 2014, 1, '', NULL, NULL, 0, 0, 3, 0, 1, 'datasync:job:add', '#', 1, '2024-02-21 21:53:08', 1, '2024-03-03 12:36:34', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2025, '编辑任务', 2014, 2, '', NULL, NULL, 0, 0, 3, 0, 1, 'datasync:job:edit', '#', 1, '2024-02-21 21:53:25', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2026, '删除任务', 2014, 3, '', NULL, NULL, 0, 0, 3, 0, 1, 'datasync:job:remove', '#', 1, '2024-02-21 21:53:48', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2027, '任务详情', 2014, 4, '', NULL, NULL, 0, 0, 3, 0, 1, 'datasync:job:query', '#', 1, '2024-02-21 21:54:04', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2028, '任务实例', 2012, 3, 'jobInstance', 'datasync/instance/index', NULL, 0, 0, 2, 1, 1, 'datasync:instance:list', 'color', 1, '2024-02-29 21:58:39', 1, '2024-02-29 22:02:37', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2029, '任务监控', 2028, 3, '', NULL, NULL, 0, 0, 3, 0, 1, 'datasync:instance:query', 'button', 1, '2024-03-03 12:32:11', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2030, '数据源类型', 2009, 0, 'datasourceType', 'metadata/datasourceType/index', '', 0, 0, 2, 1, 1, 'metadata:datasourceType:list', 'chart', 1, '2024-03-04 11:24:33', 1, '2024-03-23 19:55:23', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2031, '环境管理', 2033, 1, 'env', 'system/env/index', NULL, 0, 1, 2, 1, 1, 'system:env:list', 'skill', 1, '2024-05-02 17:11:53', 1, '2024-05-02 17:54:56', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2032, '消息中心', 0, 8, 'message', NULL, NULL, 0, 0, 1, 1, 1, NULL, 'message', 1, '2024-05-02 17:53:08', 1, '2025-03-23 16:06:15', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2033, '资源中心', 0, 6, 'config', NULL, NULL, 0, 0, 1, 1, 1, NULL, 'system', 1, '2024-05-02 17:54:44', 1, '2025-03-23 16:06:26', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2035, 'flink开发', 0, 3, 'flink', NULL, NULL, 0, 0, 1, 1, 1, NULL, 'example', 1, '2024-12-08 10:53:47', 1, '2024-12-08 13:17:37', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2036, '任务定义', 2035, 1, 'flink', 'flink/job/index', NULL, 0, 0, 2, 1, 1, 'flink:job:list', 'cascader', 1, '2024-12-08 11:51:52', 1, '2024-12-08 13:17:22', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2038, '任务实例', 2035, 2, 'instance', 'flink/instance/index', NULL, 0, 0, 2, 1, 1, 'flink:job:list', 'button', 1, '2024-12-17 21:14:41', 1, '2024-12-17 21:18:13', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2039, '资源管理', 2033, 1, 'resource', 'system/resource/index', NULL, 0, 1, 2, 1, 1, 'system:resource:list', 'skill', 1, '2024-05-02 17:11:53', 1, '2024-05-02 17:54:56', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2040, 'spark开发', 0, 4, 'spark', NULL, NULL, 0, 0, 1, 1, 1, NULL, 'email', 1, '2025-01-01 17:56:40', 1, '2025-01-01 19:27:18', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2041, '任务定义', 2040, 1, 'spark', 'spark/job/index', NULL, 0, 0, 2, 1, 1, NULL, 'button', 1, '2025-01-01 17:58:33', NULL, NULL, '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2042, '任务实例', 2040, 2, 'instance', 'spark/instance/index', NULL, 0, 0, 2, 1, 1, NULL, 'checkbox', 1, '2025-01-01 18:04:37', 1, '2025-01-01 18:04:52', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2043, '统一API', 0, 5, 'oneapi', NULL, NULL, 0, 0, 1, 1, 1, NULL, 'excel', 1, '2025-03-15 11:14:57', 1, '2025-03-23 16:05:58', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2044, 'Api定义', 2043, 1, 'oneapi', 'oneapi/index', NULL, 0, 0, 2, 1, 1, NULL, 'chart', 1, '2025-03-15 11:16:37', NULL, NULL, '', 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice` (
  `notice_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(64) NOT NULL COMMENT '公告标题',
  `notice_type` smallint(6) NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` text COMMENT '公告内容',
  `status` smallint(6) NOT NULL DEFAULT '0' COMMENT '公告状态（1正常 0关闭）',
  `creator_id` bigint(20) NOT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater_id` bigint(20) DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告表';

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
BEGIN;
INSERT INTO `sys_notice` (`notice_id`, `notice_title`, `notice_type`, `notice_content`, `status`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (1, '温馨提醒：2018-07-01 lacus新版本发布啦', 2, '新版本内容~~~~~~~~~~', 1, 1, '2022-05-21 08:30:55', 1, '2024-03-03 12:34:55', '管理员', 0);
INSERT INTO `sys_notice` (`notice_id`, `notice_title`, `notice_type`, `notice_content`, `status`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2, '维护通知：2018-07-01 lacus系统凌晨维护', 1, '维护内容', 1, 1, '2022-05-21 08:30:55', 1, '2024-03-03 12:34:46', '管理员', 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log` (
  `operation_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `business_type` smallint(6) NOT NULL DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `request_method` smallint(6) NOT NULL DEFAULT '0' COMMENT '请求方式',
  `request_module` varchar(64) NOT NULL DEFAULT '' COMMENT '请求模块',
  `request_url` varchar(256) NOT NULL DEFAULT '' COMMENT '请求URL',
  `called_method` varchar(128) NOT NULL DEFAULT '' COMMENT '调用方法',
  `operator_type` smallint(6) NOT NULL DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `user_id` bigint(20) DEFAULT '0' COMMENT '用户ID',
  `username` varchar(32) DEFAULT '' COMMENT '操作人员',
  `operator_ip` varchar(128) DEFAULT '' COMMENT '操作人员ip',
  `operator_location` varchar(256) DEFAULT '' COMMENT '操作地点',
  `dept_id` bigint(20) DEFAULT '0' COMMENT '部门ID',
  `dept_name` varchar(64) DEFAULT NULL COMMENT '部门名称',
  `operation_param` varchar(2048) DEFAULT '' COMMENT '请求参数',
  `operation_result` varchar(2048) DEFAULT '' COMMENT '返回参数',
  `status` smallint(6) NOT NULL DEFAULT '1' COMMENT '操作状态（1正常 0异常）',
  `error_stack` varchar(2048) DEFAULT '' COMMENT '错误消息',
  `operation_time` datetime NOT NULL COMMENT '操作时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`operation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志记录';

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post` (
  `post_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) NOT NULL COMMENT '岗位编码',
  `post_name` varchar(64) NOT NULL COMMENT '岗位名称',
  `post_sort` int(11) NOT NULL COMMENT '显示顺序',
  `status` smallint(6) NOT NULL COMMENT '状态（1正常 0停用）',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `creator_id` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater_id` bigint(20) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位信息表';

-- ----------------------------
-- Records of sys_post
-- ----------------------------
BEGIN;
INSERT INTO `sys_post` (`post_id`, `post_code`, `post_name`, `post_sort`, `status`, `remark`, `creator_id`, `create_time`, `updater_id`, `update_time`, `deleted`) VALUES (1, 'ceo', '总经理', 1, 1, '', NULL, '2022-05-21 08:30:54', 1, '2023-05-06 14:04:54', 0);
INSERT INTO `sys_post` (`post_id`, `post_code`, `post_name`, `post_sort`, `status`, `remark`, `creator_id`, `create_time`, `updater_id`, `update_time`, `deleted`) VALUES (2, 'se', '项目经理', 2, 1, '', NULL, '2022-05-21 08:30:54', NULL, NULL, 0);
INSERT INTO `sys_post` (`post_id`, `post_code`, `post_name`, `post_sort`, `status`, `remark`, `creator_id`, `create_time`, `updater_id`, `update_time`, `deleted`) VALUES (3, 'hr', '人力资源', 3, 1, '', NULL, '2022-05-21 08:30:54', NULL, NULL, 0);
INSERT INTO `sys_post` (`post_id`, `post_code`, `post_name`, `post_sort`, `status`, `remark`, `creator_id`, `create_time`, `updater_id`, `update_time`, `deleted`) VALUES (4, 'user', '普通员工', 5, 0, '', NULL, '2022-05-21 08:30:54', 1, '2023-03-15 15:16:28', 1);
COMMIT;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(32) NOT NULL COMMENT '角色名称',
  `role_key` varchar(128) NOT NULL COMMENT '角色权限字符串',
  `role_sort` int(11) NOT NULL COMMENT '显示顺序',
  `data_scope` smallint(6) DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3: 本部门数据权限 4: 本部门及以下数据权限 5: 本人权限）',
  `dept_id_set` varchar(1024) DEFAULT '' COMMENT '角色所拥有的部门数据权限',
  `status` smallint(6) NOT NULL COMMENT '角色状态（1正常 0停用）',
  `creator_id` bigint(20) DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater_id` bigint(20) DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_role` (`role_id`, `role_name`, `role_key`, `role_sort`, `data_scope`, `dept_id_set`, `status`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (1, '超级管理员', 'admin', 1, 1, '', 1, NULL, '2022-05-21 08:30:54', NULL, NULL, '超级管理员', 0);
INSERT INTO `sys_role` (`role_id`, `role_name`, `role_key`, `role_sort`, `data_scope`, `dept_id_set`, `status`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2, '普通角色', 'common', 2, 2, '4,6', 1, NULL, '2022-05-21 08:30:54', 1, '2024-03-10 16:11:59', '普通角色', 0);
INSERT INTO `sys_role` (`role_id`, `role_name`, `role_key`, `role_sort`, `data_scope`, `dept_id_set`, `status`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (3, '闲置角色', 'unused', 4, 2, '', 0, NULL, '2022-05-21 08:30:54', NULL, NULL, '未使用的角色', 1);
COMMIT;

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和菜单关联表';

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 2009);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 2010);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 2011);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 2012);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 2013);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 2014);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 2017);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 2018);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 2019);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 2020);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 2021);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 2022);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 2023);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 2024);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 2025);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 2026);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 2027);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 2028);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 2029);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 2030);
COMMIT;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `post_id` bigint(20) DEFAULT NULL COMMENT '职位id',
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色id',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `username` varchar(64) NOT NULL COMMENT '用户账号',
  `nick_name` varchar(32) NOT NULL COMMENT '用户昵称',
  `user_type` smallint(6) DEFAULT '0' COMMENT '用户类型（00系统用户）',
  `email` varchar(128) DEFAULT '' COMMENT '用户邮箱',
  `phone_number` varchar(18) DEFAULT '' COMMENT '手机号码',
  `sex` smallint(6) DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(512) DEFAULT '' COMMENT '头像地址',
  `password` varchar(128) NOT NULL DEFAULT '' COMMENT '密码',
  `status` smallint(6) NOT NULL DEFAULT '0' COMMENT '帐号状态（1正常 2停用 3冻结）',
  `login_ip` varchar(128) DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `creator_id` bigint(20) DEFAULT NULL COMMENT '更新者ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater_id` bigint(20) DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` (`user_id`, `post_id`, `role_id`, `dept_id`, `username`, `nick_name`, `user_type`, `email`, `phone_number`, `sex`, `avatar`, `password`, `status`, `login_ip`, `login_date`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (1, 1, 1, 4, 'admin', 'lacus', 0, 'lacus@163.com', '15888888889', 0, '', '$2a$10$rb1wRoEIkLbIknREEN1LH.FGs4g0oOS5t6l5LQ793nRaFO.SPHDHy', 1, '222.90.63.253', '2024-04-12 17:53:45', NULL, '2022-05-21 08:30:54', 1, '2024-04-12 17:53:45', '管理员', 0);
INSERT INTO `sys_user` (`user_id`, `post_id`, `role_id`, `dept_id`, `username`, `nick_name`, `user_type`, `email`, `phone_number`, `sex`, `avatar`, `password`, `status`, `login_ip`, `login_date`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (109, 1, 2, 4, 'demo', '游客', 0, '', '', 1, '', '$2a$10$0JB.QbSCyuhRBGT/ePFtOuqcTmX2xFOLGzVDuGO4026YqugtdzIHK', 1, '113.54.246.76', '2024-04-14 15:58:39', 1, '2023-12-14 13:44:26', 1, '2024-04-14 15:58:39', NULL, 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_env
-- ----------------------------
DROP TABLE IF EXISTS `sys_env`;
CREATE TABLE `sys_env` (
      `env_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
      `name` varchar(100) NOT NULL COMMENT '环境名称',
      `config` longtext NOT NULL COMMENT '环境配置',
      `remark` varchar(500) DEFAULT NULL COMMENT '环境描述',
      `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
      `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
      `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
      `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
      `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
      PRIMARY KEY (`env_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='环境管理';

-- ----------------------------
-- Table structure for sys_resources
-- ----------------------------
CREATE TABLE `sys_resources` (
     `id` int NOT NULL AUTO_INCREMENT COMMENT 'resource id',
     `pid` int DEFAULT NULL COMMENT 'parent resource id',
     `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'alia name',
     `file_name` varchar(200) DEFAULT NULL COMMENT 'file name',
     `file_path` varchar(500) DEFAULT NULL COMMENT 'file path',
     `remark` varchar(200) DEFAULT NULL,
     `type` tinyint DEFAULT NULL COMMENT 'resource type: 0 FILE，1 UDF',
     `size` bigint DEFAULT NULL COMMENT 'resource size',
     `is_directory` tinyint DEFAULT NULL,
     `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
     `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
     `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for flink_job
-- ----------------------------
CREATE TABLE `flink_job` (
    `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `job_name` varchar(100) NOT NULL COMMENT '任务名称',
    `app_id` varchar(100) DEFAULT NULL COMMENT 'application id',
    `save_point` varchar(100) DEFAULT NULL COMMENT 'savepoint',
    `job_type` varchar(100) NOT NULL COMMENT '任务类型 STREAMING_SQL, BATCH_SQL, JAR',
    `job_manager` int NOT NULL DEFAULT '1' COMMENT 'job_manager',
    `task_manager` int NOT NULL DEFAULT '1' COMMENT 'task_manager',
    `slot` int NOT NULL DEFAULT '1' COMMENT 'slot',
    `parallelism` int NOT NULL DEFAULT '1' COMMENT '并行度',
    `queue` varchar(100) DEFAULT NULL COMMENT '队列',
    `deploy_mode` varchar(100) NOT NULL COMMENT '部署模式：YARN_PER, STANDALONE, LOCAL, YARN_APPLICATION',
    `flink_sql` text COMMENT 'flink sql',
    `main_jar_path` varchar(100) DEFAULT NULL COMMENT '主jar包路径',
    `ext_jar_path` varchar(100) DEFAULT NULL COMMENT '第三方jar udf、 连接器等',
    `main_class_name` varchar(100) DEFAULT NULL COMMENT '主类名',
    `flink_run_config` varchar(200) DEFAULT NULL COMMENT 'flink参数',
    `custom_args` varchar(200) DEFAULT NULL COMMENT '自定义参数',
    `env_id` bigint DEFAULT NULL COMMENT '环境id',
    `job_status` varchar(100) DEFAULT NULL COMMENT '任务状态：1 提交成功 ，2 运行中，3 成功，4 失败',
    `remark` varchar(100) DEFAULT NULL COMMENT '任务描述',
    `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='flink任务表';


-- ----------------------------
-- Table structure for flink_job_instance
-- ----------------------------
CREATE TABLE `flink_job_instance` (
    `instance_id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `job_id` bigint NOT NULL COMMENT '任务ID',
    `instance_name` varchar(150) NOT NULL DEFAULT '' COMMENT '实例名称',
    `application_id` varchar(100) DEFAULT NULL COMMENT 'flink任务ID',
    `deploy_mode` varchar(100) NOT NULL COMMENT '部署模式：YARN_PER, STANDALONE, LOCAL, YARN_APPLICATION',
    `save_point` varchar(200) DEFAULT NULL COMMENT 'savepoint地址',
    `flink_job_id` varchar(100) DEFAULT NULL COMMENT 'flink任务ID',
    `job_script` longtext COMMENT '任务脚本',
    `submit_time` datetime DEFAULT NULL COMMENT '任务提交时间',
    `finished_time` datetime DEFAULT NULL COMMENT '任务结束时间',
    `status` varchar(100) NOT NULL DEFAULT '1' COMMENT '任务状态 RUNNING, KILL, FAILED',
    `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`instance_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='flink任务实例表';
SET FOREIGN_KEY_CHECKS = 1;


-- ----------------------------
-- Table structure for spark_job
-- ----------------------------
CREATE TABLE `spark_job` (
  `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `job_name` varchar(100) NOT NULL COMMENT '任务名称',
  `application_id` varchar(100) DEFAULT NULL COMMENT 'application id',
  `job_type` varchar(100) DEFAULT NULL COMMENT '任务类型：JAR, BATCH_SQL',
  `master` varchar(100) DEFAULT NULL COMMENT 'master',
  `deploy_mode` varchar(100) NOT NULL COMMENT '部署模式：local, yarn-client, yarn-cluster, k8s-client, k8s-cluster',
  `parallelism` int NOT NULL DEFAULT '1' COMMENT '并行度',
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
  `schedule_status` int DEFAULT NULL COMMENT '调度状态：1 启用 0 停用',
  `cron_expression` varchar(100) DEFAULT NULL COMMENT 'cron表达式',
  `start_time` datetime DEFAULT NULL COMMENT '任务开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '任务结束时间',
  `remark` varchar(100) DEFAULT NULL COMMENT '任务描述',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='spark任务表';

-- ----------------------------
-- Table structure for spark_job_instance
-- ----------------------------
CREATE TABLE `spark_job_instance` (
  `instance_id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `job_id` bigint NOT NULL COMMENT '任务id',
  `instance_name` varchar(100) NOT NULL COMMENT '任务实例名称',
  `deploy_mode` varchar(100) NOT NULL COMMENT '部署模式：local, yarn-client, yarn-cluster, k8s-client, k8s-cluster',
  `application_id` varchar(100) DEFAULT NULL COMMENT 'application id',
  `job_type` varchar(100) DEFAULT NULL COMMENT '任务类型：JAR, BATCH_SQL',
  `job_script` varchar(100) DEFAULT NULL COMMENT '任务执行脚本',
  `sql_content` longtext DEFAULT NULL COMMENT 'spark sql',
  `env_id` bigint DEFAULT NULL COMMENT '环境id',
  `job_status` varchar(100) DEFAULT NULL COMMENT '任务状态：1 提交成功 ，2 运行中，3 成功，4 失败',
  `submit_time` datetime NOT NULL COMMENT '提交时间',
  `finished_time` datetime DEFAULT NULL COMMENT '完成时间',
  `error_info` varchar(5000) DEFAULT NULL COMMENT '错误信息',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`instance_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='spark任务实例表';

-- ----------------------------
-- Table structure for one_api_info
-- ----------------------------
CREATE TABLE `one_api_info` (
    `api_id` bigint NOT NULL AUTO_INCREMENT COMMENT '接口ID',
    `api_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'api名称',
    `api_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '接口url',
    `group_id` int DEFAULT NULL COMMENT 'api分组id',
    `api_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'api类型',
    `datasource_id` bigint NOT NULL COMMENT '数据源id',
    `query_timeout` tinyint NOT NULL DEFAULT '3' COMMENT '超时时间，默认3秒，最大10s',
    `req_method` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '请求方式',
    `limit_count` int NOT NULL DEFAULT '5000' COMMENT '最大返回条数',
    `api_desc` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '接口描述',
    `api_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'api配置',
    `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '接口状态：0未发布状态，1发布状态',
    `api_response` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT 'api响应结果',
    `online_edit` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否为在线编辑，0：下线编辑，1：在线编辑',
    `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id` varchar(64) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `updater_id` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`api_id`) USING BTREE,
    KEY `api_url` (`api_url`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='api详情表';

-- ----------------------------
-- Table structure for one_api_call_history
-- ----------------------------
CREATE TABLE `one_api_call_history` (
    `history_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `call_date` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '调用日期',
    `call_ip` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '调用ip',
    `api_url` varchar(200) DEFAULT NULL COMMENT 'api地址',
    `call_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '调用状态',
    `call_code` bigint NOT NULL COMMENT '调用code',
    `error_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '错误信息',
    `call_delay` int NOT NULL DEFAULT '5000' COMMENT '调用延迟',
    `call_time` timestamp DEFAULT NULL COMMENT '调用时间',
    `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id` varchar(64) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `updater_id` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`history_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='api调用历史表';

-- ----------------------------
-- Table structure for st_job
-- ----------------------------
CREATE TABLE `st_job` (
    `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    `job_name` varchar(50) NOT NULL COMMENT '任务名称',
    `env_id` bigint NOT NULL COMMENT '环境ID',
    `engine_name` varchar(50) NOT NULL  COMMENT '引擎名称',
    `status` tinyint NOT NULL DEFAULT '0' COMMENT '任务状态',
    `description` varchar(200) COMMENT '任务描述',
    `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id` varchar(64) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `updater_id` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`job_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='数据集成任务表';

-- ----------------------------
-- Table structure for st_task
-- ----------------------------
CREATE TABLE `st_task` (
    `task_id` bigint NOT NULL AUTO_INCREMENT COMMENT '子任务ID',
    `task_name` varchar(50) NOT NULL COMMENT '子任务名称',
    `job_id` bigint NOT NULL COMMENT '任务ID',
    `connector_type` varchar(50) NOT NULL COMMENT '连接器名称',
    `datasource_id` bigint NOT NULL COMMENT '数据源id',
    `task_config` text COMMENT '子任务配置',
    `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id` varchar(64) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `updater_id` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`task_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='数据集成子任务表';

-- ----------------------------
-- Table structure for st_job_instance
-- ----------------------------
CREATE TABLE `st_job_instance` (
    `instance_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务实例ID',
    `instance_name` varchar(50) NOT NULL COMMENT '任务实例名称',
    `job_id` bigint NOT NULL COMMENT '任务ID',
    `engine_name` varchar(50) NOT NULL  COMMENT '引擎名称',
    `job_config` text COMMENT '任务配置',
    `status` tinyint NOT NULL DEFAULT '0' COMMENT '任务状态',
    `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id` varchar(64) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `updater_id` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`instance_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='数据集成任务实例表';

-- ----------------------------
-- quartz定时任务相关表
-- ----------------------------
-- 存储job详细信息
CREATE TABLE QRTZ_JOB_DETAILS (
    SCHED_NAME VARCHAR(120) NOT NULL,
    JOB_NAME VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    JOB_CLASS_NAME VARCHAR(250) NOT NULL,
    IS_DURABLE VARCHAR(1) NOT NULL,
    IS_NONCONCURRENT VARCHAR(1) NOT NULL,
    IS_UPDATE_DATA VARCHAR(1) NOT NULL,
    REQUESTS_RECOVERY VARCHAR(1) NOT NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
);

-- 存储触发器的基本信息
CREATE TABLE QRTZ_TRIGGERS (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    JOB_NAME VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    NEXT_FIRE_TIME BIGINT(13) NULL,
    PREV_FIRE_TIME BIGINT(13) NULL,
    PRIORITY INTEGER NULL,
    TRIGGER_STATE VARCHAR(16) NOT NULL,
    TRIGGER_TYPE VARCHAR(8) NOT NULL,
    START_TIME BIGINT(13) NOT NULL,
    END_TIME BIGINT(13) NULL,
    CALENDAR_NAME VARCHAR(200) NULL,
    MISFIRE_INSTR SMALLINT(2) NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
    REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP)
);

-- 存储简单的触发器
CREATE TABLE QRTZ_SIMPLE_TRIGGERS (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    REPEAT_COUNT BIGINT(7) NOT NULL,
    REPEAT_INTERVAL BIGINT(12) NOT NULL,
    TIMES_TRIGGERED BIGINT(10) NOT NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

-- 存储cron触发器
CREATE TABLE QRTZ_CRON_TRIGGERS (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    CRON_EXPRESSION VARCHAR(200) NOT NULL,
    TIME_ZONE_ID VARCHAR(80),
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

-- 存储触发器的属性
CREATE TABLE QRTZ_SIMPROP_TRIGGERS (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    STR_PROP_1 VARCHAR(512) NULL,
    STR_PROP_2 VARCHAR(512) NULL,
    STR_PROP_3 VARCHAR(512) NULL,
    INT_PROP_1 INT NULL,
    INT_PROP_2 INT NULL,
    LONG_PROP_1 BIGINT NULL,
    LONG_PROP_2 BIGINT NULL,
    DEC_PROP_1 NUMERIC(13,4) NULL,
    DEC_PROP_2 NUMERIC(13,4) NULL,
    BOOL_PROP_1 VARCHAR(1) NULL,
    BOOL_PROP_2 VARCHAR(1) NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

-- 存储Blob类型的触发器
CREATE TABLE QRTZ_BLOB_TRIGGERS (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    BLOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

-- 存储日历信息
CREATE TABLE QRTZ_CALENDARS (
    SCHED_NAME VARCHAR(120) NOT NULL,
    CALENDAR_NAME VARCHAR(200) NOT NULL,
    CALENDAR BLOB NOT NULL,
    PRIMARY KEY (SCHED_NAME,CALENDAR_NAME)
);

-- 存储已暂停的触发器组
CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP)
);

-- 存储已触发的触发器信息
CREATE TABLE QRTZ_FIRED_TRIGGERS (
    SCHED_NAME VARCHAR(120) NOT NULL,
    ENTRY_ID VARCHAR(95) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    FIRED_TIME BIGINT(13) NOT NULL,
    SCHED_TIME BIGINT(13) NOT NULL,
    PRIORITY INTEGER NOT NULL,
    STATE VARCHAR(16) NOT NULL,
    JOB_NAME VARCHAR(200) NULL,
    JOB_GROUP VARCHAR(200) NULL,
    IS_NONCONCURRENT VARCHAR(1) NULL,
    REQUESTS_RECOVERY VARCHAR(1) NULL,
    PRIMARY KEY (SCHED_NAME,ENTRY_ID)
);

-- 存储调度器状态信息
CREATE TABLE QRTZ_SCHEDULER_STATE (
    SCHED_NAME VARCHAR(120) NOT NULL,
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    LAST_CHECKIN_TIME BIGINT(13) NOT NULL,
    CHECKIN_INTERVAL BIGINT(13) NOT NULL,
    PRIMARY KEY (SCHED_NAME,INSTANCE_NAME)
);

-- 存储锁信息
CREATE TABLE QRTZ_LOCKS (
    SCHED_NAME VARCHAR(120) NOT NULL,
    LOCK_NAME VARCHAR(40) NOT NULL,
    PRIMARY KEY (SCHED_NAME,LOCK_NAME)
);

-- 创建索引
CREATE INDEX IDX_QRTZ_J_REQ_RECOVERY ON QRTZ_JOB_DETAILS(SCHED_NAME,REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_J_GRP ON QRTZ_JOB_DETAILS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_J ON QRTZ_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_JG ON QRTZ_TRIGGERS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_C ON QRTZ_TRIGGERS(SCHED_NAME,CALENDAR_NAME);
CREATE INDEX IDX_QRTZ_T_G ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_T_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_G_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NEXT_FIRE_TIME ON QRTZ_TRIGGERS(SCHED_NAME,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_MISFIRE ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE_GRP ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_FT_TRIG_INST_NAME ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME);
CREATE INDEX IDX_QRTZ_FT_INST_JOB_REQ_RCVRY ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_FT_J_G ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_JG ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_T_G ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_FT_TG ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);
