package com.lacus.dataquality.enums;

/**
 * 规则类型枚举
 */
public enum RuleType {
    /**
     * 单表规则（空值/唯一性/数值范围等）
     */
    SINGLE_TABLE,
    /**
     * 单表自定义 SQL 规则
     */
    SINGLE_TABLE_CUSTOM_SQL,
    /**
     * 多表一致性/准确性规则
     */
    MULTI_TABLE_ACCURACY,
    /**
     * 多表对比规则
     */
    MULTI_TABLE_COMPARISON;

    public static RuleType of(String name) {
        if (name == null) {
            return null;
        }
        for (RuleType type : values()) {
            if (type.name().equalsIgnoreCase(name.trim())) {
                return type;
            }
        }
        return null;
    }
}
