package com.lacus.domain.lakeintelligence.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 相似检索响应 DTO
 */
@Data
public class SearchResponse {

    @ApiModelProperty("搜索结果列表")
    private List<SearchResultItem> results;

    /**
     * 搜索结果项
     */
    @Data
    public static class SearchResultItem {

        @ApiModelProperty("图片ID")
        private String id;

        @ApiModelProperty("距离（越小越相似）")
        private Double distance;
    }
}
