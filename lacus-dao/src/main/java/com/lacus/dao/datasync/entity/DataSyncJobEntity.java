package com.lacus.dao.datasync.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@TableName("data_sync_job")
public class DataSyncJobEntity extends BaseEntity<DataSyncJobEntity> {

    @ApiModelProperty("主键")
    @TableId(value = "job_id", type = IdType.ASSIGN_UUID)
    private String jobId;

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

    @ApiModelProperty("app_container")
    private String appContainer;

    @ApiModelProperty("数据缓存位置")
    @TableField("topic")
    private String topic;

    @ApiModelProperty("同步方式：1 初始快照，2 最早，3 最近，4 指定时间戳")
    @TableField("sync_type")
    private Integer syncType;

    @ApiModelProperty("任务描述")
    @TableField("remark")
    private String remark;

    @ApiModelProperty("窗口大小(秒)")
    @TableField("window_size")
    private Integer windowSize;

    @ApiModelProperty("最大数据量(MB)")
    @TableField("max_size")
    private Integer maxSize;

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
    private String sourceStatus;
    @TableField(exist = false)
    private String sinkStatus;
}
