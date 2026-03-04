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
@Data
@TableName("st_job_instance")
public class StJobInstanceEntity extends BaseEntity<StJobInstanceEntity> {
    @TableId(value = "instance_id", type = IdType.AUTO)
    private Long instanceId;

    @TableField("instance_name")
    private String instanceName;

    @TableField("job_id")
    private Long jobId;

    @TableField("job_config")
    private String jobConfig;

    @TableField("status")
    private Integer status;

    @TableField("log_info")
    private String logInfo;

    @TableField(exist = false)
    private String jobName;
}
