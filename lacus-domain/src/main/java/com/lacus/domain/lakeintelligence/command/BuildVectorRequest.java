package com.lacus.domain.lakeintelligence.command;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 构建向量库请求命令
 */
@Data
public class BuildVectorRequest {

    @ApiModelProperty(value = "向量库名称", required = true)
    @NotBlank(message = "向量库名称不能为空")
    private String indexName;

    @ApiModelProperty(value = "关联图片库ID", required = true)
    @NotNull(message = "图片库ID不能为空")
    private Long datasetId;

    @ApiModelProperty(value = "训练任务ID（选择某次训练产出的模型）", required = true)
    @NotNull(message = "训练任务不能为空")
    private Long taskId;

    @ApiModelProperty(value = "批次大小")
    private Integer batchSize = 32;

    @ApiModelProperty(value = "距离度量: cosine/euclidean")
    private String distanceMetric = "cosine";

    @ApiModelProperty(value = "创建者ID", required = true)
    @NotBlank(message = "创建者ID不能为空")
    private String creatorId;
}
