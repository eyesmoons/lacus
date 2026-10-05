package com.lacus.domain.lakeintelligence.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 模型信息 DTO
 */
@Data
public class ModelInfoDTO {

    @ApiModelProperty("模型ID")
    private Long modelId;

    @ApiModelProperty("模型名称")
    private String modelName;

    @ApiModelProperty("模型描述")
    private String description;

    @ApiModelProperty("关联训练任务ID")
    private Long taskId;

    @ApiModelProperty("关联图片库ID")
    private Long datasetId;

    @ApiModelProperty("模型架构")
    private String modelArch;

    @ApiModelProperty("模型文件本地路径")
    private String modelPath;

    @ApiModelProperty("模型文件大小(字节)")
    private Long modelSizeBytes;

    @ApiModelProperty("Embedding维度")
    private Integer embeddingDim;

    @ApiModelProperty("实际训练轮数")
    private Integer trainingEpochs;

    @ApiModelProperty("最终损失值")
    private BigDecimal finalLoss;

    @ApiModelProperty("模型状态: PENDING/TRAINING/TRAINING_COMPLETED/TRAINING_FAILED")
    private String status;

    @ApiModelProperty("关联向量库ID")
    private Long vectorIndexId;

    @ApiModelProperty("创建者ID")
    private String creatorId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}
