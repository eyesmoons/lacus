package com.lacus.service.dto;


import com.lacus.service.vo.ApiParamsVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiConfigDTO {

    @ApiModelProperty("接口名称")
    private String apiName;

    @ApiModelProperty("最大超时时间")
    private Integer queryTimeout;

    @ApiModelProperty("最大限制条数")
    private Integer limitCount;

    @ApiModelProperty("是否分页，默认0（false） ，分页1（true）")
    private Integer pageFlag;

    @ApiModelProperty("sql脚本")
    private String sql;

    @ApiModelProperty("请求参数")
    private ApiParamsVO apiParams;

    @ApiModelProperty("前置SQL")
    private List<String> preSQL;
}
