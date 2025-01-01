package com.lacus.domain.dataServer.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ScriptDTO {


    @ApiModelProperty("脚本sql")
    private String apiSQL;

    @ApiModelProperty("脚本请求参数")
    private List<ParseParamsDTO.RequestParams> requestParams;

    @ApiModelProperty("脚本返回参数")
    private List<ParseParamsDTO.ReturnParams> returnParams;

}
