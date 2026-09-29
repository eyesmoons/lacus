package com.lacus.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum DataSourceStatusEnum {

    /**
     * 未知
     */
    UNKNOWN(0),

    /**
     * 可用
     */
    AVAILABLE(1),

    /**
     * 不可用
     */
    UNAVAILABLE(2);

    @JsonValue
    @EnumValue
    private Integer value;

    DataSourceStatusEnum(Integer value) {
        this.value = value;
    }

}
