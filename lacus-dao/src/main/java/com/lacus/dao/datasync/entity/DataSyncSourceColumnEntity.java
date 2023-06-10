package com.lacus.dao.datasync.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("data_sync_source_column")
public class DataSyncSourceColumnEntity extends BaseEntity<DataSyncSourceColumnEntity> {

    @ApiModelProperty("主键")
    @TableId(value = "source_column_id", type = IdType.AUTO)
    private Long sourceColumnId;

    @ApiModelProperty("任务ID")
    @TableField("job_id")
    private String jobId;

    @ApiModelProperty("输出源表ID")
    @TableField("source_table_id")
    private Long sourceTableId;

    @ApiModelProperty("输出源字段名称")
    @TableField("source_column_name")
    private String sourceColumnName;
}
