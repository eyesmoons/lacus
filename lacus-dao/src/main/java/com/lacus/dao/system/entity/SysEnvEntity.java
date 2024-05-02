package com.lacus.dao.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 环境管理
 * </p>
 *
 * @author lacus
 * @since 2024-04-30
 */
@Getter
@Setter
@TableName("sys_env")
@ApiModel(value = "SysEnvEntity对象", description = "环境管理")
public class SysEnvEntity extends BaseEntity<SysEnvEntity> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增主键")
    @TableId(value = "env_id", type = IdType.AUTO)
    private Long envId;

    @ApiModelProperty("环境名称")
    @TableField("`name`")
    private String name;

    @ApiModelProperty("环境配置")
    @TableField("config")
    private String config;

    @ApiModelProperty("环境描述")
    @TableField("remark")
    private String remark;


    @Override
    public Serializable pkVal() {
        return this.envId;
    }

}
