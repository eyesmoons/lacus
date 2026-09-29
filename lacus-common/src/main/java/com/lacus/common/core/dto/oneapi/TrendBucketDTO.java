package com.lacus.common.core.dto.oneapi;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TrendBucketDTO {

    @ApiModelProperty("时间桶标签")
    private String time;

    @ApiModelProperty("调用次数")
    private Long callCount;

    @ApiModelProperty("平均耗时(ms)")
    private Double avgCost;

    @ApiModelProperty("失败数")
    private Long failCount;
}
