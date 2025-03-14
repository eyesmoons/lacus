package com.lacus.service.oneapi.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ApiParamsDTO {


    @ApiModelProperty("请求参数列表")
    private List<RequestParamsDTO> requestParams;

    @ApiModelProperty("返回参数列表")
    private List<ReturnParamsDTO> returnParams;

}
