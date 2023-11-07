package com.lacus.domain.datasync.job.command;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateJobCommand extends AddJobCommand{
    @NotNull(message = "jobId不能为空")
    private Long jobId;
}
