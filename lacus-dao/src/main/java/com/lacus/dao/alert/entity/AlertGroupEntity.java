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
@TableName("alert_group")
@ApiModel(value = "AlertGroupEntity", description = "告警组")
public class AlertGroupEntity extends BaseEntity<AlertGroupEntity> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("group_code")
    private String groupCode;

    @TableField("group_name")
    private String groupName;

    @TableField("description")
    private String description;

    @TableField("enabled")
    private Boolean enabled;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
