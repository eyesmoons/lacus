package com.lacus.dataquality.enums;

/**
 * Writer 类型枚举
 */
public enum WriterType {
    JDBC,
    HDFS_FILE,
    LOCAL_FILE,
    /** 数据质量检测结果专用写入器，将结果写回 dq_check_result 表 */
    DQ_RESULT;

    public static WriterType getType(String name) {
        if (name == null) {
            return null;
        }
        for (WriterType type : values()) {
            if (type.name().equalsIgnoreCase(name.trim())) {
                return type;
            }
        }
        return null;
    }
}

