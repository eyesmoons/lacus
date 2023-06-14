package com.lacus.domain.datasync.job.dto;

import lombok.Data;

@Data
public class JobSubmitDTO {
    private String catalogId;
    private String syncType;
    private String timeStamp;
}
