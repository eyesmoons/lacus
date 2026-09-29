package com.lacus.dao.dig.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName("st_task_relation")
@Data
public class StTaskRelationEntity extends BaseEntity<StTaskRelationEntity> {
    @TableId(value = "relation_id", type = IdType.AUTO)
    private Long relationId;

    @TableField("job_id")
    private Long jobId;

    @TableField("source_task_id")
    private String sourceTaskId;

    @TableField("sink_task_id")
    private String sinkTaskId;
}
