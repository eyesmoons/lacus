package com.lacus.domain.spark.job.command;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateSparkJarJobCommand extends AddSparkJarJobCommand {
    
    @NotNull(message = "任务id不能为空")
    private Long jobId;
} 