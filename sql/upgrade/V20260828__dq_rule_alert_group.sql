-- dq_rule 绑定告警组（为空则不告警），关联 alert_group.group_code
-- 幂等：通过 information_schema 判断列是否已存在，已存在则跳过

SET @col = (SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'dq_rule' AND COLUMN_NAME = 'alert_group_code');
SET @sql = IF(@col = 0,
  'ALTER TABLE dq_rule ADD COLUMN alert_group_code varchar(64) DEFAULT NULL COMMENT ''告警组编码（关联 alert_group.group_code，为空则不告警）''',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
