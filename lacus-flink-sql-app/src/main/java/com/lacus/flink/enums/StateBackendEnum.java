package com.lacus.flink.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum StateBackendEnum {
    MEMORY("0"), FILE("1"), ROCKSDB("2");

    private final String type;

    StateBackendEnum(String type) {
        this.type = type;
    }

    public static StateBackendEnum getStateBackend(String stateBackendType) {
        if (StringUtils.isEmpty(stateBackendType)) {
            return FILE;
        }

        for (StateBackendEnum stateBackendEnum : StateBackendEnum.values()) {
            if (stateBackendEnum.getType().equalsIgnoreCase(stateBackendType.trim())) {
                return stateBackendEnum;
            }
        }
        throw new RuntimeException("不支持的StateBackend: " + stateBackendType);
    }
}
