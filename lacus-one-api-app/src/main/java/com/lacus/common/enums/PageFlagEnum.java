package com.lacus.common.enums;

import lombok.Getter;

@Getter
public enum PageFlagEnum {

    FLAG_TRUE("分页", 1),
    FLAG_FALSE("不分页", 0);



    private final String desc;

    private final Integer code;


    PageFlagEnum(String desc, Integer code) {
        this.desc = desc;
        this.code = code;
    }
}
