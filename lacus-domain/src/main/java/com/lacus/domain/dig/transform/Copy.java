package com.lacus.domain.dig.transform;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Copy extends TransformOption {

    private String targetFieldName;
}
