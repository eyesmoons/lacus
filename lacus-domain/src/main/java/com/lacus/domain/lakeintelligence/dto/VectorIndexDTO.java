package com.lacus.domain.lakeintelligence.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 向量库索引 DTO
 */
@Data
public class VectorIndexDTO {

    @ApiModelProperty("向量库ID")
    private Long indexId;

    @ApiModelProperty("向量库名称")
    private String indexName;

    @ApiModelProperty("关联图片库ID")
    private Long datasetId;

    @ApiModelProperty("关联模型ID")
    private Long modelId;

    @ApiModelProperty("向量索引本地路径")
    private String indexPath;

    @ApiModelProperty("向量总数")
    private Integer totalVectors;

    @ApiModelProperty("向量维度")
    private Integer dimension;

    @ApiModelProperty("距离度量: cosine/euclidean")
    private String distanceMetric;

    @ApiModelProperty("构建状态: PENDING/BUILDING/COMPLETED/FAILED")
    private String buildStatus;

    @ApiModelProperty("错误信息")
    private String errorMessage;

    @ApiModelProperty("创建者ID")
    private String creatorId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}
