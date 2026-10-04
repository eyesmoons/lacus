package com.lacus.domain.lakeintelligence.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 训练/构建进度响应 DTO
 */
@Data
public class ProgressResponse {

    @ApiModelProperty("任务ID")
    private String taskId;

    @ApiModelProperty("当前进度（epoch 或 progress）")
    private Integer progress;

    @ApiModelProperty("总量（total_epochs 或 total）")
    private Integer total;

    @ApiModelProperty("状态: training/building/completed/failed/cancelled")
    private String status;

    @ApiModelProperty("训练损失")
    private Double trainLoss;

    @ApiModelProperty("验证损失")
    private Double valLoss;

    @ApiModelProperty("消息")
    private String message;
}
