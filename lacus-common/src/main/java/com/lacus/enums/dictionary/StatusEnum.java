package com.lacus.enums.dictionary;

import com.lacus.enums.interfaces.DictionaryEnum;

/**
 * 除非表有特殊指明的话，一般用这个枚举代表 status字段
 */
public enum StatusEnum implements DictionaryEnum<Integer> {
    /**
     * 开关状态
     */
    ENABLE(1, "正常", CssTag.PRIMARY),
    DISABLE(0, "停用", CssTag.DANGER);

    private final int value;
    private final String description;
    private final String cssTag;

    StatusEnum(int value, String description, String cssTag) {
        this.value = value;
        this.description = description;
        this.cssTag = cssTag;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public String cssTag() {
        return cssTag;
    }

    public static StatusEnum getDefault() {
        return ENABLE;
    }

    public static String getDictName() {
        return "sys_status";
    }
}
