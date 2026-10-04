package com.lacus.enums;

/**
 * 图片库状态枚举
 */
public enum DatasetStatus {

    PROCESSING("PROCESSING", "处理中"),
    WAITING_DOWNLOAD("WAITING_DOWNLOAD", "等待下载"),
    DOWNLOADING("DOWNLOADING", "下载中"),
    READY("READY", "就绪"),
    ERROR("ERROR", "错误");

    private final String code;
    private final String desc;

    DatasetStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static DatasetStatus fromCode(String code) {
        for (DatasetStatus status : values()) {
            if (status.getCode().equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown DatasetStatus code: " + code);
    }
}
