package com.lacus.domain.spark.job.command;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddSparkJarJobCommand extends BaseSparkRunCommand {

    @NotBlank(message = "任务名称不能为空")
    private String jobName;

    @NotBlank(message = "任务类型不能为空")
    private String jobType;

    @NotBlank(message = "部署模式不能为空")
    private String deployMode;

    @NotNull(message = "主类jar包不能为空")
    private String mainJarPath;

    @NotBlank(message = "主类名不能为空")
    private String mainClassName;

    private String mainArgs;

    private String otherSparkConf;

    private Long envId;

    private String remark;
}
