package com.lacus.dao.datasync.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("data_sync_job")
public class DataSyncJobEntity extends BaseEntity<DataSyncJobEntity> {
    private static final long serialVersionUID = -7151946521794780667L;

    @ApiModelProperty("主键")
    @TableId(value = "job_id", type = IdType.AUTO)
    private Long jobId;

    @ApiModelProperty("任务名称")
    @TableField("job_name")
    private String jobName;

    @ApiModelProperty("分组ID")
    @TableField("catalog_id")
    private String catalogId;

    @ApiModelProperty("输入源ID")
    @TableField("source_datasource_id")
    private Long sourceDatasourceId;

    @ApiModelProperty("输出源ID")
    @TableField("sink_datasource_id")
    private Long sinkDatasourceId;

    @ApiModelProperty("同步方式：1 初始快照，2 最早，3 最近，4 指定时间戳")
    @TableField(exist = false)
    private String syncType;

    @ApiModelProperty("任务描述")
    @TableField("remark")
    private String remark;

    @ApiModelProperty("jobManager内存，单位为GB")
    @TableField("job_manager")
    private Integer jobManager;

    @ApiModelProperty("taskManager内存，单位为GB")
    @TableField("task_manager")
    private Integer taskManager;

    @ApiModelProperty("窗口大小(秒)")
    @TableField("window_size")
    private Integer windowSize;

    @ApiModelProperty("最大数据量(MB)")
    @TableField("max_size")
    private Long maxSize;

    @ApiModelProperty("最大数据条数(万条)")
    @TableField("max_count")
    private Integer maxCount;

    @TableField(exist = false)
    private List<String> catalogIds;
    @TableField(exist = false)
    private String sourceDatasourceName;
    @TableField(exist = false)
    private String sinkDatasourceName;
    @TableField(exist = false)
    private String status;
}
