package com.lacus.dao.rtc.entity;

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
@TableName("data_sync_sink_column")
public class DataSyncSinkColumnEntity extends BaseEntity<DataSyncSinkColumnEntity> {

    @ApiModelProperty("主键")
    @TableId(value = "sink_column_id", type = IdType.AUTO)
    private Long sinkColumnId;

    @ApiModelProperty("任务ID")
    @TableField("job_id")
    private Long jobId;

    @ApiModelProperty("输出源表ID")
    @TableField("sink_table_id")
    private Long sinkTableId;

    @ApiModelProperty("输出源字段名称")
    @TableField("sink_column_name")
    private String sinkColumnName;
}
