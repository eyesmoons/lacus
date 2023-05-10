package com.lacus.domain.datasync.jobCatelog.command;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AddJobCatelogCommand {
    @NotBlank(message = "分组名称不能为空")
    private String catelogName;

    private String remark;
}
