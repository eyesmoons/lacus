package com.lacus.dao.spark.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import com.lacus.enums.SparkDeployModeEnum;
import com.lacus.enums.SparkStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * Spark任务实例
 */
@EqualsAndHashCode(callSuper = true)
@TableName("spark_job_instance")
@Data
public class SparkJobInstanceEntity extends BaseEntity<SparkJobInstanceEntity> {

    @TableId(value = "instance_id", type = IdType.AUTO)
    private Long instanceId;

    @TableField("job_id")
    private Long jobId;

    @TableField("instance_name")
    private String instanceName;

    @TableField("deploy_mode")
    private SparkDeployModeEnum deployMode;

    @TableField("application_id")
    private String applicationId;

    @TableField("job_script")
    private String jobScript;

    @TableField("submit_time")
    private Date submitTime;

    @TableField("finished_time")
    private Date finishedTime;

    @TableField("job_status")
    private SparkStatusEnum jobStatus;

    @TableField(exist = false)
    private String jobName;

    @TableField(exist = false)
    private String jobType;
}
