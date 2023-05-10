package com.lacus.dao.datasync.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("data_sync_job")
public class DataSyncJobEntity extends BaseEntity<DataSyncJobEntity> {

    @ApiModelProperty("主键")
    @TableId(value = "job_id", type = IdType.AUTO)
    private Long jobId;

    @ApiModelProperty("任务名称")
    @TableField("job_name")
    private String jobName;

    @ApiModelProperty("分组ID")
    @TableField("catelog_id")
    private Long catelogId;

    @ApiModelProperty("输入源配置ID")
    @TableField("source_conf_id")
    private Long sourceConfId;

    @ApiModelProperty("输出源配置ID")
    @TableField("sink_conf_id")
    private Long sinkConfId;

    @ApiModelProperty("数据缓存位置")
    @TableField("topic")
    private String topic;

    @ApiModelProperty("任务名称")
    @TableField("sync_type")
    private Integer syncType;
}
