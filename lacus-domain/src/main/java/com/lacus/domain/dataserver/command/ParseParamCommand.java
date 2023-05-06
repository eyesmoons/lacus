package com.lacus.domain.dataserver.command;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Created by:
 *
 * @Author: lit
 * @Date: 2023/04/28/17:07
 * @Description:
 */
@Data
public class ParseParamCommand {


    @ApiModelProperty("接口驱动类型")
    @NotBlank(message = "接口驱动类型不能为空")
    private String driverType;

    @NotBlank(message = "接口脚本不能为空")
    private String apiScript;

}
