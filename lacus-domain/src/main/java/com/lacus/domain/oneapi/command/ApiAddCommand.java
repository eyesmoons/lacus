package com.lacus.domain.oneapi.command;

import lombok.Data;

@Data
public class ApiAddCommand {
    private String apiName;
    private String apiUrl;
    private String apiType;
    private Long datasourceId;
    private Integer queryTimeout;
    private String reqMethod;
    private Integer limitCount;
    private String apiDesc;
    private String apiConfig;

    private String apiResponse;
}
