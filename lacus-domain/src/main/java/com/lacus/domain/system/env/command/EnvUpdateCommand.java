package com.lacus.domain.system.env.command;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * @author shengyu
 * @date 2024/4/30 17:51
 */
@Data
public class EnvUpdateCommand extends EnvAddCommand {

    @NotNull
    @Positive
    protected Long envId;
}