package com.lacus.domain.lakeintelligence.command;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 相似检索请求命令
 */
@Data
public class SearchRequest {

    @ApiModelProperty(value = "图片ID（用于以图搜图）")
    private String imageId;

    @ApiModelProperty(value = "返回结果数量", required = true)
    @NotNull(message = "TopK不能为空")
    private Integer topK = 5;

    @ApiModelProperty(value = "向量库名称")
    private String collectionName = "image_collection";

    @ApiModelProperty(value = "关联向量库ID")
    private Long vectorIndexId;
}
