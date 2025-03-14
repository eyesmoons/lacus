package com.lacus.domain.rtc.job.dto;

import lombok.Data;

@Data
public class JobSubmitDTO {
    private Long jobId;
    private String syncType;
    private String timeStamp;
}
