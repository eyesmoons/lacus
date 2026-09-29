package com.lacus.dao.alert.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("alert_send_log")
@ApiModel(value = "AlertSendLogEntity", description = "告警发送日志")
public class AlertSendLogEntity extends BaseEntity<AlertSendLogEntity> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("task_id")
    private Long taskId;

    @TableField("attempt_no")
    private Integer attemptNo;

    @TableField("request_payload")
    private String requestPayload;

    @TableField("response_payload")
    private String responsePayload;

    @TableField("success")
    private Boolean success;

    @TableField("cost_ms")
    private Integer costMs;

    @TableField("error_message")
    private String errorMessage;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
