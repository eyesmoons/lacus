package com.lacus.domain.lakeintelligence.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 分类推理响应 DTO
 */
@Data
public class ClassifyResponse {

    @ApiModelProperty("预测类别名称")
    private String className;

    @ApiModelProperty("置信度 (0-1)")
    private Double confidence;

    @ApiModelProperty("类别索引")
    private Integer classId;
}
