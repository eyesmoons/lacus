package com.lacus.domain.dig.dto;

import lombok.Data;

import java.util.List;

@Data
public class JobDag {
    private Long jobId;
    private List<Relation> relations;
}
