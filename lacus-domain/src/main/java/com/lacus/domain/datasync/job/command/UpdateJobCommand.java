package com.lacus.domain.datasync.job.command;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateJobCommand extends AddJobCommand{

    @NotNull(message = "jobId不能为空")
    private Long jobId;
}
