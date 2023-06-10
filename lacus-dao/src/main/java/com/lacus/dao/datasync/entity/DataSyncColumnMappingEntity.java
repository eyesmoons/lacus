package com.lacus.dao.datasync.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("data_sync_column_mapping")
public class DataSyncColumnMappingEntity extends BaseEntity<DataSyncColumnMappingEntity> {

    @ApiModelProperty("主键")
    @TableId(value = "column_mapping_id", type = IdType.AUTO)
    private Long columnMappingId;

    @ApiModelProperty("任务ID")
    @TableField("job_id")
    private String jobId;

    @ApiModelProperty("输入源表字段ID")
    @TableField("source_column_id")
    private Long sourceColumnId;

    @ApiModelProperty("输出源表字段ID")
    @TableField("sink_column_id")
    private Long sinkColumnId;
}
