package com.lacus.domain.flink.job.command;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author shengyu
 * @date 2024/9/13 17:54
 */
@Data
public class AddFlinkJarJobCommand extends CommonFlinkRunCommand {

    @NotBlank(message = "任务名称不能为空")
    private String jobName;

    @NotBlank(message = "任务类型不能为空")
    private String jobType;

    @NotBlank(message = "部署模式不能为空")
    private String deployMode;

    @NotNull(message = "主类jar包不能为空")
    private Long mainJarPath;

    private String extJarPath;

    @NotBlank(message = "主类名不能为空")
    private String mainClassName;

    private String flinkRunConfig;

    private String customArgs;

    private Long envId;

    private String remark;
}
