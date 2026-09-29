package com.lacus.domain.system.resources.command;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * @author shengyu
 */
@Data
public class ResourceUpdateCommand extends ResourceAddCommand {

    @NotNull
    @Positive
    private Integer id;
}