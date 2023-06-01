package com.lacus.domain.datasync.jobCatalog.command;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AddJobCatalogCommand {
    @NotBlank(message = "分组名称不能为空")
    private String catalogName;

    @NotNull(message = "jobManager内存数不能为空")
    private Long jobManager;

    @NotNull(message = "taskManager内存数不能为空")
    private Long taskManager;

    private String remark;
}
