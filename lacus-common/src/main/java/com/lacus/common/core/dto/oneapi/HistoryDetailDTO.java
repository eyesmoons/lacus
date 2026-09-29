package com.lacus.common.core.dto.oneapi;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class HistoryDetailDTO {

    @ApiModelProperty("接口名称")
    private String apiName;

    @ApiModelProperty("响应状态：success/fail")
    private String status;

    @ApiModelProperty("耗时(ms)")
    private Long costTime;

    @ApiModelProperty("调用方IP")
    private String caller;

    @ApiModelProperty("入参（暂无源字段，置 null）")
    private String requestBody;

    @ApiModelProperty("响应摘要（暂无源字段，置 null）")
    private String responseSummary;

    @ApiModelProperty("错误信息")
    private String errorMessage;
}
