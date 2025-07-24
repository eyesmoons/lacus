package com.lacus.domain.dig.transform;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RenameField extends TransformOption {

    private String targetName;
}
