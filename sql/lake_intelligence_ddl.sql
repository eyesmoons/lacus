-- ============================================================
-- Lake Intelligence 模块 DDL
-- 数据库: MySQL 5.7+
-- 字符集: utf8mb4
-- 表名前缀: lake_
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for lake_datasets
-- 图片库元信息表
-- ----------------------------
DROP TABLE IF EXISTS `lake_datasets`;
CREATE TABLE `lake_datasets` (
  `dataset_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `dataset_name` varchar(128) NOT NULL COMMENT '图片库名称',
  `description` varchar(500) DEFAULT NULL COMMENT '图片库描述',
  `storage_source` varchar(32) NOT NULL COMMENT '存储来源: LOCAL/HDFS/S3/MINIO/HTTP',
  `source_config` text COMMENT '数据源配置 (JSON, 含路径/URL/凭证等)',
  `status` varchar(32) NOT NULL DEFAULT 'PROCESSING' COMMENT '状态: PROCESSING/WAITING_DOWNLOAD/DOWNLOADING/READY/ERROR',
  `image_count` int(11) DEFAULT '0' COMMENT '图片数量',
  `total_size_bytes` bigint(20) DEFAULT '0' COMMENT '总文件大小(字节)',
  `local_path` varchar(512) DEFAULT NULL COMMENT '本地存储路径',
  `error_message` varchar(1024) DEFAULT NULL COMMENT '错误信息',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`dataset_id`),
  KEY `idx_dataset_name` (`dataset_name`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图片库元信息表';

-- ----------------------------
-- Table structure for lake_tasks
-- 训练任务表
-- ----------------------------
DROP TABLE IF EXISTS `lake_tasks`;
CREATE TABLE `lake_tasks` (
  `task_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `task_name` varchar(128) NOT NULL COMMENT '任务名称',
  `task_type` varchar(64) NOT NULL COMMENT '任务类型: IMAGE_SIMILARITY',
  `dataset_id` bigint(20) NOT NULL COMMENT '关联图片库ID',
  `model_id` bigint(20) DEFAULT NULL COMMENT '关联模型ID',
  `status` varchar(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING/TRAINING/COMPLETED/FAILED/CANCELLED',
  `hyper_params' text COMMENT '超参数配置 (JSON, epochs/lr/batch_size等)',
  `training_progress` int(11) DEFAULT '0' COMMENT '训练进度百分比',
  `loss_history' text COMMENT '损失曲线数据 (JSON数组)',
  `error_message` varchar(1024) DEFAULT NULL COMMENT '错误信息',
  `started_at` datetime DEFAULT NULL COMMENT '开始时间',
  `completed_at` datetime DEFAULT NULL COMMENT '完成时间',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`task_id`),
  KEY `idx_task_name` (`task_name`),
  KEY `idx_dataset_id` (`dataset_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='训练任务表';

-- ----------------------------
-- Table structure for lake_model_info
-- 模型元信息表
-- ----------------------------
DROP TABLE IF EXISTS `lake_model_info`;
CREATE TABLE `lake_model_info` (
  `model_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `model_name` varchar(128) NOT NULL COMMENT '模型名称',
  `task_id` bigint(20) DEFAULT NULL COMMENT '关联训练任务ID',
  `dataset_id` bigint(20) NOT NULL COMMENT '关联图片库ID',
  `model_arch` varchar(64) NOT NULL DEFAULT 'SimilarityAutoEncoder' COMMENT '模型架构',
  `model_path` varchar(512) NOT NULL COMMENT '模型文件本地路径',
  `model_size_bytes` bigint(20) DEFAULT '0' COMMENT '模型文件大小(字节)',
  `embedding_dim` int(11) DEFAULT '512' COMMENT 'Embedding维度',
  `training_epochs` int(11) DEFAULT NULL COMMENT '实际训练轮数',
  `final_loss` decimal(10,6) DEFAULT NULL COMMENT '最终损失值',
  `status` varchar(32) DEFAULT 'TRAINING' COMMENT '模型状态: TRAINING/TRAINING_FAILED/TRAINING_COMPLETED',
  `vector_index_id` bigint(20) DEFAULT NULL COMMENT '关联向量库ID',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`model_id`),
  KEY `idx_model_name` (`model_name`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_dataset_id` (`dataset_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模型元信息表';

-- ----------------------------
-- Table structure for lake_vector_indexes
-- 向量库索引表
-- ----------------------------
DROP TABLE IF EXISTS `lake_vector_indexes`;
CREATE TABLE `lake_vector_indexes` (
  `index_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `index_name` varchar(128) NOT NULL COMMENT '向量库名称',
  `dataset_id` bigint(20) NOT NULL COMMENT '关联图片库ID',
  `model_id` bigint(20) NOT NULL COMMENT '关联模型ID',
  `index_path` varchar(512) NOT NULL COMMENT '向量索引本地路径',
  `total_vectors` int(11) DEFAULT '0' COMMENT '向量总数',
  `dimension` int(11) DEFAULT '512' COMMENT '向量维度',
  `distance_metric` varchar(32) DEFAULT 'cosine' COMMENT '距离度量: cosine/euclidean',
  `build_status` varchar(32) DEFAULT 'PENDING' COMMENT '构建状态: PENDING/BUILDING/COMPLETED/FAILED',
  `error_message` varchar(1024) DEFAULT NULL COMMENT '错误信息',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`index_id`),
  KEY `idx_index_name` (`index_name`),
  KEY `idx_dataset_id` (`dataset_id`),
  KEY `idx_model_id` (`model_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='向量库索引表';

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- Lake Intelligence 菜单数据
-- 插入到 sys_menu 表，菜单由后端动态管理
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 湖智AI (父目录)
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, `path`, component, is_external, is_cache, menu_type, is_visible, status, perms, icon, creator_id, create_time, remark, deleted)
VALUES (3000, '湖智AI', 0, 10, 'lake-intelligence', NULL, 0, 1, 1, 1, 1, NULL, 'cpu', 0, NOW(), '湖智AI', 0);

-- 以图搜图 (子目录)
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, `path`, component, is_external, is_cache, menu_type, is_visible, status, perms, icon, creator_id, create_time, remark, deleted)
VALUES (3001, '以图搜图', 3000, 1, 'similarity', NULL, 0, 1, 1, 1, 1, NULL, 'search', 0, NOW(), '以图搜图功能', 0);

-- 以图搜图 - 数据集管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, `path`, component, is_external, is_cache, menu_type, is_visible, status, perms, icon, creator_id, create_time, remark, deleted)
VALUES (3002, '数据集管理', 3001, 1, 'dataset/list', 'lake-intelligence/similarity/DatasetList', 0, 1, 2, 1, 1, 'lakeintelligence:dataset:list', 'folder', 0, NOW(), '数据集管理菜单', 0);

-- 以图搜图 - 模型管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, `path`, component, is_external, is_cache, menu_type, is_visible, status, perms, icon, creator_id, create_time, remark, deleted)
VALUES (3003, '模型管理', 3001, 2, 'model/list', 'lake-intelligence/similarity/ModelList', 0, 1, 2, 1, 1, 'lakeintelligence:model:list', 'cpu', 0, NOW(), '模型管理菜单', 0);

-- 以图搜图 - 向量构建
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, `path`, component, is_external, is_cache, menu_type, is_visible, status, perms, icon, creator_id, create_time, remark, deleted)
VALUES (3004, '向量构建', 3001, 3, 'vector/build', 'lake-intelligence/similarity/VectorBuild', 0, 1, 2, 1, 1, 'lakeintelligence:vector:build', 'connection', 0, NOW(), '向量构建菜单', 0);

-- 以图搜图 - 相似检索
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, `path`, component, is_external, is_cache, menu_type, is_visible, status, perms, icon, creator_id, create_time, remark, deleted)
VALUES (3005, '相似检索', 3001, 4, 'search', 'lake-intelligence/similarity/SimilaritySearch', 0, 1, 2, 1, 1, 'lakeintelligence:search:query', 'search', 0, NOW(), '相似检索菜单', 0);

-- 图片分类 (子目录)
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, `path`, component, is_external, is_cache, menu_type, is_visible, status, perms, icon, creator_id, create_time, remark, deleted)
VALUES (3006, '图片分类', 3000, 2, 'classification', NULL, 0, 1, 1, 1, 1, NULL, 'picture', 0, NOW(), '图片分类功能', 0);

-- 图片分类 - 数据集管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, `path`, component, is_external, is_cache, menu_type, is_visible, status, perms, icon, creator_id, create_time, remark, deleted)
VALUES (3007, '数据集管理', 3006, 1, 'dataset/list', 'lake-intelligence/classification/DatasetList', 0, 1, 2, 1, 1, 'lakeintelligence:dataset:list', 'folder', 0, NOW(), '数据集管理菜单', 0);

-- 图片分类 - 模型管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, `path`, component, is_external, is_cache, menu_type, is_visible, status, perms, icon, creator_id, create_time, remark, deleted)
VALUES (3008, '模型管理', 3006, 2, 'model/list', 'lake-intelligence/classification/ModelList', 0, 1, 2, 1, 1, 'lakeintelligence:model:list', 'cpu', 0, NOW(), '模型管理菜单', 0);

-- 图片分类 - 图片分类
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, `path`, component, is_external, is_cache, menu_type, is_visible, status, perms, icon, creator_id, create_time, remark, deleted)
VALUES (3009, '图片分类', 3006, 3, 'classify', 'lake-intelligence/classification/ImageClassify', 0, 1, 2, 1, 1, 'lakeintelligence:classify:predict', 'picture', 0, NOW(), '图片分类菜单', 0);

-- 按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, `path`, component, is_external, is_cache, menu_type, is_visible, status, perms, icon, creator_id, create_time, remark, deleted) VALUES
(3010, '数据集上传', 3002, 1, '', '', 0, 1, 3, 1, 1, 'lakeintelligence:dataset:add', '#', 0, NOW(), '', 0),
(3011, '数据集删除', 3002, 2, '', '', 0, 1, 3, 1, 1, 'lakeintelligence:dataset:remove', '#', 0, NOW(), '', 0),
(3012, '模型添加', 3003, 1, '', '', 0, 1, 3, 1, 1, 'lakeintelligence:model:add', '#', 0, NOW(), '', 0),
(3013, '模型删除', 3003, 2, '', '', 0, 1, 3, 1, 1, 'lakeintelligence:model:remove', '#', 0, NOW(), '', 0),
(3014, '向量构建触发', 3004, 1, '', '', 0, 1, 3, 1, 1, 'lakeintelligence:vector:build', '#', 0, NOW(), '', 0);

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 清理旧的湖智菜单数据（如果存在）
-- ============================================================
DELETE FROM sys_menu WHERE menu_id >= 3000 AND menu_id < 3100;
DELETE FROM sys_menu WHERE path LIKE 'lakeintelligence%';
DELETE FROM sys_menu WHERE path LIKE 'similarity%';
DELETE FROM sys_menu WHERE path LIKE 'classification%';
