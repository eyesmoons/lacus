package com.lacus.dao.dataCollect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("data_sync_job_instance")
public class DataSyncJobInstanceEntity extends BaseEntity<DataSyncJobInstanceEntity> {

    @ApiModelProperty("主键")
    @TableId(value = "instance_id", type = IdType.AUTO)
    private Long instanceId;

    @ApiModelProperty("任务ID")
    @TableField("job_id")
    private Long jobId;

    @TableField("instance_name")
    private String instanceName;

    @ApiModelProperty("flink任务ID")
    @TableField("application_id")
    private String applicationId;

    @ApiModelProperty("flink任务ID")
    @TableField("flink_job_id")
    private String flinkJobId;

    @TableField("job_script")
    private String jobScript;

    @ApiModelProperty("同步方式")
    @TableField("sync_type")
    private String syncType;

    @TableField("time_stamp")
    private String timeStamp;

    @ApiModelProperty("任务提交时间")
    @TableField("submit_time")
    private Date submitTime;

    @ApiModelProperty("任务结束时间")
    @TableField("finished_time")
    private Date finishedTime;

    @ApiModelProperty("savepoint地址")
    @TableField("save_point")
    private String savepoint;

    @ApiModelProperty("任务状态 1 RUNNING, 2 KILL, 3 FAILED")
    @TableField("status")
    private String status;
}
