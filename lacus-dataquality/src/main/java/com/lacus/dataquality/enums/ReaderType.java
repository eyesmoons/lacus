package com.lacus.dataquality.enums;

/**
 * Reader 类型枚举
 */
public enum ReaderType {
    JDBC,
    HIVE;

    public static ReaderType getType(String name) {
        if (name == null) {
            return null;
        }
        for (ReaderType type : values()) {
            if (type.name().equalsIgnoreCase(name.trim())) {
                return type;
            }
        }
        return null;
    }
}
