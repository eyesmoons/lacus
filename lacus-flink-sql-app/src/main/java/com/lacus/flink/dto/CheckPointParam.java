package com.lacus.flink.dto;

import com.lacus.flink.enums.StateBackendEnum;
import lombok.Data;

@Data
public class CheckPointParam {
    private long checkpointInterval = 1000 * 60L;
    private String checkpointingMode = "EXACTLY_ONCE";
    private long checkpointTimeout = 10 * 60 * 1000;
    private String checkpointDir;
    private int tolerableCheckpointFailureNumber = 1;
    private String externalizedCheckpointCleanup;
    private StateBackendEnum stateBackendEnum;
    private Boolean enableIncremental;
}
