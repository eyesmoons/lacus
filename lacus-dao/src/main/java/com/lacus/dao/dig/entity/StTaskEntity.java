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
@TableName("st_task")
@Data
public class StTaskEntity extends BaseEntity<StTaskEntity> {
    @TableId(value = "task_id", type = IdType.AUTO)
    private String taskId;

    @TableField("task_name")
    private String taskName;

    @TableField("job_id")
    private Long jobId;

    @TableField("connector_type")
    private String connectorType;

    @TableField("connector_name")
    private String connectorName;

    @TableField("connection_config")
    private String connectionConfig;

    @TableField("position")
    private String position;
}
