package com.lacus.domain.datasync.job.model;

import lombok.Data;

import java.util.List;

@Data
public class FlinkJobSource {
    private String bootstrapServers;
    private String groupId;
    private List<String> topics;
}
