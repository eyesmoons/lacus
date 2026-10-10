SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

--
-- Table structure for table `alert_channel_instance`
--

DROP TABLE IF EXISTS `alert_channel_instance`;
CREATE TABLE `alert_channel_instance`
(
    `id`              bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `channel_type_id` bigint       NOT NULL COMMENT '渠道类型ID',
    `instance_code`   varchar(64)  NOT NULL COMMENT '实例编码',
    `instance_name`   varchar(128) NOT NULL COMMENT '实例名称',
    `config_json`     longtext COMMENT '实例配置JSON',
    `enabled`         tinyint      NOT NULL DEFAULT '1' COMMENT '是否启用',
    `test_status`     varchar(16)  NOT NULL DEFAULT 'UNTESTED' COMMENT '测试状态',
    `last_test_time`  datetime(3)           DEFAULT NULL COMMENT '最近测试时间',
    `version`         int          NOT NULL DEFAULT '0' COMMENT '版本号',
    `creator_id`      varchar(64)           DEFAULT NULL,
    `create_time`     datetime(3)           DEFAULT CURRENT_TIMESTAMP(3),
    `updater_id`      varchar(64)           DEFAULT NULL,
    `update_time`     datetime(3)           DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    `deleted`         tinyint      NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_alert_channel_instance_code` (`instance_code`),
    UNIQUE KEY `uk_alert_channel_instance_name` (`instance_name`),
    KEY `idx_alert_channel_instance_type` (`channel_type_id`, `enabled`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='告警实例表';

--
-- Table structure for table `alert_channel_type`
--

DROP TABLE IF EXISTS `alert_channel_type`;
CREATE TABLE `alert_channel_type`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `type_code`     varchar(64)  NOT NULL COMMENT '渠道编码',
    `type_name`     varchar(128) NOT NULL COMMENT '渠道名称',
    `notifier_bean` varchar(255) NOT NULL COMMENT '通知插件实现类',
    `config_schema` json                  DEFAULT NULL COMMENT '动态表单Schema',
    `enabled`       tinyint      NOT NULL DEFAULT '1' COMMENT '是否启用',
    `sort_order`    int          NOT NULL DEFAULT '0' COMMENT '排序值',
    `remark`        varchar(255)          DEFAULT NULL COMMENT '备注',
    `creator_id`    varchar(64)           DEFAULT NULL,
    `create_time`   datetime(3)           DEFAULT CURRENT_TIMESTAMP(3),
    `updater_id`    varchar(64)           DEFAULT NULL,
    `update_time`   datetime(3)           DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    `deleted`       tinyint      NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_alert_channel_type_code` (`type_code`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='告警渠道类型表';

--
-- Table structure for table `alert_group`
--

DROP TABLE IF EXISTS `alert_group`;
CREATE TABLE `alert_group`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `group_code`  varchar(64)  NOT NULL COMMENT '告警组编码',
    `group_name`  varchar(128) NOT NULL COMMENT '告警组名称',
    `description` varchar(255)          DEFAULT NULL COMMENT '描述',
    `enabled`     tinyint      NOT NULL DEFAULT '1' COMMENT '是否启用',
    `creator_id`  varchar(64)           DEFAULT NULL,
    `create_time` datetime(3)           DEFAULT CURRENT_TIMESTAMP(3),
    `updater_id`  varchar(64)           DEFAULT NULL,
    `update_time` datetime(3)           DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    `deleted`     tinyint      NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_alert_group_code` (`group_code`),
    UNIQUE KEY `uk_alert_group_name` (`group_name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='告警组表';

--
-- Table structure for table `alert_group_channel_rel`
--

DROP TABLE IF EXISTS `alert_group_channel_rel`;
CREATE TABLE `alert_group_channel_rel`
(
    `id`                  bigint  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `group_id`            bigint  NOT NULL COMMENT '告警组ID',
    `channel_instance_id` bigint  NOT NULL COMMENT '告警实例ID',
    `notify_order`        int     NOT NULL DEFAULT '1' COMMENT '发送顺序',
    `enabled`             tinyint NOT NULL DEFAULT '1' COMMENT '是否启用',
    `creator_id`          varchar(64)      DEFAULT NULL,
    `create_time`         datetime(3)      DEFAULT CURRENT_TIMESTAMP(3),
    `updater_id`          varchar(64)      DEFAULT NULL,
    `update_time`         datetime(3)      DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    `deleted`             tinyint NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_alert_group_channel_rel` (`group_id`, `channel_instance_id`),
    KEY `idx_alert_group_channel_rel_channel` (`channel_instance_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 6
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='告警组与实例关系表';

--
-- Table structure for table `alert_record`
--

DROP TABLE IF EXISTS `alert_record`;
CREATE TABLE `alert_record`
(
    `id`             bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `record_no`      varchar(64)  NOT NULL COMMENT '告警记录编号',
    `group_id`       bigint       NOT NULL COMMENT '告警组ID',
    `group_code`     varchar(64)  NOT NULL COMMENT '告警组编码快照',
    `group_name`     varchar(128) NOT NULL COMMENT '告警组名称快照',
    `trigger_source` varchar(16)  NOT NULL COMMENT '触发来源',
    `biz_key`        varchar(128)          DEFAULT NULL COMMENT '业务键',
    `alert_level`    varchar(16)  NOT NULL COMMENT '告警级别',
    `title`          varchar(512) NOT NULL COMMENT '告警标题',
    `content`        text         NOT NULL COMMENT '告警正文',
    `ext_json`       longtext COMMENT '扩展信息',
    `status`         varchar(32)  NOT NULL DEFAULT 'PENDING' COMMENT '主记录状态',
    `channel_count`  int          NOT NULL DEFAULT '0' COMMENT '实例数',
    `success_count`  int          NOT NULL DEFAULT '0' COMMENT '成功数',
    `failed_count`   int          NOT NULL DEFAULT '0' COMMENT '失败数',
    `requested_by`   varchar(64)  NOT NULL COMMENT '触发人',
    `requested_time` datetime(3)  NOT NULL COMMENT '触发时间',
    `finished_time`  datetime(3)           DEFAULT NULL COMMENT '完成时间',
    `error_message`  varchar(1000)         DEFAULT NULL COMMENT '汇总错误信息',
    `creator_id`     varchar(64)           DEFAULT NULL,
    `create_time`    datetime(3)           DEFAULT CURRENT_TIMESTAMP(3),
    `updater_id`     varchar(64)           DEFAULT NULL,
    `update_time`    datetime(3)           DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    `deleted`        tinyint      NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_alert_record_no` (`record_no`),
    KEY `idx_alert_record_status_time` (`status`, `requested_time`),
    KEY `idx_alert_record_group_time` (`group_id`, `requested_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='告警主记录表';

--
-- Table structure for table `alert_send_log`
--

DROP TABLE IF EXISTS `alert_send_log`;
CREATE TABLE `alert_send_log`
(
    `id`               bigint  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `task_id`          bigint  NOT NULL COMMENT '发送任务ID',
    `attempt_no`       int     NOT NULL COMMENT '第几次尝试',
    `request_payload`  longtext COMMENT '请求内容',
    `response_payload` longtext COMMENT '响应内容',
    `success`          tinyint NOT NULL DEFAULT '0' COMMENT '是否成功',
    `cost_ms`          int     NOT NULL DEFAULT '0' COMMENT '耗时毫秒',
    `error_message`    varchar(1000)    DEFAULT NULL COMMENT '错误信息',
    `creator_id`       varchar(64)      DEFAULT NULL,
    `create_time`      datetime(3)      DEFAULT CURRENT_TIMESTAMP(3),
    `updater_id`       varchar(64)      DEFAULT NULL,
    `update_time`      datetime(3)      DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    `deleted`          tinyint NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`),
    KEY `idx_alert_send_log_task` (`task_id`, `attempt_no`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='告警发送日志表';

--
-- Table structure for table `alert_send_task`
--

DROP TABLE IF EXISTS `alert_send_task`;
CREATE TABLE `alert_send_task`
(
    `id`                  bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `task_no`             varchar(64)  NOT NULL COMMENT '任务编号',
    `record_id`           bigint       NOT NULL COMMENT '主记录ID',
    `channel_instance_id` bigint       NOT NULL COMMENT '告警实例ID',
    `channel_type_code`   varchar(64)  NOT NULL COMMENT '渠道类型编码',
    `instance_code`       varchar(64)  NOT NULL COMMENT '实例编码快照',
    `instance_name`       varchar(128) NOT NULL COMMENT '实例名称快照',
    `status`              varchar(16)  NOT NULL DEFAULT 'WAITING' COMMENT '发送状态',
    `retry_count`         int          NOT NULL DEFAULT '0' COMMENT '已重试次数',
    `max_retry_count`     int          NOT NULL DEFAULT '3' COMMENT '最大重试次数',
    `next_retry_time`     datetime(3)           DEFAULT NULL COMMENT '下次重试时间',
    `started_time`        datetime(3)           DEFAULT NULL COMMENT '开始发送时间',
    `finished_time`       datetime(3)           DEFAULT NULL COMMENT '结束发送时间',
    `response_summary`    varchar(1000)         DEFAULT NULL COMMENT '响应摘要',
    `last_error`          varchar(1000)         DEFAULT NULL COMMENT '最后错误',
    `creator_id`          varchar(64)           DEFAULT NULL,
    `create_time`         datetime(3)           DEFAULT CURRENT_TIMESTAMP(3),
    `updater_id`          varchar(64)           DEFAULT NULL,
    `update_time`         datetime(3)           DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    `deleted`             tinyint      NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_alert_send_task_no` (`task_no`),
    UNIQUE KEY `uk_alert_send_task_record_channel` (`record_id`, `channel_instance_id`),
    KEY `idx_alert_send_task_status_time` (`status`, `next_retry_time`, `create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='告警发送任务表';

--
-- Table structure for table `business_metadata`
--

DROP TABLE IF EXISTS `business_metadata`;
CREATE TABLE `business_metadata`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `biz_type`    varchar(20)  NOT NULL COMMENT '业务对象类型：DATASOURCE-数据源，DB-数据库，TABLE-表，COLUMN-字段',
    `biz_id`      varchar(128) NOT NULL COMMENT '业务对象ID；DB级为 datasourceId:dbName，其余为对应记录主键',
    `obj_key`     varchar(50)  NOT NULL COMMENT '属性键：businessName-业务名称，description-业务描述，owner-责任人，tags-标签(逗号分隔)',
    `obj_value`   varchar(500)          DEFAULT NULL COMMENT '属性值',
    `deleted`     tinyint      NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`  varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater_id`  varchar(128)          DEFAULT NULL COMMENT '修改人',
    `update_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_biz` (`biz_type`, `biz_id`, `obj_key`),
    KEY `idx_biz_obj` (`biz_type`, `biz_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 12
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='业务元数据KV表';

--
-- Table structure for table `data_sync_column_mapping`
--

DROP TABLE IF EXISTS `data_sync_column_mapping`;
CREATE TABLE `data_sync_column_mapping`
(
    `column_mapping_id` bigint      NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `job_id`            varchar(64) NOT NULL COMMENT '任务ID',
    `source_column_id`  bigint      NOT NULL COMMENT '输入源表字段ID',
    `sink_column_id`    bigint      NOT NULL COMMENT '输出源表字段ID',
    `deleted`           tinyint     NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`        varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`       datetime    NOT NULL COMMENT '创建时间',
    `updater_id`        varchar(128)         DEFAULT NULL COMMENT '修改人',
    `update_time`       timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`column_mapping_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 55
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='字段映射信息';

--
-- Table structure for table `data_sync_job`
--

DROP TABLE IF EXISTS `data_sync_job`;
CREATE TABLE `data_sync_job`
(
    `job_id`               bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `job_name`             varchar(100) NOT NULL DEFAULT '' COMMENT '任务名称',
    `catalog_id`           varchar(64)  NOT NULL COMMENT '分组ID',
    `source_datasource_id` bigint       NOT NULL COMMENT '输入源ID',
    `sink_datasource_id`   bigint       NOT NULL COMMENT '输出源ID',
    `job_manager`          bigint                DEFAULT NULL COMMENT 'jobManager内存，单位为GB',
    `task_manager`         bigint                DEFAULT NULL COMMENT 'taskManager内存，单位为GB',
    `window_size`          int          NOT NULL COMMENT '窗口大小(秒)',
    `max_size`             int          NOT NULL COMMENT '最大数据量(MB)',
    `max_count`            int          NOT NULL COMMENT '最大数据条数(万条)',
    `remark`               varchar(500) NOT NULL DEFAULT '' COMMENT '任务描述',
    `deleted`              tinyint      NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`           varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`          datetime     NOT NULL COMMENT '创建时间',
    `updater_id`           varchar(128)          DEFAULT NULL COMMENT '修改人',
    `update_time`          timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`job_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='数据采集任务主表';

--
-- Table structure for table `data_sync_job_catalog`
--

DROP TABLE IF EXISTS `data_sync_job_catalog`;
CREATE TABLE `data_sync_job_catalog`
(
    `catalog_id`   bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `catalog_name` varchar(50)  NOT NULL DEFAULT '' COMMENT '分组名称',
    `job_manager`  bigint                DEFAULT NULL COMMENT 'jobManager内存，单位为GB',
    `task_manager` bigint                DEFAULT NULL COMMENT 'taskManager内存，单位为GB',
    `remark`       varchar(300) NOT NULL DEFAULT '' COMMENT '分组描述',
    `deleted`      tinyint      NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`   varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`  datetime     NOT NULL COMMENT '创建时间',
    `updater_id`   varchar(128)          DEFAULT NULL COMMENT '修改人',
    `update_time`  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`catalog_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='数据采集任务分组表';

--
-- Table structure for table `data_sync_job_instance`
--

DROP TABLE IF EXISTS `data_sync_job_instance`;
CREATE TABLE `data_sync_job_instance`
(
    `instance_id`    bigint       NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `job_id`         bigint       NOT NULL COMMENT '任务ID',
    `instance_name`  varchar(150) NOT NULL DEFAULT '' COMMENT '实例名称',
    `application_id` varchar(100)          DEFAULT NULL COMMENT 'flink任务ID',
    `flink_job_id`   varchar(100)          DEFAULT NULL COMMENT 'flink任务ID',
    `job_script`     longtext COMMENT '任务脚本',
    `sync_type`      varchar(50)           DEFAULT NULL COMMENT '同步方式：INITIAL，TIMESTAMP，RESUME',
    `time_stamp`     varchar(100)          DEFAULT NULL COMMENT '指定时间戳',
    `submit_time`    datetime              DEFAULT NULL COMMENT '任务提交时间',
    `finished_time`  datetime              DEFAULT NULL COMMENT '任务结束时间',
    `save_point`     varchar(200)          DEFAULT NULL COMMENT 'savepoint地址',
    `status`         varchar(10)  NOT NULL DEFAULT '1' COMMENT '任务状态 RUNNING, KILL, FAILED',
    `deleted`        tinyint      NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`     varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`    datetime     NOT NULL COMMENT '创建时间',
    `updater_id`     varchar(128)          DEFAULT NULL COMMENT '修改人',
    `update_time`    timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`instance_id`) USING BTREE,
    KEY `idx_submit_time_status` (`submit_time`, `status`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 18
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC COMMENT ='数据采集实例';

--
-- Table structure for table `data_sync_sink_column`
--

DROP TABLE IF EXISTS `data_sync_sink_column`;
CREATE TABLE `data_sync_sink_column`
(
    `sink_column_id`   bigint       NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `job_id`           varchar(64)  NOT NULL COMMENT '任务ID',
    `sink_table_id`    bigint       NOT NULL COMMENT '输出源表ID',
    `sink_column_name` varchar(100) NOT NULL COMMENT '输出源字段名称',
    `deleted`          tinyint      NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`       varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`      datetime     NOT NULL COMMENT '创建时间',
    `updater_id`       varchar(128)          DEFAULT NULL COMMENT '修改人',
    `update_time`      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`sink_column_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 55
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='输出源字段信息';

--
-- Table structure for table `data_sync_sink_table`
--

DROP TABLE IF EXISTS `data_sync_sink_table`;
CREATE TABLE `data_sync_sink_table`
(
    `sink_table_id`   bigint       NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `job_id`          varchar(64)  NOT NULL COMMENT '任务ID',
    `sink_db_name`    varchar(100) NOT NULL COMMENT '输出源库名称',
    `sink_table_name` varchar(100) NOT NULL COMMENT '输出源表名称',
    `deleted`         tinyint      NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`      varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`     datetime     NOT NULL COMMENT '创建时间',
    `updater_id`      varchar(128)          DEFAULT NULL COMMENT '修改人',
    `update_time`     timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`sink_table_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 9
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='输出源表信息';

--
-- Table structure for table `data_sync_source_column`
--

DROP TABLE IF EXISTS `data_sync_source_column`;
CREATE TABLE `data_sync_source_column`
(
    `source_column_id`   bigint       NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `job_id`             varchar(64)  NOT NULL COMMENT '任务ID',
    `source_table_id`    bigint       NOT NULL COMMENT '输入源表ID',
    `source_column_name` varchar(100) NOT NULL COMMENT '输入源字段名称',
    `deleted`            tinyint      NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`         varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`        datetime     NOT NULL COMMENT '创建时间',
    `updater_id`         varchar(128)          DEFAULT NULL COMMENT '修改人',
    `update_time`        timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`source_column_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 55
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='输入源字段信息';

--
-- Table structure for table `data_sync_source_table`
--

DROP TABLE IF EXISTS `data_sync_source_table`;
CREATE TABLE `data_sync_source_table`
(
    `source_table_id`   bigint       NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `job_id`            varchar(64)  NOT NULL COMMENT '任务ID',
    `source_db_name`    varchar(100) NOT NULL COMMENT '输入源库名称',
    `source_table_name` varchar(100) NOT NULL COMMENT '输入源表名称',
    `deleted`           tinyint      NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`        varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`       datetime     NOT NULL COMMENT '创建时间',
    `updater_id`        varchar(128)          DEFAULT NULL COMMENT '修改人',
    `update_time`       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`source_table_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 9
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='输入源表信息';

--
-- Table structure for table `data_sync_table_mapping`
--

DROP TABLE IF EXISTS `data_sync_table_mapping`;
CREATE TABLE `data_sync_table_mapping`
(
    `table_mapping_id` bigint      NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `job_id`           varchar(64) NOT NULL COMMENT '任务ID',
    `source_table_id`  bigint      NOT NULL COMMENT '输入源表ID',
    `sink_table_id`    bigint      NOT NULL COMMENT '输出源表ID',
    `deleted`          tinyint     NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`       varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`      datetime    NOT NULL COMMENT '创建时间',
    `updater_id`       varchar(128)         DEFAULT NULL COMMENT '修改人',
    `update_time`      timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`table_mapping_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 9
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='表映射信息';

--
-- Table structure for table `dq_check_result`
--

DROP TABLE IF EXISTS `dq_check_result`;
CREATE TABLE `dq_check_result`
(
    `id`             bigint NOT NULL AUTO_INCREMENT COMMENT '结果ID',
    `log_id`         bigint NOT NULL COMMENT '关联执行记录ID（dq_execution_log.id）',
    `rule_id`        bigint NOT NULL COMMENT '规则ID',
    `rule_name`      varchar(128)   DEFAULT NULL COMMENT '规则名称快照',
    `template_code`  varchar(64)    DEFAULT NULL COMMENT '规则模板编码',
    `check_sql`      text COMMENT '实际执行的检测SQL',
    `actual_value`   decimal(20, 4) DEFAULT NULL COMMENT '检测到的实际值',
    `expected_value` decimal(20, 4) DEFAULT NULL COMMENT '期望值',
    `expected_type`  varchar(32)    DEFAULT NULL COMMENT '期望值类型：FIXED/DAILY_AVG等',
    `check_method`   varchar(64)    DEFAULT NULL COMMENT '校验方式',
    `operator`       varchar(8)     DEFAULT NULL COMMENT '校验操作符',
    `formula_result` decimal(20, 4) DEFAULT NULL COMMENT '公式计算结果（校验方式作用后）',
    `pass_flag`      tinyint(1)     DEFAULT NULL COMMENT '是否通过：1通过 0不通过',
    `create_time`    datetime       DEFAULT CURRENT_TIMESTAMP COMMENT '写入时间',
    PRIMARY KEY (`id`),
    KEY `idx_log_id` (`log_id`),
    KEY `idx_rule_id` (`rule_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 45
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='数据质量检测结果明细表';

--
-- Table structure for table `dq_execution_log`
--

DROP TABLE IF EXISTS `dq_execution_log`;
CREATE TABLE `dq_execution_log`
(
    `id`              bigint      NOT NULL AUTO_INCREMENT COMMENT '执行记录ID',
    `rule_id`         bigint      NOT NULL COMMENT '规则ID',
    `rule_name`       varchar(128) DEFAULT NULL COMMENT '规则名称快照',
    `spark_app_id`    varchar(128) DEFAULT NULL COMMENT 'Spark Application ID',
    `status`          varchar(32) NOT NULL COMMENT '执行状态: SUBMITTED/RUNNING/SUCCESS/FAILED/STOPPED',
    `start_time`      datetime     DEFAULT NULL COMMENT '开始时间',
    `end_time`        datetime     DEFAULT NULL COMMENT '结束时间',
    `result_value`    varchar(128) DEFAULT NULL COMMENT '检查结果值',
    `pass_flag`       tinyint(1)   DEFAULT NULL COMMENT '是否通过：1通过 0未通过',
    `datasource_id`   bigint       DEFAULT NULL COMMENT '数据源ID',
    `datasource_name` varchar(128) DEFAULT NULL COMMENT '数据源名称快照',
    `db_name`         varchar(128) DEFAULT NULL COMMENT '数据库名',
    `table_name`      varchar(128) DEFAULT NULL COMMENT '数据表名',
    `field_names`     varchar(512) DEFAULT NULL COMMENT '检测字段（逗号分隔）',
    `log_info`        text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '错误信息',
    `task_config`     longtext COMMENT '本次执行的 Spark DataQuality 配置 JSON 快照',
    `create_time`     datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `error_data_path` varchar(512) DEFAULT NULL COMMENT '错误数据行 HDFS 输出路径',
    PRIMARY KEY (`id`),
    KEY `idx_rule_id` (`rule_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 172
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='数据质量任务执行记录表';

--
-- Table structure for table `dq_rule`
--

DROP TABLE IF EXISTS `dq_rule`;
CREATE TABLE `dq_rule`
(
    `id`               bigint       NOT NULL AUTO_INCREMENT COMMENT '规则ID',
    `rule_name`        varchar(128) NOT NULL COMMENT '规则名称',
    `template_id`      bigint       NOT NULL DEFAULT '0' COMMENT '规则模板ID（关联 dq_rule_template.id）',
    `rule_config`      longtext     NOT NULL COMMENT '规则配置JSON（DataQualityConfiguration格式）',
    `spark_params`     text COMMENT 'Spark任务参数JSON',
    `datasource_id`    bigint                DEFAULT NULL COMMENT '数据源ID',
    `db_name`          varchar(256)          DEFAULT NULL COMMENT '数据库名称',
    `table_name`       varchar(256)          DEFAULT NULL COMMENT '数据表名称',
    `field_names`      varchar(1024)         DEFAULT NULL COMMENT '检测字段列表（逗号分隔）',
    `description`      text COMMENT '规则描述',
    `enabled`          tinyint(1)   NOT NULL DEFAULT '1' COMMENT '是否启用：1启用 0禁用',
    `creator_id`       bigint                DEFAULT NULL COMMENT '创建者ID',
    `create_time`      datetime              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater_id`       bigint                DEFAULT NULL COMMENT '更新者ID',
    `update_time`      datetime              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`          tinyint(1)   NOT NULL DEFAULT '0' COMMENT '删除标志：0正常 1已删除',
    `alert_group_code` varchar(64)           DEFAULT NULL COMMENT '告警组编码（关联 alert_group.group_code，为空则不告警）',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 12
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='数据质量规则定义表';

--
-- Table structure for table `dq_rule_template`
--

DROP TABLE IF EXISTS `dq_rule_template`;
CREATE TABLE `dq_rule_template`
(
    `id`                  bigint       NOT NULL AUTO_INCREMENT COMMENT '模板ID',
    `template_code`       varchar(64)  NOT NULL COMMENT '模板编码，如 NULL_CHECK',
    `template_name`       varchar(128) NOT NULL COMMENT '模板名称，如 空值检测',
    `dimension`           varchar(32)  NOT NULL DEFAULT 'completeness' COMMENT '质量维度：completeness/uniqueness/timeliness/validity/consistency/stability',
    `template_icon`       varchar(64)           DEFAULT NULL COMMENT '前端图标名称',
    `template_color`      varchar(32)           DEFAULT NULL COMMENT '前端图标颜色',
    `description`         varchar(512)          DEFAULT NULL COMMENT '模板描述',
    `check_sql_pattern`   text         NOT NULL COMMENT '聚合统计SQL模板，固定格式 SELECT COUNT(*) AS statistics_value FROM {templateCode}_items',
    `items_sql_pattern`   text COMMENT '明细行过滤SQL模板（SELECT * WHERE 条件），支持 {outputTable}/{field}/{field2}/{minValue}/{maxValue}/{enumValues}/{regexPattern}/{length}/{lengthOp}/{timeUnit}/{statMethod} 等占位符',
    `extra_config_schema` text COMMENT '模板专属额外配置字段的JSON Schema（用于前端动态渲染表单）',
    `sort_order`          int          NOT NULL DEFAULT '0' COMMENT '排序序号',
    `enabled`             tinyint(1)   NOT NULL DEFAULT '1' COMMENT '是否启用：1启用 0禁用',
    `creator_id`          bigint                DEFAULT NULL COMMENT '创建者ID',
    `create_time`         datetime              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater_id`          bigint                DEFAULT NULL COMMENT '更新者ID',
    `update_time`         datetime              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             tinyint(1)   NOT NULL DEFAULT '0' COMMENT '删除标志：0正常 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_template_code` (`template_code`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 13
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='数据质量规则模板表';

--
-- Dumping data for table `dq_rule_template`
--

INSERT INTO `dq_rule_template`
VALUES (1, 'NULL_CHECK', '字段空值校验', 'completeness', 'Warning', '#e6a23c', '检测字段中 NULL 值的行数',
        'SELECT COUNT(*) AS statistics_value FROM {templateCode}_items',
        'SELECT * FROM {outputTable} WHERE {field} IS NULL', NULL, 1, 1, NULL, '2026-03-21 16:20:57', NULL,
        '2026-03-21 16:20:57', 0),
       (2, 'EMPTY_STRING_CHECK', '字段空字符串校验', 'completeness', 'CircleClose', '#e6903c',
        '检测字段中空字符串（空值或仅含空白字符）的行数', 'SELECT COUNT(*) AS statistics_value FROM {templateCode}_items',
        'SELECT * FROM {outputTable} WHERE {field} IS NULL OR TRIM({field}) = \'\'', NULL, 2, 1, NULL,
        '2026-03-21 16:20:57', NULL, '2026-03-21 16:20:57', 0),
       (3, 'UNIQUENESS_CHECK', '字段唯一性校验', 'uniqueness', 'Key', '#67c23a',
        '检测字段中存在重复值的行数（group by 后 count > 1 的所有原始行）',
        'SELECT COUNT(*) AS statistics_value FROM {templateCode}_items',
        'SELECT * FROM (SELECT *, COUNT(*) OVER (PARTITION BY {field}) AS _cnt FROM {outputTable}) _tmp WHERE _cnt > 1',
        NULL, 3, 1, NULL, '2026-03-21 16:20:57', NULL, '2026-03-21 16:20:57', 0),
       (4, 'DISTINCT_COUNT_CHECK', '字段去重值个数校验', 'uniqueness', 'Filter', '#45a65c',
        '校验字段去重后的唯一值数量是否符合预期（distinct count）',
        'SELECT COUNT(DISTINCT {field}) AS statistics_value FROM {outputTable}', NULL, NULL, 4, 1, NULL,
        '2026-03-21 16:20:57', NULL, '2026-03-21 16:20:57', 0),
       (5, 'DUPLICATE_COUNT_CHECK', '字段重复值个数校验', 'uniqueness', 'CopyDocument', '#f56c6c',
        '检测字段中多余的重复数据行数（如1,2,2,2中的额外两个2）',
        'SELECT COUNT(*) AS statistics_value FROM {templateCode}_items',
        'SELECT * FROM (SELECT *, COUNT(*) OVER (PARTITION BY {field}) AS _cnt FROM {outputTable}) _tmp WHERE _cnt > 1',
        NULL, 5, 1, NULL, '2026-03-21 16:20:57', NULL, '2026-03-21 16:20:57', 0),
       (6, 'SINGLE_TABLE_TIME_CHECK', '单表时间字段比较', 'timeliness', 'Timer', '#409eff',
        '比较同一张表中两个时间字段的差值，检测超时行', 'SELECT COUNT(*) AS statistics_value FROM {templateCode}_items',
        'SELECT * FROM {outputTable} WHERE (UNIX_TIMESTAMP({field2}) - UNIX_TIMESTAMP({field})) / {timeUnit} > {threshold}',
        '{\"field2\":{\"type\":\"string\",\"label\":\"对比时间字段\",\"required\":true},\"timeUnit\":{\"type\":\"select\",\"label\":\"时间单位（秒数）\",\"options\":[\"1\",\"60\",\"3600\",\"86400\"],\"required\":true},\"threshold\":{\"type\":\"number\",\"label\":\"阈值（差值超过此值为异常）\",\"required\":true}}',
        6, 1, NULL, '2026-03-21 16:20:57', NULL, '2026-03-21 16:28:44', 0),
       (8, 'REGEX_CHECK', '字段格式校验', 'validity', 'EditPen', '#6c5ff5',
        '使用正则表达式校验字段格式（如身份证、手机号、邮箱等）',
        'SELECT COUNT(*) AS statistics_value FROM {templateCode}_items',
        'SELECT * FROM {outputTable} WHERE {field} NOT RLIKE \'{regexPattern}\'',
        '{\"regexPattern\":{\"type\":\"string\",\"label\":\"正则表达式\",\"required\":true}}', 8, 1, NULL,
        '2026-03-21 16:20:57', NULL, '2026-03-21 16:20:57', 0),
       (9, 'LENGTH_CHECK', '字段长度校验', 'validity', 'Rank', '#9b59b6', '校验字段字符串长度是否满足条件',
        'SELECT COUNT(*) AS statistics_value FROM {templateCode}_items',
        'SELECT * FROM {outputTable} WHERE LENGTH({field}) {lengthOp} {length}',
        '{\"lengthOp\":{\"type\":\"select\",\"label\":\"操作符\",\"options\":[\"<\",\">\",\"=\",\"!=\",\"<=\",\">=\"],\"required\":true},\"length\":{\"type\":\"number\",\"label\":\"长度阈值\",\"required\":true}}',
        9, 1, NULL, '2026-03-21 16:20:57', NULL, '2026-03-21 16:20:57', 0),
       (10, 'CONSISTENCY_CHECK', '单表字段值一致性比较', 'consistency', 'Switch', '#f39c12',
        '比较同一张表中两个字段的原值是否一致，检测不一致行',
        'SELECT COUNT(*) AS statistics_value FROM {templateCode}_items',
        'SELECT * FROM {outputTable} WHERE {field} != {field2} OR ({field} IS NULL AND {field2} IS NOT NULL) OR ({field} IS NOT NULL AND {field2} IS NULL)',
        '{\"field2\":{\"type\":\"string\",\"label\":\"对比字段B\",\"required\":true}}', 10, 1, NULL,
        '2026-03-21 16:20:57', NULL, '2026-03-21 16:20:57', 0),
       (11, 'STAT_CHECK', '字段统计值校验', 'stability', 'Odometer', '#1abc9c',
        '对字段的聚合统计值（AVG/MAX/MIN/SUM/COUNT）与固定阈值比较',
        'SELECT {statMethod}({field}) AS statistics_value FROM {outputTable}', NULL,
        '{\"statMethod\":{\"type\":\"select\",\"label\":\"统计方式\",\"options\":[\"AVG\",\"MAX\",\"MIN\",\"SUM\",\"COUNT\"],\"required\":true}}',
        11, 1, NULL, '2026-03-21 16:20:57', NULL, '2026-03-21 16:20:57', 0);

--
-- Table structure for table `dq_statistics_value`
--

DROP TABLE IF EXISTS `dq_statistics_value`;
CREATE TABLE `dq_statistics_value`
(
    `id`               bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `rule_id`          bigint NOT NULL COMMENT '规则ID',
    `template_code`    varchar(64)    DEFAULT NULL COMMENT '规则模板编码',
    `log_id`           bigint NOT NULL COMMENT '执行记录ID（关联 dq_execution_log.id）',
    `statistics_name`  varchar(256)   DEFAULT NULL COMMENT '统计指标名称，如 null_count.statistics_value',
    `statistics_value` decimal(20, 4) DEFAULT NULL COMMENT '统计值',
    `create_time`      datetime       DEFAULT CURRENT_TIMESTAMP COMMENT '写入时间',
    PRIMARY KEY (`id`),
    KEY `idx_rule_id` (`rule_id`),
    KEY `idx_log_id` (`log_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 45
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='数据质量统计值快照表';

--
-- Table structure for table `flink_job`
--

DROP TABLE IF EXISTS `flink_job`;
CREATE TABLE `flink_job`
(
    `job_id`           bigint       NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `job_name`         varchar(100) NOT NULL COMMENT '任务名称',
    `app_id`           varchar(100)          DEFAULT NULL COMMENT 'application id',
    `save_point`       varchar(100)          DEFAULT NULL COMMENT 'savepoint',
    `job_type`         varchar(100) NOT NULL COMMENT '任务类型 STREAMING_SQL, BATCH_SQL, JAR',
    `job_manager`      int          NOT NULL DEFAULT '1' COMMENT 'job_manager',
    `task_manager`     int          NOT NULL DEFAULT '1' COMMENT 'task_manager',
    `slot`             int          NOT NULL DEFAULT '1' COMMENT 'slot',
    `parallelism`      int          NOT NULL DEFAULT '1' COMMENT '并行度',
    `queue`            varchar(100)          DEFAULT NULL COMMENT '队列',
    `deploy_mode`      varchar(100) NOT NULL COMMENT '部署模式：YARN_PER, STANDALONE, LOCAL, YARN_APPLICATION',
    `flink_sql`        text COMMENT 'flink sql',
    `main_jar_path`    varchar(100)          DEFAULT NULL COMMENT '主jar包路径',
    `ext_jar_path`     varchar(100)          DEFAULT NULL COMMENT '第三方jar udf、 连接器等',
    `main_class_name`  varchar(100)          DEFAULT NULL COMMENT '主类名',
    `flink_run_config` varchar(200)          DEFAULT NULL COMMENT 'flink参数',
    `custom_args`      varchar(200)          DEFAULT NULL COMMENT '自定义参数',
    `env_id`           bigint                DEFAULT NULL COMMENT '环境id',
    `job_status`       varchar(100)          DEFAULT NULL COMMENT '任务状态：1 提交成功 ，2 运行中，3 成功，4 失败',
    `remark`           varchar(100)          DEFAULT NULL COMMENT '任务描述',
    `deleted`          tinyint      NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`       varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`      datetime     NOT NULL COMMENT '创建时间',
    `updater_id`       varchar(128)          DEFAULT NULL COMMENT '修改人',
    `update_time`      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`job_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='flink任务表';

--
-- Table structure for table `flink_job_instance`
--

DROP TABLE IF EXISTS `flink_job_instance`;
CREATE TABLE `flink_job_instance`
(
    `instance_id`    bigint       NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `job_id`         bigint       NOT NULL COMMENT '任务ID',
    `instance_name`  varchar(150) NOT NULL DEFAULT '' COMMENT '实例名称',
    `application_id` varchar(100)          DEFAULT NULL COMMENT 'flink任务ID',
    `deploy_mode`    varchar(100) NOT NULL COMMENT '部署模式：YARN_PER, STANDALONE, LOCAL, YARN_APPLICATION',
    `save_point`     varchar(200)          DEFAULT NULL COMMENT 'savepoint地址',
    `flink_job_id`   varchar(100)          DEFAULT NULL COMMENT 'flink任务ID',
    `job_script`     longtext COMMENT '任务脚本',
    `submit_time`    datetime              DEFAULT NULL COMMENT '任务提交时间',
    `finished_time`  datetime              DEFAULT NULL COMMENT '任务结束时间',
    `status`         varchar(100) NOT NULL DEFAULT '1' COMMENT '任务状态 RUNNING, KILL, FAILED',
    `deleted`        tinyint      NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`     varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`    datetime     NOT NULL COMMENT '创建时间',
    `updater_id`     varchar(128)          DEFAULT NULL COMMENT '修改人',
    `update_time`    timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`instance_id`),
    KEY `idx_submit_time_status` (`submit_time`, `status`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='flink任务实例表';

--
-- Table structure for table `lake_datasets`
--

DROP TABLE IF EXISTS `lake_datasets`;
CREATE TABLE `lake_datasets`
(
    `dataset_id`       bigint       NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `dataset_name`     varchar(128) NOT NULL COMMENT '图片库名称',
    `task_type`        varchar(32)           DEFAULT NULL,
    `description`      varchar(500)          DEFAULT NULL COMMENT '图片库描述',
    `storage_source`   varchar(32)  NOT NULL COMMENT '存储来源: LOCAL/HDFS/S3/MINIO/HTTP',
    `source_config`    text COMMENT '数据源配置 (JSON, 含路径/URL/凭证等)',
    `status`           varchar(32)  NOT NULL DEFAULT 'PROCESSING' COMMENT '状态: PROCESSING/WAITING_DOWNLOAD/DOWNLOADING/READY/ERROR',
    `image_count`      int                   DEFAULT '0' COMMENT '图片数量',
    `total_size_bytes` bigint                DEFAULT '0' COMMENT '总文件大小(字节)',
    `local_path`       varchar(512)          DEFAULT NULL COMMENT '本地存储路径',
    `error_message`    varchar(1024)         DEFAULT NULL COMMENT '错误信息',
    `deleted`          tinyint      NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`       varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`      datetime     NOT NULL COMMENT '创建时间',
    `updater_id`       varchar(128)          DEFAULT NULL COMMENT '修改人',
    `update_time`      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`dataset_id`),
    KEY `idx_dataset_name` (`dataset_name`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='图片库元信息表';

--
-- Table structure for table `lake_model_info`
--

DROP TABLE IF EXISTS `lake_model_info`;
CREATE TABLE `lake_model_info`
(
    `model_id`         bigint       NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `model_name`       varchar(128) NOT NULL COMMENT '模型名称',
    `description`      varchar(5000)         DEFAULT NULL,
    `task_id`          bigint                DEFAULT NULL COMMENT '关联训练任务ID',
    `dataset_id`       bigint       NOT NULL COMMENT '关联图片库ID',
    `model_arch`       varchar(64)  NOT NULL DEFAULT 'SimilarityAutoEncoder' COMMENT '模型架构',
    `model_path`       varchar(512) NOT NULL COMMENT '模型文件本地路径',
    `label_file_path`  varchar(512)          DEFAULT NULL,
    `model_size_bytes` bigint                DEFAULT '0' COMMENT '模型文件大小(字节)',
    `embedding_dim`    int                   DEFAULT '512' COMMENT 'Embedding维度',
    `training_epochs`  int                   DEFAULT NULL COMMENT '实际训练轮数',
    `final_loss`       decimal(10, 6)        DEFAULT NULL COMMENT '最终损失值',
    `status`           varchar(32)           DEFAULT 'TRAINING' COMMENT '模型状态: TRAINING/TRAINING_FAILED/TRAINING_COMPLETED',
    `vector_index_id`  bigint                DEFAULT NULL COMMENT '关联向量库ID',
    `deleted`          tinyint      NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`       varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`      datetime     NOT NULL COMMENT '创建时间',
    `updater_id`       varchar(128)          DEFAULT NULL COMMENT '修改人',
    `update_time`      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`model_id`),
    KEY `idx_model_name` (`model_name`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_dataset_id` (`dataset_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='模型元信息表';

--
-- Table structure for table `lake_tasks`
--

DROP TABLE IF EXISTS `lake_tasks`;
CREATE TABLE `lake_tasks`
(
    `task_id`           bigint       NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `task_name`         varchar(128) NOT NULL COMMENT '任务名称',
    `task_type`         varchar(64)  NOT NULL COMMENT '任务类型: IMAGE_SIMILARITY',
    `dataset_id`        bigint       NOT NULL COMMENT '关联图片库ID',
    `model_id`          bigint                DEFAULT NULL COMMENT '关联模型ID',
    `ml_task_id`        varchar(64)           DEFAULT NULL COMMENT 'Python ML 端的任务 UUID',
    `model_path`        varchar(512)          DEFAULT NULL COMMENT '该任务训练产出的模型文件路径',
    `status`            varchar(32)  NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING/TRAINING/COMPLETED/FAILED/CANCELLED',
    `hyper_params`      text COMMENT '超参数配置 (JSON, epochs/lr/batch_size等)',
    `training_progress` int                   DEFAULT '0' COMMENT '训练进度百分比',
    `loss_history`      text COMMENT '损失曲线数据 (JSON数组)',
    `error_message`     varchar(1024)         DEFAULT NULL COMMENT '错误信息',
    `started_at`        datetime              DEFAULT NULL COMMENT '开始时间',
    `completed_at`      datetime              DEFAULT NULL COMMENT '完成时间',
    `deleted`           tinyint      NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`        varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`       datetime     NOT NULL COMMENT '创建时间',
    `updater_id`        varchar(128)          DEFAULT NULL COMMENT '修改人',
    `update_time`       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`task_id`),
    KEY `idx_task_name` (`task_name`),
    KEY `idx_dataset_id` (`dataset_id`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 10
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='训练任务表';

--
-- Table structure for table `lake_vector_indexes`
--

DROP TABLE IF EXISTS `lake_vector_indexes`;
CREATE TABLE `lake_vector_indexes`
(
    `index_id`        bigint       NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `index_name`      varchar(128) NOT NULL COMMENT '向量库名称',
    `collection_name` varchar(128)          DEFAULT NULL COMMENT 'ChromaDB 集合名称',
    `dataset_id`      bigint       NOT NULL COMMENT '关联图片库ID',
    `model_id`        bigint       NOT NULL COMMENT '关联模型ID',
    `ml_task_id`      varchar(64)           DEFAULT NULL COMMENT 'Python ML 端的构建任务 UUID',
    `index_path`      varchar(512)          DEFAULT NULL COMMENT '向量索引本地路径',
    `total_vectors`   int                   DEFAULT '0' COMMENT '向量总数',
    `dimension`       int                   DEFAULT '512' COMMENT '向量维度',
    `distance_metric` varchar(32)           DEFAULT 'cosine' COMMENT '距离度量: cosine/euclidean',
    `build_status`    varchar(32)           DEFAULT 'PENDING' COMMENT '构建状态: PENDING/BUILDING/COMPLETED/FAILED',
    `error_message`   varchar(1024)         DEFAULT NULL COMMENT '错误信息',
    `deleted`         tinyint      NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`      varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`     datetime     NOT NULL COMMENT '创建时间',
    `updater_id`      varchar(128)          DEFAULT NULL COMMENT '修改人',
    `update_time`     timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`index_id`),
    KEY `idx_index_name` (`index_name`),
    KEY `idx_dataset_id` (`dataset_id`),
    KEY `idx_model_id` (`model_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 6
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='向量库索引表';

--
-- Table structure for table `lineage_edge`
--

DROP TABLE IF EXISTS `lineage_edge`;
CREATE TABLE `lineage_edge`
(
    `edge_id`        bigint      NOT NULL AUTO_INCREMENT COMMENT '边ID',
    `source_node_id` bigint      NOT NULL COMMENT '源节点ID（上游/数据流出方）',
    `target_node_id` bigint      NOT NULL COMMENT '目标节点ID（下游/数据流入方）',
    `dep_type`       varchar(20) NOT NULL DEFAULT 'DIRECT' COMMENT '依赖类型：DIRECT-直接依赖，TRANSFORM-转换依赖',
    `source_flag`    varchar(10) NOT NULL DEFAULT 'MANUAL' COMMENT '登记来源：AUTO-自动解析，MANUAL-手动登记',
    `remark`         varchar(255)         DEFAULT NULL COMMENT '备注',
    `creator_id`     varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater_id`     varchar(128)         DEFAULT NULL COMMENT '修改人',
    `update_time`    timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`edge_id`),
    UNIQUE KEY `uk_src_tgt` (`source_node_id`, `target_node_id`),
    KEY `idx_target` (`target_node_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 16
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='血缘连线表';

--
-- Table structure for table `lineage_node`
--

DROP TABLE IF EXISTS `lineage_node`;
CREATE TABLE `lineage_node`
(
    `node_id`       bigint       NOT NULL AUTO_INCREMENT COMMENT '节点ID',
    `table_id`      bigint       NOT NULL COMMENT '关联元数据表ID（meta_table.table_id）',
    `node_name`     varchar(128) NOT NULL COMMENT '节点名称（表名）',
    `node_type`     varchar(20)  NOT NULL DEFAULT 'TABLE' COMMENT '节点类型：TABLE-普通表，VIRTUAL_TABLE-虚拟表，DATASOURCE-数据源',
    `datasource_id` bigint                DEFAULT NULL COMMENT '所属数据源ID',
    `db_name`       varchar(128)          DEFAULT NULL COMMENT '所属库名称快照',
    `creator_id`    varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater_id`    varchar(128)          DEFAULT NULL COMMENT '修改人',
    `update_time`   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`node_id`),
    UNIQUE KEY `uk_table` (`table_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 21
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='血缘节点表';

--
-- Table structure for table `meta_column`
--

DROP TABLE IF EXISTS `meta_column`;
CREATE TABLE `meta_column`
(
    `column_id`         bigint      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `table_id`          bigint      NOT NULL COMMENT '表id',
    `column_name`       varchar(100)                                                 DEFAULT NULL COMMENT '字段名称',
    `data_type`         varchar(100)                                                 DEFAULT NULL COMMENT '数据类型',
    `column_type`       varchar(150)                                                 DEFAULT NULL COMMENT '字段类型',
    `numeric_precision` bigint unsigned                                              DEFAULT NULL,
    `numeric_scale`     bigint unsigned                                              DEFAULT NULL,
    `column_length`     bigint                                                       DEFAULT NULL COMMENT '字段长度',
    `comment`           varchar(500)                                                 DEFAULT NULL COMMENT '字段描述',
    `is_nullable`       varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '是否非空',
    `column_default`    varchar(60)                                                  DEFAULT NULL COMMENT '字段默认值',
    `deleted`           tinyint     NOT NULL                                         DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`        varchar(64) NOT NULL                                         DEFAULT '' COMMENT '创建人',
    `create_time`       datetime    NOT NULL                                         DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater_id`        varchar(128)                                                 DEFAULT NULL COMMENT '修改人',
    `update_time`       timestamp   NOT NULL                                         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`column_id`),
    KEY `unique_key_column` (`table_id`, `column_name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2461760
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='字段';

--
-- Table structure for table `meta_datasource`
--

DROP TABLE IF EXISTS `meta_datasource`;
CREATE TABLE `meta_datasource`
(
    `datasource_id`     bigint       NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `datasource_name`   varchar(50)  NOT NULL DEFAULT '' COMMENT '数据源名称',
    `type`              varchar(10)  NOT NULL DEFAULT '' COMMENT '数据源类型。(mysql,doris)',
    `source_type`       tinyint               DEFAULT NULL COMMENT '同步类型：1输入源，2输出源',
    `remark`            varchar(100) NOT NULL DEFAULT '' COMMENT '数据源描述',
    `connection_params` varchar(300)          DEFAULT NULL COMMENT '连接参数',
    `status`            tinyint      NOT NULL DEFAULT '1' COMMENT '数据源状态：启用 1，禁用 0',
    `deleted`           tinyint      NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`        varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`       datetime     NOT NULL COMMENT '创建时间',
    `updater_id`        varchar(128)          DEFAULT NULL COMMENT '修改人',
    `update_time`       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`datasource_id`),
    KEY `unique_key_datasourcename` (`datasource_name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='数据源';

--
-- Table structure for table `meta_datasource_plugin`
--

DROP TABLE IF EXISTS `meta_datasource_plugin`;
CREATE TABLE `meta_datasource_plugin`
(
    `id`                bigint      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`              varchar(50) NOT NULL COMMENT '插件名称',
    `type`              int         NOT NULL COMMENT '插件类型：1-关系型数据库，2-非关系型数据库，3-OLAP数据库',
    `driver_name`       varchar(100)         DEFAULT NULL COMMENT '驱动类名',
    `icon`              varchar(200)         DEFAULT NULL COMMENT '图标URL',
    `connection_params` text COMMENT '连接参数模板',
    `remark`            varchar(200)         DEFAULT NULL COMMENT '备注说明',
    `deleted`           tinyint     NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`        varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`       datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater_id`        varchar(128)         DEFAULT NULL COMMENT '修改人',
    `update_time`       timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`) COMMENT '插件名称唯一索引'
) ENGINE = InnoDB
  AUTO_INCREMENT = 10
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='数据源插件信息表';

--
-- Table structure for table `meta_datasource_type`
--

DROP TABLE IF EXISTS `meta_datasource_type`;
CREATE TABLE `meta_datasource_type`
(
    `type_id`      bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `type_name`    varchar(100) NOT NULL DEFAULT '' COMMENT '名称',
    `type_code`    varchar(64)  NOT NULL COMMENT '编码',
    `type_catalog` varchar(64)  NOT NULL COMMENT '类型分类：关系型数据库，非关系型数据库，图数据库，分析型数据库',
    `driver_name`  varchar(200) NOT NULL COMMENT '驱动名称',
    `icon`         varchar(200)          DEFAULT NULL COMMENT '图标',
    `jdbc_url`     varchar(200)          DEFAULT NULL COMMENT 'jdbc连接串',
    `remark`       varchar(500)          DEFAULT '' COMMENT '任务描述',
    `deleted`      tinyint      NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`   varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater_id`   varchar(128)          DEFAULT NULL COMMENT '修改人',
    `update_time`  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`type_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 11
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='数据源类型';

--
-- Dumping data for table `meta_datasource_type`
--

INSERT INTO `meta_datasource_type`
VALUES (1, 'MYSQL', 'MYSQL', 'SQL', 'com.mysql.cj.jdbc.Driver', 'MySQL.png', 'jdbc:mysql://%s:%s/%s', '', 0, '0',
        '2024-03-04 11:40:07', '1', '2024-03-16 11:34:26'),
       (2, 'ORACLE', 'ORACLE', 'SQL', 'oracle.jdbc.driver.OracleDriver', 'Oracle.png', 'jdbc:oracle:thin:@%s:%s:%s', '',
        0, '0', '2024-03-04 11:46:40', '1', '2024-03-17 08:42:27'),
       (3, 'SQLSERVER', 'SQLSERVER', 'SQL', 'com.microsoft.sqlserver.jdbc.SQLServerDriver', 'SQLServer.png',
        'jdbc:sqlserver://%s:%s;DatabaseName=%s', '', 0, '0', '2024-03-04 11:46:46', '1', '2024-03-16 11:40:29'),
       (9, 'DORIS', 'DORIS', 'OLAP', 'com.mysql.cj.jdbc.Driver', 'Doris.png', 'jdbc:mysql://%s:%s/%s', '', 0, '1',
        '2024-03-10 16:45:56', '1', '2024-03-16 11:34:26'),
       (10, 'CLICKHOUSE', 'ClickHouse', 'OLAP', 'ru.yandex.clickhouse.ClickHouseDriver', 'ClickHouse.png',
        'jdbc:clickhouse://%s:%s/%s', '', 0, '1', '2024-03-17 16:56:20', '1', '2024-03-17 12:11:42');

--
-- Table structure for table `meta_db`
--

DROP TABLE IF EXISTS `meta_db`;
CREATE TABLE `meta_db`
(
    `db_id`         bigint      NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `db_name`       varchar(80) NOT NULL COMMENT '数据库名称',
    `datasource_id` bigint      NOT NULL COMMENT '数据源ID',
    `comment`       varchar(500)         DEFAULT NULL COMMENT '备注',
    `deleted`       tinyint     NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`    varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater_id`    varchar(128)         DEFAULT NULL COMMENT '修改人',
    `update_time`   timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`db_id`),
    KEY `unique_key_db` (`datasource_id`, `db_name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 52
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='数据库';

--
-- Table structure for table `meta_table`
--

DROP TABLE IF EXISTS `meta_table`;
CREATE TABLE `meta_table`
(
    `table_id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `db_id`             bigint       NOT NULL COMMENT '数据库id',
    `table_name`        varchar(300) NOT NULL COMMENT '表名称',
    `comment`           varchar(500)          DEFAULT NULL COMMENT '表描述',
    `type`              varchar(64)           DEFAULT NULL COMMENT '表类型',
    `table_type`        varchar(20)  NOT NULL DEFAULT 'NORMAL' COMMENT '表类别：NORMAL-普通表，VIRTUAL_KAFKA-Kafka虚拟表(Topic)，VIRTUAL_HDFS-HDFS虚拟表(目录)',
    `engine`            varchar(64)           DEFAULT NULL COMMENT '引擎',
    `table_create_time` datetime              DEFAULT NULL COMMENT '表创建时间',
    `deleted`           tinyint      NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`        varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`       datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater_id`        varchar(128)          DEFAULT NULL COMMENT '修改人',
    `update_time`       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`table_id`),
    KEY `unique_key_tbl` (`db_id`, `table_name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1303
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='数据表';

--
-- Table structure for table `one_api_call_history`
--

DROP TABLE IF EXISTS `one_api_call_history`;
CREATE TABLE `one_api_call_history`
(
    `history_id`  bigint                                                 NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `call_date`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  NOT NULL COMMENT '调用日期',
    `call_ip`     varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '调用ip',
    `api_url`     varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin          DEFAULT NULL COMMENT 'api地址',
    `call_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin           DEFAULT NULL COMMENT '调用状态',
    `call_code`   bigint                                                 NOT NULL COMMENT '调用code',
    `error_info`  text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin         NOT NULL COMMENT '错误信息',
    `call_delay`  int                                                    NOT NULL DEFAULT '5000' COMMENT '调用延迟',
    `call_time`   timestamp                                              NULL     DEFAULT NULL COMMENT '调用时间',
    `deleted`     tinyint                                                NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time` datetime                                               NOT NULL COMMENT '创建时间',
    `updater_id`  varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '修改人',
    `update_time` timestamp                                              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`history_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 120
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='api调用历史表';

--
-- Table structure for table `one_api_info`
--

DROP TABLE IF EXISTS `one_api_info`;
CREATE TABLE `one_api_info`
(
    `api_id`        bigint                                                 NOT NULL AUTO_INCREMENT COMMENT '接口ID',
    `api_name`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  NOT NULL COMMENT 'api名称',
    `api_url`       varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '接口url',
    `group_id`      int                                                             DEFAULT NULL COMMENT 'api分组id',
    `api_type`      varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin           DEFAULT NULL COMMENT 'api类型',
    `datasource_id` bigint                                                 NOT NULL COMMENT '数据源id',
    `query_timeout` tinyint                                                NOT NULL DEFAULT '3' COMMENT '超时时间，默认3秒，最大10s',
    `req_method`    varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   NOT NULL COMMENT '请求方式',
    `limit_count`   int                                                    NOT NULL DEFAULT '5000' COMMENT '最大返回条数',
    `api_desc`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  NOT NULL COMMENT '接口描述',
    `api_config`    text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin         NOT NULL COMMENT 'api配置',
    `status`        tinyint(1)                                             NOT NULL DEFAULT '0' COMMENT '接口状态：0未发布状态，1发布状态',
    `api_response`  text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT 'api响应结果',
    `online_edit`   tinyint(1)                                             NOT NULL DEFAULT '0' COMMENT '是否为在线编辑，0：下线编辑，1：在线编辑',
    `deleted`       tinyint                                                NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`   datetime                                               NOT NULL COMMENT '创建时间',
    `updater_id`    varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '修改人',
    `update_time`   timestamp                                              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`api_id`) USING BTREE,
    KEY `api_url` (`api_url`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='api详情表';

--
-- Table structure for table `QRTZ_BLOB_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_BLOB_TRIGGERS`;
CREATE TABLE `QRTZ_BLOB_TRIGGERS`
(
    `SCHED_NAME`    varchar(120) NOT NULL,
    `TRIGGER_NAME`  varchar(190) NOT NULL,
    `TRIGGER_GROUP` varchar(190) NOT NULL,
    `BLOB_DATA`     blob,
    PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`),
    KEY `SCHED_NAME` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`),
    CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `QRTZ_CALENDARS`
--

DROP TABLE IF EXISTS `QRTZ_CALENDARS`;
CREATE TABLE `QRTZ_CALENDARS`
(
    `SCHED_NAME`    varchar(120) NOT NULL,
    `CALENDAR_NAME` varchar(190) NOT NULL,
    `CALENDAR`      blob         NOT NULL,
    PRIMARY KEY (`SCHED_NAME`, `CALENDAR_NAME`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `QRTZ_CRON_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_CRON_TRIGGERS`;
CREATE TABLE `QRTZ_CRON_TRIGGERS`
(
    `SCHED_NAME`      varchar(120) NOT NULL,
    `TRIGGER_NAME`    varchar(190) NOT NULL,
    `TRIGGER_GROUP`   varchar(190) NOT NULL,
    `CRON_EXPRESSION` varchar(120) NOT NULL,
    `TIME_ZONE_ID`    varchar(80) DEFAULT NULL,
    PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`),
    CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `QRTZ_FIRED_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_FIRED_TRIGGERS`;
CREATE TABLE `QRTZ_FIRED_TRIGGERS`
(
    `SCHED_NAME`        varchar(120) NOT NULL,
    `ENTRY_ID`          varchar(95)  NOT NULL,
    `TRIGGER_NAME`      varchar(190) NOT NULL,
    `TRIGGER_GROUP`     varchar(190) NOT NULL,
    `INSTANCE_NAME`     varchar(190) NOT NULL,
    `FIRED_TIME`        bigint       NOT NULL,
    `SCHED_TIME`        bigint       NOT NULL,
    `PRIORITY`          int          NOT NULL,
    `STATE`             varchar(16)  NOT NULL,
    `JOB_NAME`          varchar(190) DEFAULT NULL,
    `JOB_GROUP`         varchar(190) DEFAULT NULL,
    `IS_NONCONCURRENT`  varchar(1)   DEFAULT NULL,
    `REQUESTS_RECOVERY` varchar(1)   DEFAULT NULL,
    PRIMARY KEY (`SCHED_NAME`, `ENTRY_ID`),
    KEY `IDX_QRTZ_FT_TRIG_INST_NAME` (`SCHED_NAME`, `INSTANCE_NAME`),
    KEY `IDX_QRTZ_FT_INST_JOB_REQ_RCVRY` (`SCHED_NAME`, `INSTANCE_NAME`, `REQUESTS_RECOVERY`),
    KEY `IDX_QRTZ_FT_J_G` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`),
    KEY `IDX_QRTZ_FT_JG` (`SCHED_NAME`, `JOB_GROUP`),
    KEY `IDX_QRTZ_FT_T_G` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`),
    KEY `IDX_QRTZ_FT_TG` (`SCHED_NAME`, `TRIGGER_GROUP`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `QRTZ_JOB_DETAILS`
--

DROP TABLE IF EXISTS `QRTZ_JOB_DETAILS`;
CREATE TABLE `QRTZ_JOB_DETAILS`
(
    `SCHED_NAME`        varchar(120) NOT NULL,
    `JOB_NAME`          varchar(190) NOT NULL,
    `JOB_GROUP`         varchar(190) NOT NULL,
    `DESCRIPTION`       varchar(250) DEFAULT NULL,
    `JOB_CLASS_NAME`    varchar(250) NOT NULL,
    `IS_DURABLE`        varchar(1)   NOT NULL,
    `IS_NONCONCURRENT`  varchar(1)   NOT NULL,
    `IS_UPDATE_DATA`    varchar(1)   NOT NULL,
    `REQUESTS_RECOVERY` varchar(1)   NOT NULL,
    `JOB_DATA`          blob,
    PRIMARY KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`),
    KEY `IDX_QRTZ_J_REQ_RECOVERY` (`SCHED_NAME`, `REQUESTS_RECOVERY`),
    KEY `IDX_QRTZ_J_GRP` (`SCHED_NAME`, `JOB_GROUP`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `QRTZ_LOCKS`
--

DROP TABLE IF EXISTS `QRTZ_LOCKS`;
CREATE TABLE `QRTZ_LOCKS`
(
    `SCHED_NAME` varchar(120) NOT NULL,
    `LOCK_NAME`  varchar(40)  NOT NULL,
    PRIMARY KEY (`SCHED_NAME`, `LOCK_NAME`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `QRTZ_PAUSED_TRIGGER_GRPS`
--

DROP TABLE IF EXISTS `QRTZ_PAUSED_TRIGGER_GRPS`;
CREATE TABLE `QRTZ_PAUSED_TRIGGER_GRPS`
(
    `SCHED_NAME`    varchar(120) NOT NULL,
    `TRIGGER_GROUP` varchar(190) NOT NULL,
    PRIMARY KEY (`SCHED_NAME`, `TRIGGER_GROUP`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `QRTZ_SCHEDULER_STATE`
--

DROP TABLE IF EXISTS `QRTZ_SCHEDULER_STATE`;
CREATE TABLE `QRTZ_SCHEDULER_STATE`
(
    `SCHED_NAME`        varchar(120) NOT NULL,
    `INSTANCE_NAME`     varchar(190) NOT NULL,
    `LAST_CHECKIN_TIME` bigint       NOT NULL,
    `CHECKIN_INTERVAL`  bigint       NOT NULL,
    PRIMARY KEY (`SCHED_NAME`, `INSTANCE_NAME`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `QRTZ_SIMPLE_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_SIMPLE_TRIGGERS`;
CREATE TABLE `QRTZ_SIMPLE_TRIGGERS`
(
    `SCHED_NAME`      varchar(120) NOT NULL,
    `TRIGGER_NAME`    varchar(190) NOT NULL,
    `TRIGGER_GROUP`   varchar(190) NOT NULL,
    `REPEAT_COUNT`    bigint       NOT NULL,
    `REPEAT_INTERVAL` bigint       NOT NULL,
    `TIMES_TRIGGERED` bigint       NOT NULL,
    PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`),
    CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `QRTZ_SIMPROP_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_SIMPROP_TRIGGERS`;
CREATE TABLE `QRTZ_SIMPROP_TRIGGERS`
(
    `SCHED_NAME`    varchar(120) NOT NULL,
    `TRIGGER_NAME`  varchar(190) NOT NULL,
    `TRIGGER_GROUP` varchar(190) NOT NULL,
    `STR_PROP_1`    varchar(512)   DEFAULT NULL,
    `STR_PROP_2`    varchar(512)   DEFAULT NULL,
    `STR_PROP_3`    varchar(512)   DEFAULT NULL,
    `INT_PROP_1`    int            DEFAULT NULL,
    `INT_PROP_2`    int            DEFAULT NULL,
    `LONG_PROP_1`   bigint         DEFAULT NULL,
    `LONG_PROP_2`   bigint         DEFAULT NULL,
    `DEC_PROP_1`    decimal(13, 4) DEFAULT NULL,
    `DEC_PROP_2`    decimal(13, 4) DEFAULT NULL,
    `BOOL_PROP_1`   varchar(1)     DEFAULT NULL,
    `BOOL_PROP_2`   varchar(1)     DEFAULT NULL,
    PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`),
    CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `QRTZ_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_TRIGGERS`;
CREATE TABLE `QRTZ_TRIGGERS`
(
    `SCHED_NAME`     varchar(120) NOT NULL,
    `TRIGGER_NAME`   varchar(190) NOT NULL,
    `TRIGGER_GROUP`  varchar(190) NOT NULL,
    `JOB_NAME`       varchar(190) NOT NULL,
    `JOB_GROUP`      varchar(190) NOT NULL,
    `DESCRIPTION`    varchar(250) DEFAULT NULL,
    `NEXT_FIRE_TIME` bigint       DEFAULT NULL,
    `PREV_FIRE_TIME` bigint       DEFAULT NULL,
    `PRIORITY`       int          DEFAULT NULL,
    `TRIGGER_STATE`  varchar(16)  NOT NULL,
    `TRIGGER_TYPE`   varchar(8)   NOT NULL,
    `START_TIME`     bigint       NOT NULL,
    `END_TIME`       bigint       DEFAULT NULL,
    `CALENDAR_NAME`  varchar(190) DEFAULT NULL,
    `MISFIRE_INSTR`  smallint     DEFAULT NULL,
    `JOB_DATA`       blob,
    PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`),
    KEY `IDX_QRTZ_T_J` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`),
    KEY `IDX_QRTZ_T_JG` (`SCHED_NAME`, `JOB_GROUP`),
    KEY `IDX_QRTZ_T_C` (`SCHED_NAME`, `CALENDAR_NAME`),
    KEY `IDX_QRTZ_T_G` (`SCHED_NAME`, `TRIGGER_GROUP`),
    KEY `IDX_QRTZ_T_STATE` (`SCHED_NAME`, `TRIGGER_STATE`),
    KEY `IDX_QRTZ_T_N_STATE` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`, `TRIGGER_STATE`),
    KEY `IDX_QRTZ_T_N_G_STATE` (`SCHED_NAME`, `TRIGGER_GROUP`, `TRIGGER_STATE`),
    KEY `IDX_QRTZ_T_NEXT_FIRE_TIME` (`SCHED_NAME`, `NEXT_FIRE_TIME`),
    KEY `IDX_QRTZ_T_NFT_ST` (`SCHED_NAME`, `TRIGGER_STATE`, `NEXT_FIRE_TIME`),
    KEY `IDX_QRTZ_T_NFT_MISFIRE` (`SCHED_NAME`, `MISFIRE_INSTR`, `NEXT_FIRE_TIME`),
    KEY `IDX_QRTZ_T_NFT_ST_MISFIRE` (`SCHED_NAME`, `MISFIRE_INSTR`, `NEXT_FIRE_TIME`, `TRIGGER_STATE`),
    KEY `IDX_QRTZ_T_NFT_ST_MISFIRE_GRP` (`SCHED_NAME`, `MISFIRE_INSTR`, `NEXT_FIRE_TIME`, `TRIGGER_GROUP`,
                                         `TRIGGER_STATE`),
    CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `QRTZ_JOB_DETAILS` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `spark_job`
--

DROP TABLE IF EXISTS `spark_job`;
CREATE TABLE `spark_job`
(
    `job_id`           bigint       NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `job_name`         varchar(100) NOT NULL COMMENT '任务名称',
    `application_id`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'application id',
    `job_type`         varchar(100)                                                  DEFAULT NULL COMMENT '任务类型：JAR, BATCH_SQL',
    `master`           varchar(100)                                                  DEFAULT NULL COMMENT 'master',
    `deploy_mode`      varchar(100) NOT NULL COMMENT '部署模式：local, yarn-client, yarn-cluster, k8s-client, k8s-cluster',
    `parallelism`      int          NOT NULL                                         DEFAULT '1' COMMENT '并行度',
    `driver_cores`     int          NOT NULL                                         DEFAULT '1' COMMENT 'driver核心数',
    `driver_memory`    int          NOT NULL                                         DEFAULT '1' COMMENT 'driver内存',
    `num_executors`    int          NOT NULL                                         DEFAULT '1' COMMENT 'executors个数',
    `executor_cores`   int          NOT NULL                                         DEFAULT '1' COMMENT '每个executor的核心数',
    `executor_memory`  int          NOT NULL                                         DEFAULT '1' COMMENT '每个executor的内存',
    `other_spark_conf` longtext COMMENT '其他配置',
    `main_jar_path`    int                                                           DEFAULT NULL COMMENT '主jar包路径',
    `main_class_name`  varchar(100)                                                  DEFAULT NULL COMMENT '主类名',
    `main_args`        varchar(200)                                                  DEFAULT NULL COMMENT '主类参数',
    `sql_content`      longtext COMMENT 'spark sql',
    `queue`            varchar(100)                                                  DEFAULT NULL COMMENT '队列',
    `namespace`        varchar(100)                                                  DEFAULT NULL COMMENT 'k8s命名空间',
    `env_id`           bigint                                                        DEFAULT NULL COMMENT '环境id',
    `job_status`       varchar(100)                                                  DEFAULT NULL COMMENT '任务状态：1 提交成功 ，2 运行中，3 成功，4 失败',
    `schedule_status`  int                                                           DEFAULT NULL COMMENT '调度状态：1 启用 0 停用',
    `cron_expression`  varchar(100)                                                  DEFAULT NULL COMMENT 'cron表达式',
    `start_time`       datetime                                                      DEFAULT NULL COMMENT '任务开始时间',
    `end_time`         datetime                                                      DEFAULT NULL COMMENT '任务结束时间',
    `remark`           varchar(100)                                                  DEFAULT NULL COMMENT '任务描述',
    `deleted`          tinyint      NOT NULL                                         DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`       varchar(64)  NOT NULL                                         DEFAULT '' COMMENT '创建人',
    `create_time`      datetime     NOT NULL COMMENT '创建时间',
    `updater_id`       varchar(128)                                                  DEFAULT NULL COMMENT '修改人',
    `update_time`      timestamp    NOT NULL                                         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`job_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='spark任务表';

--
-- Table structure for table `spark_job_instance`
--

DROP TABLE IF EXISTS `spark_job_instance`;
CREATE TABLE `spark_job_instance`
(
    `instance_id`    bigint       NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `job_id`         bigint       NOT NULL COMMENT '任务id',
    `instance_name`  varchar(100) NOT NULL COMMENT '任务实例名称',
    `deploy_mode`    varchar(100) NOT NULL COMMENT '部署模式：local, yarn-client, yarn-cluster, k8s-client, k8s-cluster',
    `application_id` varchar(100)          DEFAULT NULL COMMENT 'application id',
    `job_type`       varchar(100)          DEFAULT NULL COMMENT '任务类型：JAR, BATCH_SQL',
    `job_script`     varchar(100)          DEFAULT NULL COMMENT '任务执行脚本',
    `sql_content`    longtext COMMENT 'spark sql',
    `env_id`         bigint                DEFAULT NULL COMMENT '环境id',
    `job_status`     varchar(100)          DEFAULT NULL COMMENT '任务状态：1 提交成功 ，2 运行中，3 成功，4 失败',
    `submit_time`    datetime     NOT NULL COMMENT '提交时间',
    `finished_time`  datetime              DEFAULT NULL COMMENT '完成时间',
    `error_info`     varchar(5000)         DEFAULT NULL COMMENT '错误信息',
    `deleted`        tinyint      NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`     varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`    datetime     NOT NULL COMMENT '创建时间',
    `updater_id`     varchar(128)          DEFAULT NULL COMMENT '修改人',
    `update_time`    timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`instance_id`),
    KEY `idx_submit_time_status` (`submit_time`, `job_status`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='spark任务实例表';

--
-- Table structure for table `st_job`
--

DROP TABLE IF EXISTS `st_job`;
CREATE TABLE `st_job`
(
    `job_id`         bigint                                                NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    `job_name`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '任务名称',
    `job_type`       int                                                            DEFAULT '1' COMMENT '任务类型',
    `job_script`     text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '专家模式任务配置',
    `env_id`         bigint                                                         DEFAULT NULL COMMENT '环境ID',
    `engine_name`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '引擎名称',
    `engine_version` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin         DEFAULT NULL COMMENT '引擎版本',
    `engine_param`   varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin        DEFAULT NULL COMMENT '引擎参数',
    `status`         tinyint                                               NOT NULL DEFAULT '0' COMMENT '任务状态',
    `description`    varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin         DEFAULT NULL COMMENT '任务描述',
    `deleted`        tinyint                                               NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`    datetime                                              NOT NULL COMMENT '创建时间',
    `updater_id`     varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin         DEFAULT NULL COMMENT '修改人',
    `update_time`    timestamp                                             NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`job_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 13
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='数据集成任务表';

--
-- Table structure for table `st_job_instance`
--

DROP TABLE IF EXISTS `st_job_instance`;
CREATE TABLE `st_job_instance`
(
    `instance_id`   bigint                                                NOT NULL AUTO_INCREMENT COMMENT '任务实例ID',
    `instance_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '任务实例名称',
    `job_id`        bigint                                                NOT NULL COMMENT '任务ID',
    `job_config`    text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '任务配置',
    `status`        tinyint(1)                                            NOT NULL DEFAULT '0' COMMENT '任务状态',
    `log_info`      longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
    `start_time`    datetime                                                       DEFAULT NULL COMMENT '开始时间',
    `end_time`      datetime                                                       DEFAULT NULL COMMENT '结束时间',
    `deleted`       tinyint                                               NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`   datetime                                              NOT NULL COMMENT '创建时间',
    `updater_id`    varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin         DEFAULT NULL COMMENT '修改人',
    `update_time`   timestamp                                             NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`instance_id`) USING BTREE,
    KEY `idx_start_time_status` (`start_time`, `status`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 19
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='数据集成任务实例表';

--
-- Table structure for table `st_task`
--

DROP TABLE IF EXISTS `st_task`;
CREATE TABLE `st_task`
(
    `task_id`           varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '子任务ID',
    `task_name`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  NOT NULL COMMENT '子任务名称',
    `job_id`            bigint                                                 NOT NULL COMMENT '任务ID',
    `connector_type`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  NOT NULL COMMENT '连接器类型',
    `connector_name`    varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '连接器名称',
    `connection_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '子任务配置',
    `position`          varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin         DEFAULT NULL COMMENT '节点位置',
    `deleted`           tinyint                                                NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`       datetime                                               NOT NULL COMMENT '创建时间',
    `updater_id`        varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '修改人',
    `update_time`       timestamp                                              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`task_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='数据集成子任务表';

--
-- Table structure for table `st_task_relation`
--

DROP TABLE IF EXISTS `st_task_relation`;
CREATE TABLE `st_task_relation`
(
    `relation_id`    bigint                                                 NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `job_id`         bigint                                                 NOT NULL COMMENT '任务ID',
    `source_task_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '输入任务节点ID',
    `sink_task_id`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '输出任务节点ID',
    `deleted`        tinyint                                                NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`    datetime                                               NOT NULL COMMENT '创建时间',
    `updater_id`     varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '修改人',
    `update_time`    timestamp                                              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`relation_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 789
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='数据集成子任务关系表';

--
-- Table structure for table `sys_config`
--

DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`
(
    `config_id`       int           NOT NULL AUTO_INCREMENT COMMENT '参数主键',
    `config_name`     varchar(128)  NOT NULL DEFAULT '' COMMENT '配置名称',
    `config_key`      varchar(128)  NOT NULL DEFAULT '' COMMENT '配置键名',
    `config_options`  varchar(1024) NOT NULL DEFAULT '' COMMENT '可选的选项',
    `config_value`    varchar(256)  NOT NULL DEFAULT '' COMMENT '配置值',
    `is_allow_change` tinyint(1)    NOT NULL COMMENT '是否允许修改',
    `creator_id`      bigint                 DEFAULT NULL COMMENT '创建者ID',
    `updater_id`      bigint                 DEFAULT NULL COMMENT '更新者ID',
    `update_time`     datetime               DEFAULT NULL COMMENT '更新时间',
    `create_time`     datetime               DEFAULT NULL COMMENT '创建时间',
    `remark`          varchar(128)           DEFAULT NULL COMMENT '备注',
    `deleted`         tinyint(1)    NOT NULL DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`config_id`),
    UNIQUE KEY `config_key_uniq_idx` (`config_key`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 6
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='参数配置表';

--
-- Dumping data for table `sys_config`
--

INSERT INTO `sys_config`
VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName',
        '[\"skin-blue\",\"skin-green\",\"skin-purple\",\"skin-red\",\"skin-yellow\"]', 'skin-blue', 1, NULL, 1,
        '2023-02-17 16:46:50', '2022-05-21 08:30:55',
        '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow', 0),
       (2, '用户管理-账号初始密码', 'sys.user.initPassword', '', '1234567', 1, NULL, NULL, '2022-08-28 21:54:19',
        '2022-05-21 08:30:55', '初始化密码 123456', 0),
       (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', '[\"theme-dark\",\"theme-light\"]', 'theme-dark', 1, NULL,
        NULL, '2022-08-28 22:12:15', '2022-08-20 08:30:55', '深色主题theme-dark，浅色主题theme-light', 0),
       (4, '账号自助-验证码开关', 'sys.account.captchaOnOff', '[\"true\",\"false\"]', 'false', 0, NULL, 1,
        '2023-05-06 14:20:47', '2022-05-21 08:30:55', '是否开启验证码功能（true开启，false关闭）', 0),
       (5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', '[\"true\",\"false\"]', 'true', 0, NULL, 1,
        '2022-10-05 22:18:57', '2022-05-21 08:30:55', '是否开启注册用户功能（true开启，false关闭）', 0);

--
-- Table structure for table `sys_dept`
--

DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`
(
    `dept_id`     bigint      NOT NULL AUTO_INCREMENT COMMENT '部门id',
    `parent_id`   bigint      NOT NULL DEFAULT '0' COMMENT '父部门id',
    `ancestors`   text        NOT NULL COMMENT '祖级列表',
    `dept_name`   varchar(64) NOT NULL DEFAULT '' COMMENT '部门名称',
    `order_num`   int         NOT NULL DEFAULT '0' COMMENT '显示顺序',
    `leader_id`   bigint               DEFAULT NULL,
    `leader_name` varchar(64)          DEFAULT NULL COMMENT '负责人',
    `phone`       varchar(16)          DEFAULT NULL COMMENT '联系电话',
    `email`       varchar(128)         DEFAULT NULL COMMENT '邮箱',
    `status`      smallint    NOT NULL DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
    `creator_id`  bigint               DEFAULT NULL COMMENT '创建者ID',
    `create_time` datetime             DEFAULT NULL COMMENT '创建时间',
    `updater_id`  bigint               DEFAULT NULL COMMENT '更新者ID',
    `update_time` datetime             DEFAULT NULL COMMENT '更新时间',
    `deleted`     tinyint(1)  NOT NULL DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`dept_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 205
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='部门表';

--
-- Dumping data for table `sys_dept`
--

INSERT INTO `sys_dept`
VALUES (1, 0, '0', 'lacus', 0, NULL, 'casey', '15800001223', 'casey@163.com', 1, NULL, '2022-05-21 08:30:54', 1,
        '2023-05-06 14:02:18', 0),
       (2, 1, '0,1', '西安分公司', 1, NULL, 'casey', '15888888888', 'casey@163.com', 1, NULL, '2022-05-21 08:30:54', 1,
        '2023-05-06 14:02:43', 0),
       (3, 1, '0,1', '长沙分公司', 2, NULL, 'valarchie', '15888888888', 'valarchie@163.com', 1, NULL,
        '2022-05-21 08:30:54', 1, '2023-03-15 15:14:33', 1),
       (4, 2, '0,1,2', '研发部门', 1, NULL, 'valarchie', '15888888888', 'valarchie@163.com', 1, NULL,
        '2022-05-21 08:30:54', NULL, NULL, 0),
       (5, 2, '0,1,2', '市场部门', 2, NULL, 'valarchie', '15888888888', 'valarchie@163.com', 1, NULL,
        '2022-05-21 08:30:54', 1, '2023-05-06 14:04:19', 1),
       (6, 2, '0,1,2', '测试部门', 3, NULL, 'valarchie', '15888888888', 'valarchie@163.com', 1, NULL,
        '2022-05-21 08:30:54', 1, '2023-05-06 14:04:13', 1),
       (7, 2, '0,1,2', '财务部门', 4, NULL, 'valarchie', '15888888888', 'valarchie@163.com', 1, NULL,
        '2022-05-21 08:30:54', 1, '2023-05-06 14:04:16', 1),
       (8, 2, '0,1,2', '运维部门', 5, NULL, 'valarchie', '15888888888', 'valarchie@163.com', 1, NULL,
        '2022-05-21 08:30:54', NULL, NULL, 0),
       (9, 3, '0,1,3', '市场部门', 1, NULL, 'valarchie', '15888888888', 'valarchie@163.com', 1, NULL,
        '2022-05-21 08:30:54', 1, '2023-03-15 15:14:31', 1),
       (10, 3, '0,1,3', '财务部门', 2, NULL, 'valarchie', '15888888888', 'valarchie@163.com', 0, NULL,
        '2022-05-21 08:30:54', 1, '2023-03-15 15:14:29', 1),
       (203, 1, '0,1', '北京分公司', 0, NULL, NULL, NULL, NULL, 1, 1, '2023-05-06 14:03:09', 1, '2023-05-06 14:03:14',
        0),
       (204, 203, '0,1,203', '测试部门', 0, NULL, NULL, NULL, NULL, 0, 1, '2023-05-06 14:03:40', NULL, NULL, 0);

--
-- Table structure for table `sys_env`
--

DROP TABLE IF EXISTS `sys_env`;
CREATE TABLE `sys_env`
(
    `env_id`      bigint       NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `name`        varchar(100) NOT NULL COMMENT '环境名称',
    `config`      longtext     NOT NULL COMMENT '环境配置',
    `remark`      varchar(500)          DEFAULT NULL COMMENT '环境描述',
    `deleted`     tinyint      NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`  varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater_id`  varchar(128)          DEFAULT NULL COMMENT '修改人',
    `update_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`env_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='环境管理';

--
-- Table structure for table `sys_job`
--

DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job`
(
    `job_id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    `job_name`        varchar(64)  NOT NULL COMMENT '任务名称',
    `job_group`       varchar(64)  NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
    `invoke_target`   varchar(500) NOT NULL COMMENT '调用目标字符串',
    `cron_expression` varchar(128)          DEFAULT '' COMMENT 'cron执行表达式',
    `misfire_policy`  varchar(20)           DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
    `concurrent`      char(1)               DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
    `status`          varchar(10)           DEFAULT NULL,
    `deleted`         tinyint      NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`      varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater_id`      varchar(128)          DEFAULT NULL COMMENT '修改人',
    `update_time`     timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`          varchar(500)          DEFAULT '' COMMENT '备注信息',
    PRIMARY KEY (`job_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 10
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='定时任务调度表';


--
-- Table structure for table `sys_job_log`
--

DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log`
(
    `job_log_id`     bigint       NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
    `job_name`       varchar(64)  NOT NULL COMMENT '任务名称',
    `job_group`      varchar(64)  NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
    `invoke_target`  varchar(500) NOT NULL COMMENT '调用目标字符串',
    `job_message`    varchar(500)          DEFAULT NULL COMMENT '日志信息',
    `status`         char(1)               DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
    `exception_info` varchar(2000)         DEFAULT '' COMMENT '异常信息',
    `deleted`        tinyint      NOT NULL DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`     varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater_id`     varchar(128)          DEFAULT NULL COMMENT '修改人',
    `update_time`    timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`job_log_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 332
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='定时任务调度日志表';

--
-- Table structure for table `sys_login_info`
--

DROP TABLE IF EXISTS `sys_login_info`;
CREATE TABLE `sys_login_info`
(
    `info_id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '访问ID',
    `username`         varchar(50)  NOT NULL DEFAULT '' COMMENT '用户账号',
    `ip_address`       varchar(128) NOT NULL DEFAULT '' COMMENT '登录IP地址',
    `login_location`   varchar(255) NOT NULL DEFAULT '' COMMENT '登录地点',
    `browser`          varchar(50)  NOT NULL DEFAULT '' COMMENT '浏览器类型',
    `operation_system` varchar(50)  NOT NULL DEFAULT '' COMMENT '操作系统',
    `status`           smallint     NOT NULL DEFAULT '0' COMMENT '登录状态（1成功 0失败）',
    `msg`              varchar(255) NOT NULL DEFAULT '' COMMENT '提示消息',
    `login_time`       datetime              DEFAULT NULL COMMENT '访问时间',
    `deleted`          tinyint(1)   NOT NULL DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`info_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 353
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='系统访问记录';

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`
(
    `menu_id`     bigint      NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `menu_name`   varchar(64) NOT NULL COMMENT '菜单名称',
    `parent_id`   bigint      NOT NULL DEFAULT '0' COMMENT '父菜单ID',
    `order_num`   int         NOT NULL DEFAULT '0' COMMENT '显示顺序',
    `path`        varchar(255)         DEFAULT '' COMMENT '路由地址',
    `component`   varchar(255)         DEFAULT NULL COMMENT '组件路径',
    `query`       varchar(255)         DEFAULT NULL COMMENT '路由参数',
    `is_external` tinyint(1)  NOT NULL DEFAULT '1' COMMENT '是否为外链（1是 0否）',
    `is_cache`    tinyint(1)  NOT NULL DEFAULT '0' COMMENT '是否缓存（1缓存 0不缓存）',
    `menu_type`   smallint    NOT NULL DEFAULT '0' COMMENT '菜单类型（M=1目录 C=2菜单 F=3按钮）',
    `is_visible`  tinyint(1)  NOT NULL DEFAULT '0' COMMENT '菜单状态（1显示 0隐藏）',
    `status`      smallint    NOT NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
    `perms`       varchar(128)         DEFAULT NULL COMMENT '权限标识',
    `icon`        varchar(128)         DEFAULT '#' COMMENT '菜单图标',
    `creator_id`  bigint               DEFAULT NULL COMMENT '创建者ID',
    `create_time` datetime             DEFAULT NULL COMMENT '创建时间',
    `updater_id`  bigint               DEFAULT NULL COMMENT '更新者ID',
    `update_time` datetime             DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(512)         DEFAULT '' COMMENT '备注',
    `deleted`     tinyint(1)  NOT NULL DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`menu_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 3016
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='菜单权限表';

--
-- Dumping data for table `sys_menu`
--

INSERT INTO `sys_menu`
VALUES (1, '基础建设', 0, 7, 'system', NULL, '', 0, 1, 1, 1, 1, '', 'system', 0, '2022-05-21 08:30:54', 1,
        '2026-03-28 11:41:23', '系统管理目录', 0),
       (2, '系统监控', 0, 9, 'monitor', NULL, '', 0, 1, 1, 1, 1, '', 'monitor', 0, '2022-05-21 08:30:54', 1,
        '2025-03-23 16:06:11', '系统监控目录', 0),
       (5, '用户管理', 1, 1, 'user', 'system/user/index', '', 0, 1, 2, 1, 1, 'system:user:list', 'user', 0,
        '2022-05-21 08:30:54', NULL, NULL, '用户管理菜单', 0),
       (6, '角色管理', 1, 2, 'role', 'system/role/index', '', 0, 1, 2, 1, 1, 'system:role:list', 'email', 0,
        '2022-05-21 08:30:54', 1, '2023-09-04 10:58:41', '角色管理菜单', 0),
       (7, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', 0, 1, 2, 1, 1, 'system:menu:list', 'tree-table', 0,
        '2022-05-21 08:30:54', NULL, NULL, '菜单管理菜单', 0),
       (8, '部门管理', 1, 4, 'dept', 'system/dept/index', '', 0, 1, 2, 1, 1, 'system:dept:list', 'tree', 0,
        '2022-05-21 08:30:54', NULL, NULL, '部门管理菜单', 0),
       (9, '岗位管理', 1, 5, 'post', 'system/post/index', '', 0, 1, 2, 1, 1, 'system:post:list', 'post', 0,
        '2022-05-21 08:30:54', NULL, NULL, '岗位管理菜单', 0),
       (12, '日志管理', 2, 9, 'log', '', '', 0, 1, 1, 1, 1, '', 'log', 0, '2022-05-21 08:30:54', 1,
        '2024-05-02 17:52:06', '日志管理菜单', 0),
       (13, '在线用户', 2, 1, 'online', 'monitor/online/index', '', 0, 1, 2, 1, 1, 'monitor:online:list', 'online', 0,
        '2022-05-21 08:30:54', NULL, NULL, '在线用户菜单', 0),
       (14, '数据监控', 2, 3, 'druid', 'monitor/druid/index', '', 0, 1, 2, 1, 1, 'monitor:druid:list', 'druid', 0,
        '2022-05-21 08:30:54', NULL, NULL, '数据监控菜单', 0),
       (15, '服务监控', 2, 4, 'server', 'monitor/server/index', '', 0, 1, 2, 1, 1, 'monitor:server:list', 'server', 0,
        '2022-05-21 08:30:54', NULL, NULL, '服务监控菜单', 0),
       (16, '缓存监控', 2, 5, 'cache', 'monitor/cache/index', '', 0, 1, 2, 1, 1, 'monitor:cache:list', 'redis', 0,
        '2022-05-21 08:30:54', NULL, NULL, '缓存监控菜单', 0),
       (18, '操作日志', 12, 1, 'operlog', 'monitor/operlog/index', '', 0, 1, 2, 1, 1, 'monitor:operlog:list', 'form', 0,
        '2022-05-21 08:30:54', NULL, NULL, '操作日志菜单', 0),
       (19, '登录日志', 12, 2, 'logininfor', 'monitor/logininfor/index', '', 0, 1, 2, 1, 1, 'monitor:logininfor:list',
        'logininfor', 0, '2022-05-21 08:30:54', NULL, NULL, '登录日志菜单', 0),
       (20, '用户查询', 5, 1, '', '', '', 0, 1, 3, 1, 1, 'system:user:query', '#', 0, '2022-05-21 08:30:54', NULL, NULL,
        '', 0),
       (21, '用户新增', 5, 2, '', '', '', 0, 1, 3, 1, 1, 'system:user:add', '#', 0, '2022-05-21 08:30:54', NULL, NULL,
        '', 0),
       (22, '用户修改', 5, 3, '', '', '', 0, 1, 3, 1, 1, 'system:user:edit', '#', 0, '2022-05-21 08:30:54', NULL, NULL,
        '', 0),
       (23, '用户删除', 5, 4, '', '', '', 0, 1, 3, 1, 1, 'system:user:remove', '#', 0, '2022-05-21 08:30:54', NULL,
        NULL, '', 0),
       (24, '用户导出', 5, 5, '', '', '', 0, 1, 3, 1, 1, 'system:user:export', '#', 0, '2022-05-21 08:30:54', NULL,
        NULL, '', 0),
       (25, '用户导入', 5, 6, '', '', '', 0, 1, 3, 1, 1, 'system:user:import', '#', 0, '2022-05-21 08:30:54', NULL,
        NULL, '', 0),
       (26, '重置密码', 5, 7, '', '', '', 0, 1, 3, 1, 1, 'system:user:resetPwd', '#', 0, '2022-05-21 08:30:54', NULL,
        NULL, '', 0),
       (27, '角色查询', 6, 1, '', '', '', 0, 1, 3, 1, 1, 'system:role:query', '#', 0, '2022-05-21 08:30:54', NULL, NULL,
        '', 0),
       (28, '角色新增', 6, 2, '', '', '', 0, 1, 3, 1, 1, 'system:role:add', '#', 0, '2022-05-21 08:30:54', NULL, NULL,
        '', 0),
       (29, '角色修改', 6, 3, '', '', '', 0, 1, 3, 1, 1, 'system:role:edit', '#', 0, '2022-05-21 08:30:54', NULL, NULL,
        '', 0),
       (30, '角色删除', 6, 4, '', '', '', 0, 1, 3, 1, 1, 'system:role:remove', '#', 0, '2022-05-21 08:30:54', NULL,
        NULL, '', 0),
       (31, '角色导出', 6, 5, '', '', '', 0, 1, 3, 1, 1, 'system:role:export', '#', 0, '2022-05-21 08:30:54', NULL,
        NULL, '', 0),
       (32, '菜单查询', 7, 1, '', '', '', 0, 1, 3, 1, 1, 'system:menu:query', '#', 0, '2022-05-21 08:30:54', NULL, NULL,
        '', 0),
       (33, '菜单新增', 7, 2, '', '', '', 0, 1, 3, 1, 1, 'system:menu:add', '#', 0, '2022-05-21 08:30:54', NULL, NULL,
        '', 0),
       (34, '菜单修改', 7, 3, '', '', '', 0, 1, 3, 1, 1, 'system:menu:edit', '#', 0, '2022-05-21 08:30:54', NULL, NULL,
        '', 0),
       (35, '菜单删除', 7, 4, '', '', '', 0, 1, 3, 1, 1, 'system:menu:remove', '#', 0, '2022-05-21 08:30:54', NULL,
        NULL, '', 0),
       (36, '部门查询', 8, 1, '', '', '', 0, 1, 3, 1, 1, 'system:dept:query', '#', 0, '2022-05-21 08:30:54', NULL, NULL,
        '', 0),
       (37, '部门新增', 8, 2, '', '', '', 0, 1, 3, 1, 1, 'system:dept:add', '#', 0, '2022-05-21 08:30:54', NULL, NULL,
        '', 0),
       (38, '部门修改', 8, 3, '', '', '', 0, 1, 3, 1, 1, 'system:dept:edit', '#', 0, '2022-05-21 08:30:54', NULL, NULL,
        '', 0),
       (39, '部门删除', 8, 4, '', '', '', 0, 1, 3, 1, 1, 'system:dept:remove', '#', 0, '2022-05-21 08:30:54', NULL,
        NULL, '', 0),
       (40, '岗位查询', 9, 1, '', '', '', 0, 1, 3, 1, 1, 'system:post:query', '#', 0, '2022-05-21 08:30:54', NULL, NULL,
        '', 0),
       (41, '岗位新增', 9, 2, '', '', '', 0, 1, 3, 1, 1, 'system:post:add', '#', 0, '2022-05-21 08:30:54', NULL, NULL,
        '', 0),
       (42, '岗位修改', 9, 3, '', '', '', 0, 1, 3, 1, 1, 'system:post:edit', '#', 0, '2022-05-21 08:30:54', NULL, NULL,
        '', 0),
       (43, '岗位删除', 9, 4, '', '', '', 0, 1, 3, 1, 1, 'system:post:remove', '#', 0, '2022-05-21 08:30:54', NULL,
        NULL, '', 0),
       (44, '岗位导出', 9, 5, '', '', '', 0, 1, 3, 1, 1, 'system:post:export', '#', 0, '2022-05-21 08:30:54', NULL,
        NULL, '', 0),
       (54, '操作查询', 18, 1, '#', '', '', 0, 1, 3, 1, 1, 'monitor:operlog:query', '#', 0, '2022-05-21 08:30:54', NULL,
        NULL, '', 0),
       (55, '操作删除', 18, 2, '#', '', '', 0, 1, 3, 1, 1, 'monitor:operlog:remove', '#', 0, '2022-05-21 08:30:54',
        NULL, NULL, '', 0),
       (56, '日志导出', 18, 4, '#', '', '', 0, 1, 3, 1, 1, 'monitor:operlog:export', '#', 0, '2022-05-21 08:30:54',
        NULL, NULL, '', 0),
       (57, '登录查询', 19, 1, '#', '', '', 0, 1, 3, 1, 1, 'monitor:logininfor:query', '#', 0, '2022-05-21 08:30:54',
        NULL, NULL, '', 0),
       (58, '登录删除', 19, 2, '#', '', '', 0, 1, 3, 1, 1, 'monitor:logininfor:remove', '#', 0, '2022-05-21 08:30:54',
        NULL, NULL, '', 0),
       (59, '日志导出', 19, 3, '#', '', '', 0, 1, 3, 1, 1, 'monitor:logininfor:export', '#', 0, '2022-05-21 08:30:54',
        NULL, NULL, '', 0),
       (60, '在线查询', 13, 1, '#', '', '', 0, 1, 3, 1, 1, 'monitor:online:query', '#', 0, '2022-05-21 08:30:54', NULL,
        NULL, '', 0),
       (61, '批量强退', 13, 2, '#', '', '', 0, 1, 3, 1, 1, 'monitor:online:batchLogout', '#', 0, '2022-05-21 08:30:54',
        NULL, NULL, '', 0),
       (62, '单条强退', 13, 3, '#', '', '', 0, 1, 3, 1, 1, 'monitor:online:forceLogout', '#', 0, '2022-05-21 08:30:54',
        NULL, NULL, '', 0),
       (2013, '分组管理', 2033, 1, 'catalog', 'datasync/catalog/index', NULL, 0, 0, 2, 1, 1, 'datasync:catalog:list',
        'tool', 1, '2023-05-10 17:43:08', 1, '2025-03-23 15:32:45', '', 0),
       (2015, '接口文档', 0, 10, 'tool', NULL, NULL, 0, 0, 1, 1, 1, NULL, 'documentation', 1, '2023-07-06 16:27:09', 1,
        '2026-03-28 17:41:53', '', 0),
       (2016, '系统接口', 2015, 1, 'swagger', 'tool/swagger/index', NULL, 0, 1, 2, 1, 1, 'tool:swagger:list', 'swagger',
        1, '2023-07-06 16:28:21', NULL, NULL, '', 0),
       (2031, '环境管理', 2033, 2, 'env', 'system/env/index', NULL, 0, 1, 2, 1, 1, 'system:env:list', 'skill', 1,
        '2024-05-02 17:11:53', 1, '2026-03-28 17:03:23', '', 0),
       (2033, '平台运维', 0, 6, 'config', NULL, NULL, 0, 0, 1, 1, 1, NULL, 'job', 1, '2024-05-02 17:54:44', 1,
        '2026-03-28 17:38:10', '', 0),
       (2039, '资源管理', 2033, 3, 'resource', 'system/resource/index', NULL, 0, 1, 2, 1, 1, 'system:resource:list',
        'skill', 1, '2024-05-02 17:11:53', 1, '2026-03-28 17:03:28', '', 0),
       (2043, '数据服务', 0, 5, 'oneapi', NULL, NULL, 0, 0, 1, 1, 1, NULL, 'excel', 1, '2025-03-15 11:14:57', 1,
        '2026-03-28 11:43:08', '', 0),
       (2044, 'Api定义', 2043, 1, 'oneapi', 'oneapi/index', NULL, 0, 0, 2, 1, 1, NULL, 'select', 1,
        '2025-03-15 11:16:37', 1, '2026-03-28 17:39:47', '', 0),
       (2051, '数据资产', 0, 1, 'data_asset', NULL, NULL, 0, 0, 1, 1, 1, NULL, 'form', 1, '2026-03-26 09:47:26', 1,
        '2026-03-28 17:42:30', '', 0),
       (2052, '元数据管理', 2051, 1, 'metadata', NULL, NULL, 0, 0, 1, 1, 1, NULL, 'code', 1, '2026-03-26 09:48:57',
        NULL, NULL, '', 0),
       (2053, '数据源类型', 2052, 1, 'datasourceType', 'metadata/datasourceType/index', NULL, 0, 0, 2, 1, 1, NULL,
        'example', 1, '2026-03-26 09:49:54', 1, '2026-03-26 09:52:59', '', 0),
       (2054, '数据源定义', 2052, 2, 'datasource', 'metadata/datasource/index', NULL, 0, 0, 2, 1, 1, NULL,
        'international', 1, '2026-03-26 09:51:21', 1, '2026-03-26 09:52:47', '', 0),
       (2055, '数据表查询', 2052, 3, 'table', 'metadata/table/index', NULL, 0, 0, 2, 1, 1, NULL, 'table', 1,
        '2026-03-26 09:52:09', NULL, NULL, '', 0),
       (2061, '告警中心', 2, 6, 'alert', NULL, NULL, 0, 0, 1, 0, 0, NULL, 'message', 1, '2026-03-27 22:50:04', 1,
        '2026-03-28 11:49:31', '告警中心目录', 0),
       (2062, '告警实例', 2061, 1, 'channel', 'monitor/alert/channel/index', NULL, 0, 0, 2, 1, 1,
        'monitor:alertChannel:list', 'guide', 1, '2026-03-27 22:50:04', 1, '2026-03-27 22:50:04', '告警实例菜单', 0),
       (2063, '告警组', 2061, 2, 'group', 'monitor/alert/group/index', NULL, 0, 0, 2, 1, 1, 'monitor:alertGroup:list',
        'peach', 1, '2026-03-27 22:50:04', 1, '2026-03-27 22:50:04', '告警组菜单', 0),
       (2064, '告警历史', 2061, 3, 'record', 'monitor/alert/record/index', NULL, 0, 0, 2, 1, 1,
        'monitor:alertRecord:list', 'tickets', 1, '2026-03-27 22:50:04', 1, '2026-03-27 22:50:04', '告警历史菜单', 0),
       (2065, '实例查询', 2062, 1, '#', NULL, NULL, 0, 0, 3, 0, 1, 'monitor:alertChannel:query', '#', 1,
        '2026-03-27 22:50:04', 1, '2026-03-27 22:50:04', '', 0),
       (2066, '实例新增', 2062, 2, '#', NULL, NULL, 0, 0, 3, 0, 1, 'monitor:alertChannel:add', '#', 1,
        '2026-03-27 22:50:04', 1, '2026-03-27 22:50:04', '', 0),
       (2067, '实例修改', 2062, 3, '#', NULL, NULL, 0, 0, 3, 0, 1, 'monitor:alertChannel:edit', '#', 1,
        '2026-03-27 22:50:04', 1, '2026-03-27 22:50:04', '', 0),
       (2068, '实例删除', 2062, 4, '#', NULL, NULL, 0, 0, 3, 0, 1, 'monitor:alertChannel:remove', '#', 1,
        '2026-03-27 22:50:04', 1, '2026-03-27 22:50:04', '', 0),
       (2069, '实例测试', 2062, 5, '#', NULL, NULL, 0, 0, 3, 0, 1, 'monitor:alertChannel:test', '#', 1,
        '2026-03-27 22:50:04', 1, '2026-03-27 22:50:04', '', 0),
       (2070, '告警组查询', 2063, 1, '#', NULL, NULL, 0, 0, 3, 0, 1, 'monitor:alertGroup:query', '#', 1,
        '2026-03-27 22:50:04', 1, '2026-03-27 22:50:04', '', 0),
       (2071, '告警组新增', 2063, 2, '#', NULL, NULL, 0, 0, 3, 0, 1, 'monitor:alertGroup:add', '#', 1,
        '2026-03-27 22:50:04', 1, '2026-03-27 22:50:04', '', 0),
       (2072, '告警组修改', 2063, 3, '#', NULL, NULL, 0, 0, 3, 0, 1, 'monitor:alertGroup:edit', '#', 1,
        '2026-03-27 22:50:04', 1, '2026-03-27 22:50:04', '', 0),
       (2073, '告警组删除', 2063, 4, '#', NULL, NULL, 0, 0, 3, 0, 1, 'monitor:alertGroup:remove', '#', 1,
        '2026-03-27 22:50:04', 1, '2026-03-27 22:50:04', '', 0),
       (2074, '历史查询', 2064, 1, '#', NULL, NULL, 0, 0, 3, 0, 1, 'monitor:alertRecord:query', '#', 1,
        '2026-03-27 22:50:04', 1, '2026-03-27 22:50:04', '', 0),
       (2075, '执行告警', 2064, 2, '#', NULL, NULL, 0, 0, 3, 0, 1, 'monitor:alertRecord:execute', '#', 1,
        '2026-03-27 22:50:04', 1, '2026-03-27 22:50:04', '', 0),
       (2076, '重试告警', 2064, 3, '#', NULL, NULL, 0, 0, 3, 0, 1, 'monitor:alertRecord:retry', '#', 1,
        '2026-03-27 22:50:04', 1, '2026-03-27 22:50:04', '', 0),
       (2077, '数据接入', 0, 2, 'data_integrate', NULL, NULL, 0, 0, 1, 1, 1, NULL, 'international', 1,
        '2026-03-28 11:24:33', NULL, NULL, '', 0),
       (2078, '实时采集', 2077, 1, 'datasync', NULL, NULL, 0, 0, 1, 1, 1, NULL, 'example', 1, '2026-03-28 11:25:43', 1,
        '2026-03-28 17:38:41', '', 0),
       (2079, '任务定义', 2078, 1, 'rtc_job', 'datasync/job/index', NULL, 0, 0, 2, 1, 1, NULL, 'select', 1,
        '2026-03-28 11:26:24', 1, '2026-03-28 16:53:56', '', 0),
       (2080, '任务实例', 2078, 2, 'rtc_job_instance', 'datasync/instance/index', NULL, 0, 0, 2, 1, 1, NULL, 'color', 1,
        '2026-03-28 11:27:04', 1, '2026-03-28 16:54:05', '', 0),
       (2081, '数据集成', 2077, 2, 'dig', NULL, NULL, 0, 0, 1, 1, 1, NULL, 'date', 1, '2026-03-28 11:28:05', NULL, NULL,
        '', 0),
       (2082, '任务定义', 2081, 1, 'dig_job', 'dig/job/index', NULL, 0, 0, 2, 1, 1, NULL, 'select', 1,
        '2026-03-28 11:29:05', 1, '2026-03-28 16:54:13', '', 0),
       (2083, '任务实例', 2081, 2, 'dig_instance', 'dig/instance/index', NULL, 0, 0, 2, 1, 1, NULL, 'date', 1,
        '2026-03-28 11:29:37', 1, '2026-03-28 16:54:19', '', 0),
       (2084, '数据开发', 0, 3, 'data_develop', NULL, NULL, 0, 0, 1, 1, 1, NULL, 'skill', 1, '2026-03-28 11:30:35',
        NULL, NULL, '', 0),
       (2085, 'flink开发', 2084, 1, 'flink', NULL, NULL, 0, 0, 1, 1, 1, NULL, 'rate', 1, '2026-03-28 11:31:25', NULL,
        NULL, '', 0),
       (2086, '任务定义', 2085, 1, 'flink_job', 'flink/job/index', NULL, 0, 0, 2, 1, 1, NULL, 'select', 1,
        '2026-03-28 11:32:22', 1, '2026-03-28 16:54:57', '', 0),
       (2087, '任务实例', 2085, 2, 'flink_instance', 'flink/instance/index', NULL, 0, 0, 2, 1, 1, NULL, 'date', 1,
        '2026-03-28 11:33:01', 1, '2026-03-28 16:55:04', '', 0),
       (2088, 'spark开发', 2084, 2, 'spark', NULL, NULL, 0, 0, 1, 1, 1, NULL, 'druid', 1, '2026-03-28 11:36:16', NULL,
        NULL, '', 0),
       (2089, '任务定义', 2088, 1, 'spark_job', 'spark/job/index', NULL, 0, 0, 2, 1, 1, NULL, 'select', 1,
        '2026-03-28 11:36:54', 1, '2026-03-28 16:55:11', '', 0),
       (2090, '任务实例', 2088, 2, 'spark_instance', 'spark/instance/index', NULL, 0, 0, 2, 1, 1, NULL, 'date', 1,
        '2026-03-28 11:37:30', 1, '2026-03-28 16:55:17', '', 0),
       (2091, '数据治理', 0, 4, 'data_governance', NULL, NULL, 0, 0, 1, 1, 1, NULL, 'server', 1, '2026-03-28 11:38:49',
        NULL, NULL, '', 0),
       (2092, '数据质量', 2091, 1, 'dataquality', NULL, NULL, 0, 0, 1, 1, 1, NULL, 'time', 1, '2026-03-28 11:39:18',
        NULL, NULL, '', 0),
       (2093, '规则管理', 2092, 1, 'dataquality/rule', 'dataquality/rule/index', NULL, 0, 0, 2, 1, 1, NULL, 'druid', 1,
        '2026-03-28 11:40:05', NULL, NULL, '', 0),
       (2094, '执行记录', 2092, 2, 'dataquality/result', 'dataquality/result/index', NULL, 0, 0, 2, 1, 1, NULL, 'row',
        1, '2026-03-28 11:40:47', NULL, NULL, '', 0),
       (2095, '通知公告', 2033, 4, 'system_notice', 'system/notice/index', NULL, 0, 0, 2, 1, 1, 'system:notice:list',
        'message', 1, '2026-03-28 11:45:19', 1, '2026-03-28 16:51:55', '', 0),
       (2096, '告警中心', 2033, 5, 'alert', NULL, NULL, 0, 0, 1, 1, 1, NULL, 'guide', 1, '2026-03-28 11:47:08', NULL,
        NULL, '', 0),
       (2097, '告警实例', 2096, 1, 'alert_instance', 'monitor/alert/channel/index', NULL, 0, 0, 2, 1, 1, NULL, 'date',
        1, '2026-03-28 11:47:51', 1, '2026-03-28 16:52:24', '', 0),
       (2098, '告警组', 2096, 2, 'alert_group', 'monitor/alert/group/index', NULL, 0, 0, 2, 1, 1, NULL, 'list', 1,
        '2026-03-28 11:48:36', 1, '2026-03-28 16:52:34', '', 0),
       (2099, '告警记录', 2096, 3, 'alert_record', 'monitor/alert/record/index', NULL, 0, 0, 2, 1, 1, NULL, 'log', 1,
        '2026-03-28 11:49:20', 1, '2026-03-28 16:52:37', '', 0),
       (2100, '数据血缘', 2052, 4, 'lineage', 'metadata/lineage/index', NULL, 0, 0, 2, 1, 1, 'metadata:lineage:view',
        'chart', 1, '2026-08-26 10:38:32', NULL, NULL, '血缘图页面', 0),
       (2101, '业务元数据编辑', 2055, 1, '#', '', '', 0, 1, 3, 1, 1, 'metadata:bizmeta:edit', '#', 1,
        '2026-08-26 10:38:32', NULL, NULL, '表详情/数据源业务元数据维护按钮', 0),
       (2102, '血缘登记', 2100, 1, '#', '', '', 0, 1, 3, 1, 1, 'metadata:lineage:edit', '#', 1, '2026-08-26 10:38:32',
        NULL, NULL, '血缘登记/删除按钮', 0),
       (2103, 'API监控', 2043, 2, 'monitor', 'oneapi/monitor', NULL, 0, 0, 2, 1, 1, 'oneapi:monitor:view', 'monitor', 1,
        '2026-08-28 10:00:00', NULL, NULL, '', 0),
       (2104, 'API统计', 2043, 3, 'stats', 'oneapi/stats', NULL, 0, 0, 2, 1, 1, 'oneapi:stats:view', 'chart', 1,
        '2026-08-28 10:00:00', NULL, NULL, '', 0),
       (2105, '调用历史', 2043, 4, 'history', 'oneapi/history', NULL, 0, 0, 2, 1, 1, 'oneapi:history:view', 'log', 1,
        '2026-08-28 10:00:00', NULL, NULL, '', 0),
       (2106, '规则模板', 2092, 3, 'dataquality/template', 'dataquality/template/index', NULL, 0, 0, 2, 1, 1,
        'dq:template:list', 'checkbox', 1, NULL, 1, '2026-08-30 17:16:33', '规则模板管\n  理菜单', 0),
       (2107, '质量报告', 2092, 4, 'dataquality/report', 'dataquality/report/index', NULL, 0, 0, 2, 1, 1,
        'dq:report:list', 'date', 1, NULL, 1, '2026-08-31 13:57:07', '数据质量报告菜单', 0),
       (3000, '湖智AI', 0, 11, 'lake-intelligence', NULL, NULL, 0, 1, 1, 1, 1, NULL, 'international', 0,
        '2026-10-04 22:34:49', 1, '2026-10-05 07:31:54', '湖智AI平台', 0),
       (3001, '以图搜图', 3000, 3, 'similarity', NULL, NULL, 0, 1, 1, 1, 1, NULL, 'search', 0, '2026-10-04 22:34:49', 1,
        '2026-10-05 07:30:31', '以图搜图功能', 0),
       (3002, '数据集管理', 3000, 1, 'dataset/list', 'lake-intelligence/DatasetList', NULL, 0, 1, 2, 1, 1,
        'lakeintelligence:dataset:list', 'documentation', 0, '2026-10-04 22:34:49', 1, '2026-10-05 07:48:59',
        '数据集管理菜单', 0),
       (3003, '模型管理', 3000, 2, 'model/list', 'lake-intelligence/ModelList', NULL, 0, 1, 2, 1, 1,
        'lakeintelligence:model:list', 'international', 0, '2026-10-04 22:34:49', 1, '2026-10-05 07:49:18',
        '模型管理菜单', 0),
       (3004, '向量构建', 3001, 1, 'vector/build', 'lakeintelligence/similarity/VectorBuild', NULL, 0, 1, 2, 1, 1,
        'lakeintelligence:vector:build', 'icon', 0, '2026-10-04 22:34:49', 1, '2026-10-05 07:49:26', '向量构建菜单', 0),
       (3005, '相似检索', 3001, 2, 'search', 'lakeintelligence/similarity/SimilaritySearch', NULL, 0, 1, 2, 1, 1,
        'lakeintelligence:search:query', 'search', 0, '2026-10-04 22:34:49', 1, '2026-10-05 07:49:36', '相似检索菜单',
        0),
       (3006, '图片分类', 3000, 4, 'classification', NULL, NULL, 0, 1, 1, 1, 1, NULL, 'color', 0, '2026-10-04 22:34:49',
        1, '2026-10-04 22:42:53', '图片分类功能', 0),
       (3009, '图片分类', 3006, 1, 'classify', 'lakeintelligence/classification/ImageClassify', NULL, 0, 1, 2, 1, 1,
        'lakeintelligence:classify:predict', 'example', 0, '2026-10-04 22:34:49', 1, '2026-10-05 07:50:14',
        '图片分类菜单', 0),
       (3010, '数据集上传', 3002, 1, '', '', NULL, 0, 1, 3, 1, 1, 'lakeintelligence:dataset:add', '#', 0,
        '2026-10-04 22:34:49', NULL, NULL, '', 0),
       (3011, '数据集删除', 3002, 2, '', '', NULL, 0, 1, 3, 1, 1, 'lakeintelligence:dataset:remove', '#', 0,
        '2026-10-04 22:34:49', NULL, NULL, '', 0),
       (3012, '模型添加', 3003, 1, '', '', NULL, 0, 1, 3, 1, 1, 'lakeintelligence:model:add', '#', 0,
        '2026-10-04 22:34:49', NULL, NULL, '', 0),
       (3013, '模型删除', 3003, 2, '', '', NULL, 0, 1, 3, 1, 1, 'lakeintelligence:model:remove', '#', 0,
        '2026-10-04 22:34:49', NULL, NULL, '', 0),
       (3014, '向量构建触发', 3004, 1, '', '', NULL, 0, 1, 3, 1, 1, 'lakeintelligence:vector:build', '#', 0,
        '2026-10-04 22:34:49', NULL, NULL, '', 0),
       (3015, '训练任务管理', 3000, 5, 'tasks', 'lake-intelligence/tasks/TaskList', NULL, 0, 1, 2, 1, 1,
        'lakeintelligence:task:list', 'list', 0, '2026-10-05 21:12:27', 1, '2026-10-06 21:00:12', '任务管理菜单', 0);

--
-- Table structure for table `sys_notice`
--

DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`
(
    `notice_id`      int          NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    `notice_title`   varchar(64)  NOT NULL COMMENT '公告标题',
    `notice_type`    smallint     NOT NULL COMMENT '公告类型（1通知 2公告）',
    `notice_content` text COMMENT '公告内容',
    `status`         smallint     NOT NULL DEFAULT '0' COMMENT '公告状态（1正常 0关闭）',
    `creator_id`     bigint       NOT NULL COMMENT '创建者ID',
    `create_time`    datetime              DEFAULT NULL COMMENT '创建时间',
    `updater_id`     bigint                DEFAULT NULL COMMENT '更新者ID',
    `update_time`    datetime              DEFAULT NULL COMMENT '更新时间',
    `remark`         varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
    `deleted`        tinyint(1)   NOT NULL DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`notice_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='通知公告表';

--
-- Dumping data for table `sys_notice`
--

INSERT INTO `sys_notice`
VALUES (1, '温馨提醒：lacus2.0版本发布啦', 2, '新版本内容', 1, 1, '2022-05-21 08:30:55', 1, '2026-03-07 09:40:22',
        '管理员', 0),
       (2, '维护通知：2018-07-01 lacus系统凌晨维护', 1, '维护内容', 1, 1, '2022-05-21 08:30:55', 1,
        '2024-03-03 12:34:46', '管理员', 0);

--
-- Table structure for table `sys_operation_log`
--

DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log`
(
    `operation_id`      bigint       NOT NULL AUTO_INCREMENT COMMENT '日志主键',
    `business_type`     smallint     NOT NULL DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
    `request_method`    smallint     NOT NULL DEFAULT '0' COMMENT '请求方式',
    `request_module`    varchar(64)  NOT NULL DEFAULT '' COMMENT '请求模块',
    `request_url`       varchar(256) NOT NULL DEFAULT '' COMMENT '请求URL',
    `called_method`     varchar(128) NOT NULL DEFAULT '' COMMENT '调用方法',
    `operator_type`     smallint     NOT NULL DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
    `user_id`           bigint                DEFAULT '0' COMMENT '用户ID',
    `username`          varchar(32)           DEFAULT '' COMMENT '操作人员',
    `operator_ip`       varchar(128)          DEFAULT '' COMMENT '操作人员ip',
    `operator_location` varchar(256)          DEFAULT '' COMMENT '操作地点',
    `dept_id`           bigint                DEFAULT '0' COMMENT '部门ID',
    `dept_name`         varchar(64)           DEFAULT NULL COMMENT '部门名称',
    `operation_param`   varchar(2048)         DEFAULT '' COMMENT '请求参数',
    `operation_result`  varchar(2048)         DEFAULT '' COMMENT '返回参数',
    `status`            smallint     NOT NULL DEFAULT '1' COMMENT '操作状态（1正常 0异常）',
    `error_stack`       varchar(2048)         DEFAULT '' COMMENT '错误消息',
    `operation_time`    datetime     NOT NULL COMMENT '操作时间',
    `deleted`           tinyint(1)   NOT NULL DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`operation_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 466
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='操作日志记录';

--
-- Table structure for table `sys_post`
--

DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`
(
    `post_id`     bigint      NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
    `post_code`   varchar(64) NOT NULL COMMENT '岗位编码',
    `post_name`   varchar(64) NOT NULL COMMENT '岗位名称',
    `post_sort`   int         NOT NULL COMMENT '显示顺序',
    `status`      smallint    NOT NULL COMMENT '状态（1正常 0停用）',
    `remark`      varchar(512)         DEFAULT NULL COMMENT '备注',
    `creator_id`  bigint               DEFAULT NULL,
    `create_time` datetime             DEFAULT NULL COMMENT '创建时间',
    `updater_id`  bigint               DEFAULT NULL,
    `update_time` datetime             DEFAULT NULL COMMENT '更新时间',
    `deleted`     tinyint(1)  NOT NULL DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`post_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='岗位信息表';

--
-- Dumping data for table `sys_post`
--

INSERT INTO `sys_post`
VALUES (1, 'ceo', '总经理', 1, 1, '', NULL, '2022-05-21 08:30:54', 1, '2023-05-06 14:04:54', 0),
       (2, 'se', '项目经理', 2, 1, '', NULL, '2022-05-21 08:30:54', NULL, NULL, 0),
       (3, 'hr', '人力资源', 3, 1, '', NULL, '2022-05-21 08:30:54', NULL, NULL, 0),
       (4, 'user', '普通员工', 5, 0, '', NULL, '2022-05-21 08:30:54', 1, '2023-03-15 15:16:28', 1);

--
-- Table structure for table `sys_resources`
--

DROP TABLE IF EXISTS `sys_resources`;
CREATE TABLE `sys_resources`
(
    `id`           int         NOT NULL AUTO_INCREMENT COMMENT 'resource id',
    `pid`          int                                                           DEFAULT NULL COMMENT 'parent resource id',
    `name`         varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'alia name',
    `file_name`    varchar(200)                                                  DEFAULT NULL COMMENT 'file name',
    `file_path`    varchar(500)                                                  DEFAULT NULL COMMENT 'file path',
    `remark`       varchar(200)                                                  DEFAULT NULL,
    `type`         tinyint                                                       DEFAULT NULL COMMENT 'resource type: 0 FILE，1 UDF',
    `size`         bigint                                                        DEFAULT NULL COMMENT 'resource size',
    `is_directory` tinyint                                                       DEFAULT NULL,
    `deleted`      tinyint     NOT NULL                                          DEFAULT '0' COMMENT '删除标识：正常 0 删除 1',
    `creator_id`   varchar(64) NOT NULL                                          DEFAULT '' COMMENT '创建人',
    `create_time`  datetime    NOT NULL                                          DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater_id`   varchar(128)                                                  DEFAULT NULL COMMENT '修改人',
    `update_time`  timestamp   NOT NULL                                          DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 8861
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `role_id`     bigint       NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name`   varchar(32)  NOT NULL COMMENT '角色名称',
    `role_key`    varchar(128) NOT NULL COMMENT '角色权限字符串',
    `role_sort`   int          NOT NULL COMMENT '显示顺序',
    `data_scope`  smallint              DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3: 本部门数据权限 4: 本部门及以下数据权限 5: 本人权限）',
    `dept_id_set` varchar(1024)         DEFAULT '' COMMENT '角色所拥有的部门数据权限',
    `status`      smallint     NOT NULL COMMENT '角色状态（1正常 0停用）',
    `creator_id`  bigint                DEFAULT NULL COMMENT '创建者ID',
    `create_time` datetime              DEFAULT NULL COMMENT '创建时间',
    `updater_id`  bigint                DEFAULT NULL COMMENT '更新者ID',
    `update_time` datetime              DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(512)          DEFAULT NULL COMMENT '备注',
    `deleted`     tinyint(1)   NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (`role_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='角色信息表';

--
-- Dumping data for table `sys_role`
--

INSERT INTO `sys_role`
VALUES (1, '超级管理员', 'admin', 1, 1, '', 1, NULL, '2022-05-21 08:30:54', NULL, NULL, '超级管理员', 0),
       (2, '普通角色', 'common', 2, 2, '4,6', 1, NULL, '2022-05-21 08:30:54', 1, '2026-03-28 16:58:52', '普通角色', 0),
       (3, '闲置角色', 'unused', 4, 2, '', 0, NULL, '2022-05-21 08:30:54', NULL, NULL, '未使用的角色', 1);

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`
(
    `role_id` bigint NOT NULL COMMENT '角色ID',
    `menu_id` bigint NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='角色和菜单关联表';

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `user_id`      bigint       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `post_id`      bigint                DEFAULT NULL COMMENT '职位id',
    `role_id`      bigint                DEFAULT NULL COMMENT '角色id',
    `dept_id`      bigint                DEFAULT NULL COMMENT '部门ID',
    `username`     varchar(64)  NOT NULL COMMENT '用户账号',
    `nick_name`    varchar(32)  NOT NULL COMMENT '用户昵称',
    `user_type`    smallint              DEFAULT '0' COMMENT '用户类型（00系统用户）',
    `email`        varchar(128)          DEFAULT '' COMMENT '用户邮箱',
    `phone_number` varchar(18)           DEFAULT '' COMMENT '手机号码',
    `sex`          smallint              DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
    `avatar`       varchar(512)          DEFAULT '' COMMENT '头像地址',
    `password`     varchar(128) NOT NULL DEFAULT '' COMMENT '密码',
    `status`       smallint     NOT NULL DEFAULT '0' COMMENT '帐号状态（1正常 2停用 3冻结）',
    `login_ip`     varchar(128)          DEFAULT '' COMMENT '最后登录IP',
    `login_date`   datetime              DEFAULT NULL COMMENT '最后登录时间',
    `creator_id`   bigint                DEFAULT NULL COMMENT '更新者ID',
    `create_time`  datetime              DEFAULT NULL COMMENT '创建时间',
    `updater_id`   bigint                DEFAULT NULL COMMENT '更新者ID',
    `update_time`  datetime              DEFAULT NULL COMMENT '更新时间',
    `remark`       varchar(512)          DEFAULT NULL COMMENT '备注',
    `deleted`      tinyint(1)   NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (`user_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 110
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='用户信息表';

--
-- Dumping data for table `sys_user`
--

INSERT INTO `sys_user`
VALUES (1, 1, 1, 4, 'admin', 'valarchie1', 0, 'lacus@163.com', '15888888889', 0, '',
        '$2a$10$rb1wRoEIkLbIknREEN1LH.FGs4g0oOS5t6l5LQ793nRaFO.SPHDHy', 1, '127.0.0.1', '2026-10-10 15:10:24', NULL,
        '2022-05-21 08:30:54', 1, '2026-10-10 15:10:24', '管理员', 0),
       (109, 1, 2, 4, 'demo', '游客', 0, '', '', 1, '', '$2a$10$0JB.QbSCyuhRBGT/ePFtOuqcTmX2xFOLGzVDuGO4026YqugtdzIHK',
        1, '127.0.0.1', '2025-07-13 19:57:54', 1, '2023-12-14 13:44:26', 1, '2025-07-13 19:57:54', NULL, 0);

SET FOREIGN_KEY_CHECKS = 1;
