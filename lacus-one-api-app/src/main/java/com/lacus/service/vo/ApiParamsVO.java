package com.lacus.service.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ApiParamsVO {

    @ApiModelProperty("请求参数列表")
    private List<RequestParamsVO> requestParams;

    @ApiModelProperty("返回参数列表")
    private List<ReturnParamsVO> returnParams;

}
