package com.lacus.common.core.dto.oneapi;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StatsSummaryDTO {

    @ApiModelProperty("调用总量")
    private Long totalCount;

    @ApiModelProperty("成功数")
    private Long successCount;

    @ApiModelProperty("失败数")
    private Long failCount;

    @ApiModelProperty("平均耗时(ms)")
    private Double avgCost;

    @ApiModelProperty("Top接口")
    private java.util.List<TopApiDTO> topApis;
}
