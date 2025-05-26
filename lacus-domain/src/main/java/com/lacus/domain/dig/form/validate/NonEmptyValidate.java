package com.lacus.domain.dig.form.validate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.seatunnel.shade.com.fasterxml.jackson.annotation.JsonProperty;

@EqualsAndHashCode(callSuper = true)
@Data
public class NonEmptyValidate extends AbstractValidate {
    private final boolean required = true;

    @JsonProperty("type")
    private final RequiredType requiredType = RequiredType.NON_EMPTY;
}
