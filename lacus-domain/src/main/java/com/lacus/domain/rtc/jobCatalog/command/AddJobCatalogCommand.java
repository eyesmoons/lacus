package com.lacus.domain.rtc.jobCatalog.command;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AddJobCatalogCommand {
    @NotBlank(message = "分组名称不能为空")
    private String catalogName;

    private String remark;
}
