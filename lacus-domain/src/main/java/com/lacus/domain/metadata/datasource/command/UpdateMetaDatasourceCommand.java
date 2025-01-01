package com.lacus.domain.metadata.datasource.command;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
public class UpdateMetaDatasourceCommand extends AddMetaDatasourceCommand{

    @NotNull
    @PositiveOrZero
    private Long datasourceId;
}
