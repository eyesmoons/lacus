package com.lacus.common.enums;

import lombok.Getter;

@Getter
public enum ApiTypeEnum {

    API("core", "core"),
    WORKFLOW("workflow", "api工作流");


    private String name;

    private String desc;


    ApiTypeEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }


}
