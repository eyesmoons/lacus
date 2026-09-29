-- 工作台聚合查询索引：四张实例表原本只有主键索引，
-- 趋势/分布查询按时间列过滤，组合 (时间列, status) 可走覆盖扫描避开 job_script/job_config 等 longtext 字段。
-- 在含数据的环境执行前请评估表大小与写入影响。

ALTER TABLE flink_job_instance ADD INDEX idx_submit_time_status (submit_time, status);

ALTER TABLE spark_job_instance ADD INDEX idx_submit_time_status (submit_time, job_status);

ALTER TABLE data_sync_job_instance ADD INDEX idx_submit_time_status (submit_time, status);

ALTER TABLE st_job_instance ADD INDEX idx_start_time_status (start_time, status);
