package com.lacus.common.enums;

public enum ErrorMsgEnum {
    DORIS_ERROR("写入Doris失败", 40001),
    CLEAN_ERROR("数据清洗失败", 40002);

    private final String desc;
    private final Integer status;

    ErrorMsgEnum(String desc, Integer status) {
        this.desc = desc;
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getStatus() {
        return status;
    }
}
