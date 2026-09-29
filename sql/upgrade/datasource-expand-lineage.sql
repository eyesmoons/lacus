-- 数据源模块扩展(数据源扩展/业务元数据/数据血缘)存量库升级脚本。
-- 全新环境直接执行 lacus.sql 即可,无需本脚本。

-- 1. 虚拟表标记:meta_table 增加表类别列,存量数据视为普通表
ALTER TABLE meta_table
    ADD COLUMN `table_type` varchar(20) NOT NULL DEFAULT 'NORMAL'
        COMMENT '表类别：NORMAL-普通表，VIRTUAL_KAFKA-Kafka虚拟表(Topic)，VIRTUAL_HDFS-HDFS虚拟表(目录)'
        AFTER `type`;

-- 2. 业务元数据 KV 表(biz_type + biz_id + obj_key 定位一条属性)
CREATE TABLE IF NOT EXISTS `business_metadata` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_type` varchar(20) NOT NULL COMMENT '业务对象类型：DATASOURCE-数据源，DB-数据库，TABLE-表，COLUMN-字段',
  `biz_id` varchar(128) NOT NULL COMMENT '业务对象ID；DB级为 datasourceId:dbName，其余为对应记录主键',
  `obj_key` varchar(50) NOT NULL COMMENT '属性键：businessName-业务名称，description-业务描述，owner-责任人，tags-标签(逗号分隔)',
  `obj_value` varchar(500) DEFAULT NULL COMMENT '属性值',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
  `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_biz` (`biz_type`,`biz_id`,`obj_key`),
  KEY `idx_biz_obj` (`biz_type`,`biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业务元数据KV表';

-- 3. 血缘节点表(一张元数据表一个节点)
CREATE TABLE IF NOT EXISTS `lineage_node` (
  `node_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '节点ID',
  `table_id` bigint(20) NOT NULL COMMENT '关联元数据表ID（meta_table.table_id）',
  `node_name` varchar(128) NOT NULL COMMENT '节点名称（表名）',
  `node_type` varchar(20) NOT NULL DEFAULT 'TABLE' COMMENT '节点类型：TABLE-普通表，VIRTUAL_TABLE-虚拟表，DATASOURCE-数据源',
  `datasource_id` bigint(20) DEFAULT NULL COMMENT '所属数据源ID',
  `db_name` varchar(128) DEFAULT NULL COMMENT '所属库名称快照',
  `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`node_id`),
  UNIQUE KEY `uk_table` (`table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='血缘节点表';

-- 4. 血缘连线表((源,目标)唯一,重复登记被数据库约束拦截)
CREATE TABLE IF NOT EXISTS `lineage_edge` (
  `edge_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '边ID',
  `source_node_id` bigint(20) NOT NULL COMMENT '源节点ID（上游/数据流出方）',
  `target_node_id` bigint(20) NOT NULL COMMENT '目标节点ID（下游/数据流入方）',
  `dep_type` varchar(20) NOT NULL DEFAULT 'DIRECT' COMMENT '依赖类型：DIRECT-直接依赖，TRANSFORM-转换依赖',
  `source_flag` varchar(10) NOT NULL DEFAULT 'MANUAL' COMMENT '登记来源：AUTO-自动解析，MANUAL-手动登记',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater_id` varchar(128) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`edge_id`),
  UNIQUE KEY `uk_src_tgt` (`source_node_id`,`target_node_id`),
  KEY `idx_target` (`target_node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='血缘连线表';

-- 5. 新增数据源插件(Hive 常规源,Kafka/HDFS 虚拟源;connectionParams 驱动前端动态表单)
INSERT INTO `meta_datasource_plugin`
    (`name`, `type`, `driver_name`, `icon`, `connection_params`, `remark`, `deleted`, `creator_id`, `create_time`)
VALUES
    ('HIVE', 1, 'org.apache.hive.jdbc.HiveDriver', 'Hive.png',
     '[{"name":"host","description":"HiveServer2地址","required":true},{"name":"port","description":"端口","required":true,"defaultValue":"10000"},{"name":"databaseName","description":"默认数据库","required":false,"defaultValue":"default"},{"name":"username","description":"用户名","required":false},{"name":"password","description":"密码","required":false}]',
     'Hive数据仓库', 0, '1', NOW()),
    ('KAFKA', 2, NULL, 'Kafka.png',
     '[{"name":"bootstrapServers","description":"Broker地址列表(逗号分隔)","required":true,"defaultValue":"localhost:9092"},{"name":"securityProtocol","description":"安全协议(PLAINTEXT/SASL_SSL等)","required":false,"defaultValue":"PLAINTEXT"}]',
     'Kafka虚拟数据源:Topic同步为虚拟表', 0, '1', NOW()),
    ('HDFS', 2, NULL, 'HDFS.png',
     '[{"name":"nameNodeUri","description":"NameNode URI(hdfs://host:8020)","required":true},{"name":"scanRootPath","description":"扫描根路径","required":true,"defaultValue":"/"},{"name":"user","description":"访问用户","required":false}]',
     'HDFS虚拟数据源:目录同步为虚拟表', 0, '1', NOW());

-- 6. 血缘图菜单与权限项(menu_id 接在现有最大值 2099 之后)
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,`path`,component,query,is_external,is_cache,menu_type,is_visible,status,perms,icon,creator_id,create_time,updater_id,update_time,remark,deleted) VALUES
(2100,'数据血缘',2052,4,'lineage','metadata/lineage/index',NULL,0,0,2,1,1,'metadata:lineage:view','chart',1,NOW(),NULL,NULL,'血缘图页面',0),
(2101,'业务元数据编辑',2055,1,'#','','',0,1,3,1,1,'metadata:bizmeta:edit','#',1,NOW(),NULL,NULL,'表详情/数据源业务元数据维护按钮',0),
(2102,'血缘登记',2100,1,'#','','',0,1,3,1,1,'metadata:lineage:edit','#',1,NOW(),NULL,NULL,'血缘登记/删除按钮',0);
