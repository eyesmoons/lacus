package com.lacus.domain.dig.form.validate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.apache.seatunnel.shade.com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@EqualsAndHashCode(callSuper = true)
@Data
public class UnionNonEmptyValidate extends AbstractValidate {
    private final boolean required = false;
    private List<String> fields;

    @JsonProperty("type")
    private final RequiredType requiredType = RequiredType.UNION_NON_EMPTY;

    public UnionNonEmptyValidate(@NonNull List<String> fields) {
        checkArgument(!fields.isEmpty());
        this.fields = fields;
        this.withMessage(
                "parameters:"
                        + fields
                        + " if any of these parameters is set, other parameters must also be set");
    }
}
