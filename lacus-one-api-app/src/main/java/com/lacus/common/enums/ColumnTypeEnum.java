package com.lacus.common.enums;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum ColumnTypeEnum {

    C_STRING("STRING", "字符串"),

    C_INT("INT", "数值"),

    C_STRING_ARRAY("STRING_ARRAY", "字符数组"),

    C_INT_ARRAY("INT_ARRAY", "数值数组"),

    C_DATE("DATE", "日期");


    private final String name;

    private final String desc;


    ColumnTypeEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }


    public static ColumnTypeEnum match(String type) {
        ColumnTypeEnum[] values = ColumnTypeEnum.values();
        for (ColumnTypeEnum enums : values) {
            if (Objects.equals(enums.getName(), type)) {
                return enums;
            }
        }
        return null;
    }


}
