package com.lacus.domain.dig.command;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AddStJobCommand {
    @NotBlank(message = "任务名称不能为空")
    private String jobName;

    private String description;
}
