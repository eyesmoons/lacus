package com.lacus.domain.dig.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Relation {
    private String sourceTaskId;
    private String sinkTaskId;
}
