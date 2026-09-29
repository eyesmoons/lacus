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
@TableName("alert_record")
@ApiModel(value = "AlertRecordEntity", description = "告警主记录")
public class AlertRecordEntity extends BaseEntity<AlertRecordEntity> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("record_no")
    private String recordNo;

    @TableField("group_id")
    private Long groupId;

    @TableField("group_code")
    private String groupCode;

    @TableField("group_name")
    private String groupName;

    @TableField("trigger_source")
    private String triggerSource;

    @TableField("biz_key")
    private String bizKey;

    @TableField("alert_level")
    private String alertLevel;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("ext_json")
    private String extJson;

    @TableField("status")
    private String status;

    @TableField("channel_count")
    private Integer channelCount;

    @TableField("success_count")
    private Integer successCount;

    @TableField("failed_count")
    private Integer failedCount;

    @TableField("requested_by")
    private String requestedBy;

    @TableField("requested_time")
    private Date requestedTime;

    @TableField("finished_time")
    private Date finishedTime;

    @TableField("error_message")
    private String errorMessage;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
