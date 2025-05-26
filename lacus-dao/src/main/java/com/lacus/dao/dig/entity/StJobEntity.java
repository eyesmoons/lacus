package com.lacus.dao.dig.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author shengyu
 */
@EqualsAndHashCode(callSuper = true)
@TableName("st_job")
@Data
public class StJobEntity extends BaseEntity<StJobEntity> {
    @TableId(value = "job_id", type = IdType.AUTO)
    private Long jobId;

    @TableField("job_name")
    private String jobName;

    @TableField("env_id")
    private Long envId;

    @TableField("engine_name")
    private String engineName;

    @TableField("status")
    private Integer status;

    @TableField("description")
    private String description;
}
