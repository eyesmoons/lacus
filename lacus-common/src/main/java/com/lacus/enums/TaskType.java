package com.lacus.enums;

/**
 * 任务类型枚举
 */
public enum TaskType {

    SIMILARITY("SIMILARITY", "图像相似度"),
    CLASSIFICATION("CLASSIFICATION", "图像分类");

    private final String code;
    private final String desc;

    TaskType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static TaskType fromCode(String code) {
        for (TaskType type : values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown TaskType code: " + code);
    }
}
