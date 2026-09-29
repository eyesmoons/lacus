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
@TableName("alert_group_channel_rel")
@ApiModel(value = "AlertGroupChannelRelEntity", description = "告警组与实例关系")
public class AlertGroupChannelRelEntity extends BaseEntity<AlertGroupChannelRelEntity> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("group_id")
    private Long groupId;

    @TableField("channel_instance_id")
    private Long channelInstanceId;

    @TableField("notify_order")
    private Integer notifyOrder;

    @TableField("enabled")
    private Boolean enabled;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
