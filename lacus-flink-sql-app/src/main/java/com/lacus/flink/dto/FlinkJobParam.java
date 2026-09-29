package com.lacus.flink.dto;

import com.lacus.flink.enums.FlinkJobTypeEnum;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FlinkJobParam {
    private String sqlPath;
    private FlinkJobTypeEnum jobTypeEnum;
    private CheckPointParam checkPointParam;
}
