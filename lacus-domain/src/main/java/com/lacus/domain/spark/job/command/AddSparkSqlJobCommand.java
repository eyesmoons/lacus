package com.lacus.domain.spark.job.command;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddSparkSqlJobCommand extends BaseSparkRunCommand {

    @NotEmpty(message = "任务名称不能为空")
    private String jobName;

    @NotEmpty(message = "任务类型不能为空")
    private String jobType;

    @NotEmpty(message = "部署模式不能为空")
    private String deployMode;

    @NotEmpty(message = "sql不能为空")
    private String sqlContent;

    private String otherSparkConf;

    private Long envId;

    private String remark;
}
