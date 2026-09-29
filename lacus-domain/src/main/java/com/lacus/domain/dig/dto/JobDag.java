package com.lacus.domain.dig.dto;

import lombok.Data;

import java.util.List;

@Data
public class JobDag {
    private Long jobId;
    private List<Node> plugins;
    private List<Relation> relations;
    private String engineName;
    private String engineVersion;
    private String engineParam;
}
