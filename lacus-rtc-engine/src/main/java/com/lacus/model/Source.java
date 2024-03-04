package com.lacus.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @created by shengyu on 2023/9/6 10:01
 */
@Data
public class Source implements Serializable {
    private static final long serialVersionUID = 4899153037120065886L;
    private String bootstrapServers;
    private String groupId;
    private List<String> topics;
}
