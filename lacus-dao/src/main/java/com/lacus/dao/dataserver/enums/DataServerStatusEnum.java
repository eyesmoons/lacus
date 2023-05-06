package com.lacus.dao.dataserver.enums;

import com.lacus.dao.system.enums.dictionary.CssTag;
import com.lacus.dao.system.enums.interfaces.DictionaryEnum;

/**
 * Created by:
 *
 * @Author: lit
 * @Date: 2023/04/27/10:20
 * @Description:
 */
public enum DataServerStatusEnum implements DictionaryEnum<Integer> {

    ENABLE(1, "启用", CssTag.PRIMARY),
    DISABLE(0, "禁用", CssTag.DANGER);


    private final int value;

    private final String description;

    private final String cssTag;

    DataServerStatusEnum(int value, String description, String cssTag) {
        this.value = value;
        this.description = description;
        this.cssTag = cssTag;
    }


    public static String getDictName() {
        return "dataservice_status";
    }


    @Override
    public Object getValue() {
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


}
