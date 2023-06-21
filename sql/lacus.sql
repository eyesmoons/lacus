SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for data_sync_column_mapping
-- ----------------------------
DROP TABLE IF EXISTS `data_sync_column_mapping`;
CREATE TABLE `data_sync_column_mapping` (
  `column_mapping_id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `job_id` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务ID',
  `source_column_id` bigint NOT NULL COMMENT '输入源表字段ID',
  `sink_column_id` bigint NOT NULL COMMENT '输出源表字段ID',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`column_mapping_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字段映射信息';

-- ----------------------------
-- Table structure for data_sync_job
-- ----------------------------
DROP TABLE IF EXISTS `data_sync_job`;
CREATE TABLE `data_sync_job` (
  `job_id` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `job_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '任务名称',
  `catalog_id` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '分组ID',
  `source_datasource_id` bigint NOT NULL COMMENT '输入源ID',
  `sink_datasource_id` bigint NOT NULL COMMENT '输出源ID',
  `sync_type` tinyint NOT NULL DEFAULT '1' COMMENT '同步方式：1 初始快照，2 最早，3 最近，4 指定时间戳',
  `app_container` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'yarn' COMMENT '调度容器',
  `window_size` int NOT NULL COMMENT '窗口大小(秒)',
  `max_size` int NOT NULL COMMENT '最大数据量(MB)',
  `max_count` int NOT NULL COMMENT '最大数据条数(万条)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '任务描述',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数据同步任务主表';

-- ----------------------------
-- Table structure for data_sync_job_catalog
-- ----------------------------
DROP TABLE IF EXISTS `data_sync_job_catalog`;
CREATE TABLE `data_sync_job_catalog` (
  `catalog_id` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `catalog_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '分组名称',
  `job_manager` bigint DEFAULT NULL COMMENT 'jobManager内存，单位为GB',
  `task_manager` bigint DEFAULT NULL COMMENT 'taskManager内存，单位为GB',
  `remark` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '分组描述',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`catalog_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数据同步任务分组表';


-- ----------------------------
-- Table structure for data_sync_job_instance
-- ----------------------------
DROP TABLE IF EXISTS `data_sync_job_instance`;
CREATE TABLE `data_sync_job_instance` (
  `instance_id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `catalog_id` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务分组ID',
  `application_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'flink任务ID',
  `flink_job_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'flink任务ID',
  `type` tinyint NOT NULL DEFAULT '1' COMMENT '类型 1 source 2 sink',
  `sync_type` tinyint NOT NULL COMMENT '启动方式',
  `submit_time` datetime DEFAULT NULL COMMENT '任务提交时间',
  `finished_time` datetime DEFAULT NULL COMMENT '任务结束时间',
  `savepoint` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'savepoint地址',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '任务状态 1 RUNNING, 2 KILL, 3 FAILED',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`instance_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数据同步实例';


-- ----------------------------
-- Table structure for data_sync_sink_column
-- ----------------------------
DROP TABLE IF EXISTS `data_sync_sink_column`;
CREATE TABLE `data_sync_sink_column` (
  `sink_column_id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `job_id` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务ID',
  `sink_table_id` bigint NOT NULL COMMENT '输出源表ID',
  `sink_column_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '输出源字段名称',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`sink_column_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='输出源字段信息';

-- ----------------------------
-- Table structure for data_sync_sink_table
-- ----------------------------
DROP TABLE IF EXISTS `data_sync_sink_table`;
CREATE TABLE `data_sync_sink_table` (
  `sink_table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `job_id` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务ID',
  `sink_db_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '输出源库名称',
  `sink_table_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '输出源表名称',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`sink_table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='输出源表信息';

-- ----------------------------
-- Table structure for data_sync_source_column
-- ----------------------------
DROP TABLE IF EXISTS `data_sync_source_column`;
CREATE TABLE `data_sync_source_column` (
  `source_column_id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `job_id` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务ID',
  `source_table_id` bigint NOT NULL COMMENT '输入源表ID',
  `source_column_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '输入源字段名称',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`source_column_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='输入源字段信息';

-- ----------------------------
-- Table structure for data_sync_source_table
-- ----------------------------
DROP TABLE IF EXISTS `data_sync_source_table`;
CREATE TABLE `data_sync_source_table` (
  `source_table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `job_id` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务ID',
  `source_db_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '输入源库名称',
  `source_table_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '输入源表名称',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`source_table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='输入源表信息';

-- ----------------------------
-- Table structure for data_sync_table_mapping
-- ----------------------------
DROP TABLE IF EXISTS `data_sync_table_mapping`;
CREATE TABLE `data_sync_table_mapping` (
  `table_mapping_id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `job_id` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务ID',
  `source_table_id` bigint NOT NULL COMMENT '输入源表ID',
  `sink_table_id` bigint NOT NULL COMMENT '输出源表ID',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`table_mapping_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='表映射信息';

-- ----------------------------
-- Table structure for meta_column
-- ----------------------------
DROP TABLE IF EXISTS `meta_column`;
CREATE TABLE `meta_column` (
  `column_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `table_id` bigint NOT NULL COMMENT '表id',
  `column_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '字段名称',
  `data_type` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '数据类型',
  `column_type` varchar(150) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '字段类型',
  `numeric_precision` bigint unsigned DEFAULT NULL,
  `numeric_scale` bigint unsigned DEFAULT NULL,
  `column_length` int DEFAULT NULL COMMENT '字段长度',
  `comment` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '字段描述',
  `is_nullable` varchar(30) COLLATE utf8mb4_general_ci NOT NULL COMMENT '是否非空',
  `column_default` varchar(60) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '字段默认值',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater_id` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`column_id`),
  KEY `unique_key_column` (`table_id`,`column_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字段';

-- ----------------------------
-- Table structure for meta_datasource
-- ----------------------------
DROP TABLE IF EXISTS `meta_datasource`;
CREATE TABLE `meta_datasource` (
  `datasource_id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `datasource_name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '数据源名称',
  `type` varchar(10) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '数据源类型。(mysql,doris)',
  `source_type` tinyint DEFAULT NULL COMMENT '同步类型：1输入源，2输出源',
  `remark` varchar(100) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '数据源描述',
  `ip` varchar(100) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'ip/主机名',
  `port` int NOT NULL COMMENT '端口',
  `username` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户名',
  `password` varchar(30) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '密码',
  `default_db_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '默认数据库名',
  `connection_params` varchar(300) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '连接参数',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '数据源状态：启用 1，禁用 0',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`datasource_id`),
  KEY `unique_key_datasourcename` (`datasource_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数据源';

-- ----------------------------
-- Table structure for meta_db
-- ----------------------------
DROP TABLE IF EXISTS `meta_db`;
CREATE TABLE `meta_db` (
  `db_id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `db_name` varchar(80) COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据库名称',
  `datasource_id` bigint NOT NULL COMMENT '数据源ID',
  `comment` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater_id` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`db_id`),
  KEY `unique_key_db` (`datasource_id`,`db_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数据库';

-- ----------------------------
-- Table structure for meta_table
-- ----------------------------
DROP TABLE IF EXISTS `meta_table`;
CREATE TABLE `meta_table` (
  `table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `db_id` bigint NOT NULL COMMENT '数据库id',
  `table_name` varchar(300) COLLATE utf8mb4_general_ci NOT NULL COMMENT '表名称',
  `comment` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '表描述',
  `type` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '表类型',
  `engine` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '引擎',
  `table_create_time` datetime DEFAULT NULL COMMENT '表创建时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater_id` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`table_id`),
  KEY `unique_key_tbl` (`db_id`,`table_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数据表';

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `config_id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '配置名称',
  `config_key` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '配置键名',
  `config_options` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '可选的选项',
  `config_value` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '配置值',
  `is_allow_change` tinyint(1) NOT NULL COMMENT '是否允许修改',
  `creator_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `updater_id` bigint DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`config_id`),
  UNIQUE KEY `config_key_uniq_idx` (`config_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='参数配置表';

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
  `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父部门id',
  `ancestors` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '祖级列表',
  `dept_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '部门名称',
  `order_num` int NOT NULL DEFAULT '0' COMMENT '显示顺序',
  `leader_id` bigint DEFAULT NULL,
  `leader_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '负责人',
  `phone` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系电话',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
  `status` smallint NOT NULL DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `creator_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater_id` bigint DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=205 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='部门表';

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
  `info_id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户账号',
  `ip_address` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '浏览器类型',
  `operation_system` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '操作系统',
  `status` smallint NOT NULL DEFAULT '0' COMMENT '登录状态（1成功 0失败）',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '提示消息',
  `login_time` datetime DEFAULT NULL COMMENT '访问时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`info_id`)
) ENGINE=InnoDB AUTO_INCREMENT=555 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统访问记录';

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int NOT NULL DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '路由参数',
  `is_external` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否为外链（1是 0否）',
  `is_cache` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否缓存（1缓存 0不缓存）',
  `menu_type` smallint NOT NULL DEFAULT '0' COMMENT '菜单类型（M=1目录 C=2菜单 F=3按钮）',
  `is_visible` tinyint(1) NOT NULL DEFAULT '0' COMMENT '菜单状态（1显示 0隐藏）',
  `status` smallint NOT NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '#' COMMENT '菜单图标',
  `creator_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater_id` bigint DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '备注',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2015 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜单权限表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (1, '系统管理', 0, 2, 'system', NULL, '', 0, 1, 1, 1, 1, '', 'system', 0, '2022-05-21 08:30:54', 1, '2023-04-24 21:51:14', '系统管理目录', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2, '系统监控', 0, 3, 'monitor', NULL, '', 0, 1, 1, 1, 1, '', 'monitor', 0, '2022-05-21 08:30:54', 1, '2023-04-24 21:51:27', '系统监控目录', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (3, '系统工具', 0, 4, 'tool', NULL, '', 0, 1, 1, 1, 1, '', 'tool', 0, '2022-05-21 08:30:54', 1, '2023-04-24 21:51:30', '系统工具目录', 1);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (4, '官网文档', 0, 5, 'https://juejin.cn/column/7159946528827080734', NULL, '', 1, 1, 1, 1, 1, '', 'guide', 0, '2022-05-21 08:30:54', 1, '2023-04-26 09:14:48', 'pandora官网地址', 1);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (5, '用户管理', 1, 1, 'user', 'system/user/index', '', 0, 1, 2, 1, 1, 'system:user:list', 'user', 0, '2022-05-21 08:30:54', NULL, NULL, '用户管理菜单', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (6, '角色管理', 1, 2, 'role', 'system/role/index', '', 0, 1, 2, 1, 1, 'system:role:list', 'peoples', 0, '2022-05-21 08:30:54', NULL, NULL, '角色管理菜单', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (7, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', 0, 1, 2, 1, 1, 'system:menu:list', 'tree-table', 0, '2022-05-21 08:30:54', NULL, NULL, '菜单管理菜单', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (8, '部门管理', 1, 4, 'dept', 'system/dept/index', '', 0, 1, 2, 1, 1, 'system:dept:list', 'tree', 0, '2022-05-21 08:30:54', NULL, NULL, '部门管理菜单', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (9, '岗位管理', 1, 5, 'post', 'system/post/index', '', 0, 1, 2, 1, 1, 'system:post:list', 'post', 0, '2022-05-21 08:30:54', NULL, NULL, '岗位管理菜单', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (10, '参数设置', 1, 7, 'config', 'system/config/index', '', 0, 1, 2, 1, 1, 'system:config:list', 'edit', 0, '2022-05-21 08:30:54', NULL, NULL, '参数设置菜单', 1);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (11, '通知公告', 1, 8, 'notice', 'system/notice/index', '', 0, 1, 2, 1, 1, 'system:notice:list', 'message', 0, '2022-05-21 08:30:54', NULL, NULL, '通知公告菜单', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (12, '日志管理', 1, 9, 'log', '', '', 0, 1, 1, 1, 1, '', 'log', 0, '2022-05-21 08:30:54', NULL, NULL, '日志管理菜单', 0);
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
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2007, 'demo', 1, 1, 'demo', 'system/demo/index', NULL, 0, 0, 2, 1, 1, 'system:demo:list', '#', 1, '2023-02-17 16:23:41', 1, '2023-02-17 16:25:00', '', 1);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2009, '元数据管理', 0, 1, 'metadata', NULL, NULL, 0, 0, 1, 1, 1, NULL, 'redis', 1, '2023-04-20 18:11:10', 1, '2023-04-24 21:53:24', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2010, '数据源管理', 2009, 1, 'datasource', 'metadata/datasource/index', NULL, 0, 1, 2, 1, 1, 'metadata:datasource:list', 'dict', 1, '2023-04-20 18:13:53', 1, '2023-05-30 17:09:02', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2011, '数据表查询', 2009, 2, 'table', 'metadata/table/index', NULL, 0, 1, 2, 1, 1, 'metadata:table:list', 'table', 1, '2023-04-24 17:58:27', 1, '2023-05-30 17:09:05', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2012, '数据同步', 0, 4, 'datasync', NULL, NULL, 0, 0, 1, 1, 1, NULL, 'example', 1, '2023-05-10 17:36:09', 1, '2023-05-10 17:37:40', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2013, '分组管理', 2012, 1, 'catalog', 'datasync/catalog/index', NULL, 0, 0, 2, 1, 1, 'datasync:catalog:list', 'tool', 1, '2023-05-10 17:43:08', 1, '2023-05-14 18:58:19', '', 0);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_external`, `is_cache`, `menu_type`, `is_visible`, `status`, `perms`, `icon`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2014, '任务管理', 2012, 2, 'job', 'datasync/job/index', NULL, 0, 0, 2, 1, 1, 'datasync:job:list', 'select', 1, '2023-05-11 18:14:36', 1, '2023-05-14 18:58:28', '', 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice` (
  `notice_id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告标题',
  `notice_type` smallint NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '公告内容',
  `status` smallint NOT NULL DEFAULT '0' COMMENT '公告状态（1正常 0关闭）',
  `creator_id` bigint NOT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater_id` bigint DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='通知公告表';

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
BEGIN;
INSERT INTO `sys_notice` (`notice_id`, `notice_title`, `notice_type`, `notice_content`, `status`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (1, '温馨提醒：2018-07-01 Pandora新版本发布啦', 2, '新版本内容~~~~~~~~~~', 1, 1, '2022-05-21 08:30:55', 1, '2022-08-29 20:12:37', '管理员', 0);
INSERT INTO `sys_notice` (`notice_id`, `notice_title`, `notice_type`, `notice_content`, `status`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2, '维护通知：2018-07-01 Pandora系统凌晨维护', 1, '维护内容', 1, 1, '2022-05-21 08:30:55', NULL, NULL, '管理员', 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log` (
  `operation_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `business_type` smallint NOT NULL DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `request_method` smallint NOT NULL DEFAULT '0' COMMENT '请求方式',
  `request_module` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '请求模块',
  `request_url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '请求URL',
  `called_method` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '调用方法',
  `operator_type` smallint NOT NULL DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `user_id` bigint DEFAULT '0' COMMENT '用户ID',
  `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作人员',
  `operator_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作人员ip',
  `operator_location` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作地点',
  `dept_id` bigint DEFAULT '0' COMMENT '部门ID',
  `dept_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '部门名称',
  `operation_param` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '请求参数',
  `operation_result` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '返回参数',
  `status` smallint NOT NULL DEFAULT '1' COMMENT '操作状态（1正常 0异常）',
  `error_stack` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '错误消息',
  `operation_time` datetime NOT NULL COMMENT '操作时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`operation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=703 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作日志记录';

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post` (
  `post_id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位编码',
  `post_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位名称',
  `post_sort` int NOT NULL COMMENT '显示顺序',
  `status` smallint NOT NULL COMMENT '状态（1正常 0停用）',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `creator_id` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater_id` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='岗位信息表';

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
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` smallint DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3: 本部门数据权限 4: 本部门及以下数据权限 5: 本人权限）',
  `dept_id_set` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '角色所拥有的部门数据权限',
  `status` smallint NOT NULL COMMENT '角色状态（1正常 0停用）',
  `creator_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater_id` bigint DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色信息表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_role` (`role_id`, `role_name`, `role_key`, `role_sort`, `data_scope`, `dept_id_set`, `status`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (1, '超级管理员', 'admin', 1, 1, '', 1, NULL, '2022-05-21 08:30:54', NULL, NULL, '超级管理员', 0);
INSERT INTO `sys_role` (`role_id`, `role_name`, `role_key`, `role_sort`, `data_scope`, `dept_id_set`, `status`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2, '普通角色', 'common', 2, 2, '4,6', 1, NULL, '2022-05-21 08:30:54', 1, '2023-05-06 14:28:42', '普通角色', 0);
INSERT INTO `sys_role` (`role_id`, `role_name`, `role_key`, `role_sort`, `data_scope`, `dept_id_set`, `status`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (3, '闲置角色', 'unused', 4, 2, '', 0, NULL, '2022-05-21 08:30:54', NULL, NULL, '未使用的角色', 1);
COMMIT;

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色和菜单关联表';

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 1);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 2);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 5);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 6);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 7);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 8);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 9);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 11);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 12);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 13);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 14);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 15);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 16);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 18);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 19);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 20);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 21);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 22);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 23);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 24);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 25);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 26);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 27);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 28);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 29);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 30);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 31);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 32);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 33);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 34);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 35);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 36);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 37);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 38);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 39);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 40);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 41);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 42);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 43);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 44);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 50);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 51);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 52);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 53);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 54);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 55);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 56);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 57);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 58);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 59);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 60);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 61);
COMMIT;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `post_id` bigint DEFAULT NULL COMMENT '职位id',
  `role_id` bigint DEFAULT NULL COMMENT '角色id',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户昵称',
  `user_type` smallint DEFAULT '0' COMMENT '用户类型（00系统用户）',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '用户邮箱',
  `phone_number` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '手机号码',
  `sex` smallint DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '头像地址',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '密码',
  `status` smallint NOT NULL DEFAULT '0' COMMENT '帐号状态（1正常 2停用 3冻结）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `creator_id` bigint DEFAULT NULL COMMENT '更新者ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater_id` bigint DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户信息表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` (`user_id`, `post_id`, `role_id`, `dept_id`, `username`, `nick_name`, `user_type`, `email`, `phone_number`, `sex`, `avatar`, `password`, `status`, `login_ip`, `login_date`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (1, 1, 1, 4, 'admin', 'valarchie1', 0, 'pandora@163.com', '15888888889', 0, '', '$2a$10$rb1wRoEIkLbIknREEN1LH.FGs4g0oOS5t6l5LQ793nRaFO.SPHDHy', 1, '0:0:0:0:0:0:0:1', '2023-06-16 15:56:09', NULL, '2022-05-21 08:30:54', 1, '2023-06-16 15:56:09', '管理员', 0);
INSERT INTO `sys_user` (`user_id`, `post_id`, `role_id`, `dept_id`, `username`, `nick_name`, `user_type`, `email`, `phone_number`, `sex`, `avatar`, `password`, `status`, `login_ip`, `login_date`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (2, 2, 2, 5, 'ag1', 'valarchie2', 0, 'pandora@qq.com', '15666666666', 1, '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 1, '127.0.0.1', '2022-05-21 08:30:54', NULL, '2022-05-21 08:30:54', NULL, NULL, '测试员1', 1);
INSERT INTO `sys_user` (`user_id`, `post_id`, `role_id`, `dept_id`, `username`, `nick_name`, `user_type`, `email`, `phone_number`, `sex`, `avatar`, `password`, `status`, `login_ip`, `login_date`, `creator_id`, `create_time`, `updater_id`, `update_time`, `remark`, `deleted`) VALUES (3, 2, 0, 5, 'ag2', 'valarchie3', 0, 'pandora@qq.com', '15666666667', 1, '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 1, '127.0.0.1', '2022-05-21 08:30:54', NULL, '2022-05-21 08:30:54', NULL, NULL, '测试员2', 1);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
