package com.lacus.domain.lakeintelligence.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

    @ApiModelProperty("训练损失中的重建项")
    private Double reconLoss;

    @ApiModelProperty("训练损失中的对比项")
    private Double contrastiveLoss;

    @ApiModelProperty("消息")
    private String message;

    @ApiModelProperty("模型ID")
    private Long modelId;

    @ApiModelProperty("模型名称")
    private String modelName;

    @ApiModelProperty("模型路径")
    private String modelPath;

    @ApiModelProperty("最终损失值")
    private BigDecimal finalLoss;

    @ApiModelProperty("训练轮数")
    private Integer trainingEpochs;

    @ApiModelProperty("模型文件大小(字节)")
    private Long modelSizeBytes;

    @ApiModelProperty("损失曲线 [{epoch,trainLoss,valLoss,reconLoss,contrastiveLoss}]")
    private List<Map<String, Object>> lossHistory;
}
