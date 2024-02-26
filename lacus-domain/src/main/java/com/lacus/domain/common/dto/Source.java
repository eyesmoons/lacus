package com.lacus.domain.common.dto;

import lombok.Data;

import java.util.List;

/**
 * @created by shengyu on 2023/9/6 10:01
 */
@Data
public class Source {
    private String bootstrapServers;
    private String groupId;
    private List<String> topics;
}
