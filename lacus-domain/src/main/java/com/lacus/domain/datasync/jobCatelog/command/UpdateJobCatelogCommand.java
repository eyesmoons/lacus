package com.lacus.domain.datasync.jobCatelog.command;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UpdateJobCatelogCommand extends AddJobCatelogCommand{

    @NotNull
    private Long catelogId;
}
