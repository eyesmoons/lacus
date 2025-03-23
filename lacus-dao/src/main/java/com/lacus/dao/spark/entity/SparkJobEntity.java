package com.lacus.dao.spark.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import com.lacus.enums.SparkDeployModeEnum;
import com.lacus.enums.SparkJobTypeEnum;
import com.lacus.enums.SparkStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author shengyu
 * @date 2024/12/31 17:35
 */
@EqualsAndHashCode(callSuper = true)
@TableName("spark_job")
@Data
public class SparkJobEntity extends BaseEntity<SparkJobEntity> {
    @TableId(value = "job_id", type = IdType.AUTO)
    private Long jobId;

    @TableField("job_name")
    private String jobName;

    @TableField("application_id")
    private String applicationId;

    @TableField("job_type")
    private SparkJobTypeEnum jobType;

    @TableField("master")
    private String master;

    @TableField("deploy_mode")
    private SparkDeployModeEnum deployMode;

    @TableField("parallelism")
    private Integer parallelism;

    @TableField("driver_cores")
    private Integer driverCores;

    @TableField("driver_memory")
    private Integer driverMemory;

    @TableField("num_executors")
    private Integer numExecutors;

    @TableField("executor_cores")
    private Integer executorCores;

    @TableField("executor_memory")
    private Integer executorMemory;

    @TableField("other_spark_conf")
    private String otherSparkConf;

    @TableField("main_jar_path")
    private Integer mainJarPath;

    @TableField("main_class_name")
    private String mainClassName;

    @TableField("main_args")
    private String mainArgs;

    @TableField("sql_content")
    private String sqlContent;

    @TableField("queue")
    private String queue;

    @TableField("namespace")
    private String namespace;

    @TableField("env_id")
    private Long envId;

    @TableField("job_status")
    private SparkStatusEnum jobStatus;

    @TableField("remark")
    private String remark;

    @TableField("schedule_status")
    private Boolean scheduleStatus;

    @TableField("cron_expression")
    private String cronExpression;
}
