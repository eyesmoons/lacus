package com.lacus.domain.lakeintelligence.command;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建模型请求命令
 */
@Data
public class CreateModelRequest {
    @ApiModelProperty(value = "模型名称", required = true)
    @NotBlank(message = "模型名称不能为空")
    private String modelName;

    @ApiModelProperty("模型描述")
    private String description;

    @ApiModelProperty(value = "关联数据集 ID", required = true)
    @NotNull(message = "数据集不能为空")
    private Long datasetId;

    @ApiModelProperty(value = "任务类型: SIMILARITY/CLASSIFICATION")
    private String taskType = "SIMILARITY";
}
