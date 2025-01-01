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

    @TableField("deploy_mode")
    private SparkDeployModeEnum deployMode;

    @TableField("instance_name")
    private String instanceName;

    @TableField("application_id")
    private String applicationId;

    @TableField("spark_job_id")
    private String sparkJobId;

    @TableField("job_script")
    private String jobScript;

    @TableField("submit_time")
    private Date submitTime;

    @TableField("finished_time")
    private Date finishedTime;

    @TableField("status")
    private SparkStatusEnum status;

    @TableField(exist = false)
    private String jobName;

    @TableField(exist = false)
    private String jobType;
}
