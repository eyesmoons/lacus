package com.lacus.common.utils.yarn;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class FlinkJobDetail {

    private String jid;

    private String name;

    private boolean isStoppable;

    private String state;

    @JSONField(name = "start-time")
    private Long startTime;

    @JSONField(name = "end-time")
    private Long endTime;

    private Long duration;

    private Long now;

    private Object timestamps;

    private Object vertices;

    @JSONField(name = "status-counts")
    private Object statusCounts;

    private Object plan;
}
