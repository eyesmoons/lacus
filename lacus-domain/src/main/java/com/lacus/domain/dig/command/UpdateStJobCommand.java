package com.lacus.domain.dig.command;

import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateStJobCommand extends AddStJobCommand {
    @NotNull(message = "任务ID不能为空")
    private Long jobId;
    private String jobScript;
    private String engineName;
    private String engineVersion;
    private String engineParam;
}
