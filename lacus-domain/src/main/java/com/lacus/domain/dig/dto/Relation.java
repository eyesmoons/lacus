package com.lacus.domain.dig.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Relation {
    private Long sourceTaskId;
    private Long sinkTaskId;
}
