package com.lacus.domain.oneapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiTestResp {

    private Object data;

    private String debugInfo;

    private Long costTime;

    private Long code;

}
