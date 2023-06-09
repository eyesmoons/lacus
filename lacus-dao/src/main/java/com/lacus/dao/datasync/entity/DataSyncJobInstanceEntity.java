package com.lacus.dao.datasync.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

@Data
@TableName("data_sync_job_instance")
public class DataSyncJobInstanceEntity extends BaseEntity<DataSyncJobInstanceEntity> {

    @ApiModelProperty("主键")
    @TableId(value = "instance_id", type = IdType.AUTO)
    private Long instanceId;

    @ApiModelProperty("任务ID")
    @TableField("job_id")
    private Long jobId;

    @ApiModelProperty("flink任务ID")
    @TableField("application_id")
    private String applicationId;

    @ApiModelProperty("类型 1 source 2 sink")
    @TableField("type")
    private Integer type;

    @ApiModelProperty("任务提交时间")
    @TableField("submit_time")
    private Date submitTime;

    @ApiModelProperty("任务结束时间")
    @TableField("finished_time")
    private Date finishedTime;

    @ApiModelProperty("savepoint地址")
    @TableField("savepoint")
    private String savepoint;

    @ApiModelProperty("任务状态 1 RUNNING, 2 KILL, 3 FAILED")
    @TableField("status")
    private Integer status;
}
