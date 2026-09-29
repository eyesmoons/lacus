package com.lacus.dao.alert.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("alert_send_task")
@ApiModel(value = "AlertSendTaskEntity", description = "告警发送任务")
public class AlertSendTaskEntity extends BaseEntity<AlertSendTaskEntity> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("task_no")
    private String taskNo;

    @TableField("record_id")
    private Long recordId;

    @TableField("channel_instance_id")
    private Long channelInstanceId;

    @TableField("channel_type_code")
    private String channelTypeCode;

    @TableField("instance_code")
    private String instanceCode;

    @TableField("instance_name")
    private String instanceName;

    @TableField("status")
    private String status;

    @TableField("retry_count")
    private Integer retryCount;

    @TableField("max_retry_count")
    private Integer maxRetryCount;

    @TableField("next_retry_time")
    private Date nextRetryTime;

    @TableField("started_time")
    private Date startedTime;

    @TableField("finished_time")
    private Date finishedTime;

    @TableField("response_summary")
    private String responseSummary;

    @TableField("last_error")
    private String lastError;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
