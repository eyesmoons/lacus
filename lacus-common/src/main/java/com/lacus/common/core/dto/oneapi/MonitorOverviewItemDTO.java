package com.lacus.common.core.dto.oneapi;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MonitorOverviewItemDTO {

    @ApiModelProperty("api地址")
    private String apiUrl;

    @ApiModelProperty("接口名称")
    private String apiName;

    @ApiModelProperty("数据源名称")
    private String datasourceName;

    @ApiModelProperty("调用次数")
    private Long callCount;

    @ApiModelProperty("成功数")
    private Long successCount;

    @ApiModelProperty("失败数")
    private Long failCount;

    @ApiModelProperty("错误率")
    private Double errorRate;

    @ApiModelProperty("平均耗时(ms)")
    private Double avgCost;

    @ApiModelProperty("P95耗时(ms)")
    private Long p95Cost;
}
