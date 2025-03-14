package com.lacus.domain.oneapi.command;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApiUpdateCommand extends ApiAddCommand {
    private Long apiId;
}
