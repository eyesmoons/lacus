package com.lacus.domain.oneapi.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ApiParseDTO {

    private String apiUrl;

    @NotBlank(message = "sql脚本不能为空")
    private String sqlScript;

}
