package com.lacus.dao.alert.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("alert_channel_instance")
@ApiModel(value = "AlertChannelInstanceEntity", description = "告警实例")
public class AlertChannelInstanceEntity extends BaseEntity<AlertChannelInstanceEntity> {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Long id;

    @TableField("channel_type_id")
    private Long channelTypeId;

    @TableField("instance_code")
    private String instanceCode;

    @TableField("instance_name")
    private String instanceName;

    @TableField("config_json")
    private String configJson;

    @TableField("enabled")
    private Boolean enabled;

    @TableField("test_status")
    private String testStatus;

    @TableField("last_test_time")
    private Date lastTestTime;

    @TableField("version")
    private Integer version;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
