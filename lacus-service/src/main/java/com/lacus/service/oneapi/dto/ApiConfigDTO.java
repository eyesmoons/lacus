package com.lacus.service.oneapi.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiConfigDTO {


    @ApiModelProperty("api接口地址")
    private String apiUrl;

    @ApiModelProperty("是否分页，默认0（false） ，分页1（true）")
    private Integer pageFlag;

    @ApiModelProperty("sql脚本")
    private String sql;

    @ApiModelProperty("请求参数")
    private ApiParamsDTO apiParams;
}
