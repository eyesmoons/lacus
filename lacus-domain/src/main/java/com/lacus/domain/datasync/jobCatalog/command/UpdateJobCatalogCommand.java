package com.lacus.domain.datasync.jobCatalog.command;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateJobCatalogCommand extends AddJobCatalogCommand {

    @NotNull
    private Long catalogId;
}
