package com.lacus.dao.dataCollect.entity;

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
@TableName("data_sync_sink_table")
public class DataSyncSinkTableEntity extends BaseEntity<DataSyncSinkTableEntity> {

    @ApiModelProperty("主键")
    @TableId(value = "sink_table_id", type = IdType.AUTO)
    private Long sinkTableId;

    @ApiModelProperty("任务ID")
    @TableField("job_id")
    private Long jobId;

    @ApiModelProperty("输出源库名称")
    @TableField("sink_db_name")
    private String sinkDbName;

    @ApiModelProperty("输出源表名称")
    @TableField("sink_table_name")
    private String sinkTableName;
}
