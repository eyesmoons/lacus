package com.lacus.domain.dig.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobTaskInfo {
    private List<Relation> edges;
    private List<StTaskConfig> plugins;
}
