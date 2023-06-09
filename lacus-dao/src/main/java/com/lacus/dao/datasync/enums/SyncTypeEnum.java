package com.lacus.dao.datasync.enums;

import java.util.Objects;

public enum SyncTypeEnum {
    INITIAL(1, "initial"),
    LATEST_OFFSET(2, "latest-offset"),
    TIMESTAMP(3, "timestamp"),
    SPECIFIC_OFFSET(4, "specific-offset");

    private final Integer code;
    private final String name;

    SyncTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static String getByCode(Integer code) {
        SyncTypeEnum[] values = SyncTypeEnum.values();
        for (SyncTypeEnum value : values) {
            if (Objects.equals(code, value.getCode())) {
                return value.getName();
            }
        }
        return null;
    }
}
