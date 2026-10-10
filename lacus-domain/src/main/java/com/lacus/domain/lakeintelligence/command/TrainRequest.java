package com.lacus.domain.lakeintelligence.command;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 启动训练请求命令
 */
@Data
public class TrainRequest {

    @ApiModelProperty(value = "任务名称", required = true)
    @NotBlank(message = "任务名称不能为空")
    private String taskName;

    @ApiModelProperty(value = "任务类型（如 IMAGE_SIMILARITY）", required = true)
    @NotBlank(message = "任务类型不能为空")
    private String taskType;

    @ApiModelProperty(value = "关联图片库ID", required = true)
    @NotNull(message = "图片库ID不能为空")
    private Long datasetId;

    @ApiModelProperty(value = "训练器类型（如 similarity）")
    private String trainerType = "similarity";

    @ApiModelProperty(value = "训练轮数")
    private Integer epochs = 10;

    @ApiModelProperty(value = "批次大小")
    private Integer batchSize = 32;

    @ApiModelProperty(value = "学习率")
    private Double learningRate = 1e-3;

    @ApiModelProperty(value = "计算设备（cpu/cuda/mps）")
    private String device = "cpu";

    @ApiModelProperty(value = "关联模型ID")
    private Long modelId;

    @ApiModelProperty(value = "创建者ID")
    private String creatorId;

    @ApiModelProperty(value = "CSV 标签文件路径")
    private String labelFilePath;
}
