package com.lacus.domain.dig.transform;

import lombok.Data;

import java.util.List;

@Data
public class CopyTransformOptions implements TransformOptions {

    private List<Copy> copyList;
}
