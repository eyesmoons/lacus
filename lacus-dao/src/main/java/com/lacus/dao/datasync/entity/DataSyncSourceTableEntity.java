package com.lacus.dao.datasync.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("data_sync_source_table")
public class DataSyncSourceTableEntity extends BaseEntity<DataSyncSourceTableEntity> {

    @ApiModelProperty("主键")
    @TableId(value = "source_table_id", type = IdType.AUTO)
    private Long sourceTableId;

    @ApiModelProperty("任务ID")
    @TableField("job_id")
    private Long jobId;

    @ApiModelProperty("输出源库名称")
    @TableField("source_db_name")
    private String sourceDbName;

    @ApiModelProperty("输出源表名称")
    @TableField("source_table_name")
    private String sourceTableName;
}
