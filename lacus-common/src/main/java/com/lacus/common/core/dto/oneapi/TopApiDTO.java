package com.lacus.common.core.dto.oneapi;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TopApiDTO {

    @ApiModelProperty("api地址")
    private String apiUrl;

    @ApiModelProperty("接口名称")
    private String apiName;

    @ApiModelProperty("调用次数")
    private Long callCount;

    @ApiModelProperty("失败数")
    private Long failCount;

    @ApiModelProperty("错误率")
    private Double errorRate;
}
