package com.lacus.dao.metadata.enums;

import com.lacus.dao.system.enums.dictionary.CssTag;
import com.lacus.dao.system.enums.interfaces.DictionaryEnum;

public enum DatasourceStatusEnum implements DictionaryEnum<Integer> {
    ENABLE(1, "启用", CssTag.PRIMARY),
    DISABLE(0, "禁用", CssTag.DANGER);

    private final int value;
    private final String description;
    private final String cssTag;

    DatasourceStatusEnum(int value, String description, String cssTag) {
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

    public static String getDictName() {
        return "datasource_status";
    }
}
