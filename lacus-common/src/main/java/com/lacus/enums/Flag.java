package com.lacus.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum Flag {
    NO(0, "no"),
    YES(1, "yes");

    @EnumValue
    private final int code;
    private final String descp;

    private Flag(int code, String descp) {
        this.code = code;
        this.descp = descp;
    }

    public int getCode() {
        return this.code;
    }

    public String getDescp() {
        return this.descp;
    }
}
