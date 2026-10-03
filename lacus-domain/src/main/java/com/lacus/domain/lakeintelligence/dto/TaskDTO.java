package com.lacus.domain.lakeintelligence.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 训练任务 DTO
 */
@Data
public class TaskDTO {

    @ApiModelProperty("任务ID")
    private Long taskId;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("任务类型: IMAGE_SIMILARITY")
    private String taskType;

    @ApiModelProperty("关联图片库ID")
    private Long datasetId;

    @ApiModelProperty("关联模型ID")
    private Long modelId;

    @ApiModelProperty("状态: PENDING/TRAINING/COMPLETED/FAILED/CANCELLED")
    private String status;

    @ApiModelProperty("超参数配置 (JSON)")
    private String hyperParams;

    @ApiModelProperty("训练进度百分比")
    private Integer trainingProgress;

    @ApiModelProperty("损失曲线数据 (JSON数组)")
    private String lossHistory;

    @ApiModelProperty("错误信息")
    private String errorMessage;

    @ApiModelProperty("开始时间")
    private Date startedAt;

    @ApiModelProperty("完成时间")
    private Date completedAt;

    @ApiModelProperty("创建者ID")
    private String creatorId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}
