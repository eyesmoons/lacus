package com.lacus.domain.system.env.command;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class EnvAddCommand {

    @NotBlank(message = "环境名称不能为空")
    protected String name;

    @NotBlank(message = "环境内容不能为空")
    protected String config;

    protected String remark;
}
