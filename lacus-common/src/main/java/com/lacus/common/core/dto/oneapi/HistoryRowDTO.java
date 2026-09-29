package com.lacus.common.core.dto.oneapi;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class HistoryRowDTO {

    @ApiModelProperty("调用记录ID")
    private Long callId;

    @ApiModelProperty("接口名称")
    private String apiName;

    @ApiModelProperty("请求方式")
    private String reqMethod;

    @ApiModelProperty("响应状态：success/fail")
    private String status;

    @ApiModelProperty("耗时(ms)")
    private Long costTime;

    @ApiModelProperty("调用方IP")
    private String caller;

    @ApiModelProperty("调用时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date callTime;

    @ApiModelProperty("入参摘要（暂无源字段，置 null）")
    private String paramSummary;
}
