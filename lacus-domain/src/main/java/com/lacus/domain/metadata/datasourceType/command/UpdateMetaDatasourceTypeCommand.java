package com.lacus.domain.metadata.datasourceType.command;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
public class UpdateMetaDatasourceTypeCommand extends AddMetaDatasourceTypeCommand {

    @NotNull
    @PositiveOrZero
    private Long typeId;
}
