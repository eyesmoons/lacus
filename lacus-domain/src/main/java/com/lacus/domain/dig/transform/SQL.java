package com.lacus.domain.dig.transform;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SQL extends TransformOption {

    private String query;
}
