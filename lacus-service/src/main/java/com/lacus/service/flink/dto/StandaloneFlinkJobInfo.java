package com.lacus.service.flink.dto;

import lombok.Data;

@Data
public class StandaloneFlinkJobInfo {

    private String jid;

    private String state;

    private String errors;

}
