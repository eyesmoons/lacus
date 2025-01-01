package com.lacus.domain.dataServer.command;

import com.lacus.domain.dataServer.dto.ScriptDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
public class AddDataServerCommand {

    @NotBlank(message = "数据服务接口名称不能为空")
    @ApiModelProperty("接口名称")
    private String apiName;

    @NotBlank(message = "数据服务接口驱动类型不能为空")
    @ApiModelProperty("数据服务驱动类型:Mysql,Doris,oracle...")
    private String driverType;

    @NotBlank(message = "数据服务接口地址不能为空")
    @ApiModelProperty("接口Url")
    private String apiUrl;

    @ApiModelProperty("接口描述")
    private String apiDesc;

    @NotBlank(message = "数据服务接口请求方式不能为空")
    @ApiModelProperty("接口请求方式")
    private String requestMethod;

    @NotBlank(message = "数据服务接口所属项目组不能为空")
    @ApiModelProperty("接口所属项目组")
    private String projectTeam;

    @ApiModelProperty("接口超时时间")
    private Integer queryTimeout;

    @ApiModelProperty("是否开启限流，默认不开启0，开启1")
    private Integer currentLimit;

    @ApiModelProperty("接口最大返回行数")
    private Long maxReturnRows;

    @NotNull(message = "数据服务接口数据源不能为空")
    @ApiModelProperty("数据源id")
    private Long datasourceId;

    @NotNull(message = "数据服务接口内容不能为空")
    @ApiModelProperty("apiScript")
    private ScriptDTO apiScript;

    @ApiModelProperty("api状态")
    private Integer apiStatus;


}
