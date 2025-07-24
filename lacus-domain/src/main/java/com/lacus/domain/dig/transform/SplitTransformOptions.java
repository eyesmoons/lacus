package com.lacus.domain.dig.transform;

import lombok.Data;

import java.util.List;

@Data
public class SplitTransformOptions implements TransformOptions {

    List<Split> splits;
}
