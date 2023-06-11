package com.lacus.dao.datasync.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("data_sync_table_mapping")
public class DataSyncTableMappingEntity extends BaseEntity<DataSyncTableMappingEntity> {

    @ApiModelProperty("主键")
    @TableId(value = "table_mapping_id", type = IdType.AUTO)
    private Long tableMappingId;

    @ApiModelProperty("任务ID")
    @TableField("job_id")
    private String jobId;

    @ApiModelProperty("输入源表ID")
    @TableField("source_table_id")
    private Long sourceTableId;

    @ApiModelProperty("输出源表ID")
    @TableField("sink_table_id")
    private Long sinkTableId;
}
