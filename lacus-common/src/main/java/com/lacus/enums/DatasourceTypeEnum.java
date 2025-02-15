package com.lacus.enums;

import com.lacus.enums.dictionary.CssTag;
import com.lacus.enums.interfaces.DictionaryEnum;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public enum DatasourceTypeEnum implements DictionaryEnum<Integer> {
    RELATIONAL(1, "RELATIONAL", "关系型数据库", CssTag.NONE),
    NON_RELATIONAL(2, "NON_RELATIONAL", "NoSql数据库", CssTag.NONE),
    OLAP(3, "OLAP", "OLAP数据库", CssTag.NONE),
    ;

    private final Integer value;
    private final String name;
    private final String description;
    private final String cssTag;

    DatasourceTypeEnum(Integer value, String name, String description, String cssTag) {
        this.value = value;
        this.name = name;
        this.description = description;
        this.cssTag = cssTag;
    }

    public static String getDictName() {
        return "datasource_type";
    }

    public static List<Map<String, Object>> listAll() {
        List<Map<String, Object>> list = new ArrayList<>();
        DatasourceTypeEnum[] values = DatasourceTypeEnum.values();
        for (DatasourceTypeEnum value : values) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", value.getValue());
            item.put("remark", value.getName());
            list.add(item);
        }
        return list;
    }

    public static DatasourceTypeEnum getByType(String type) {
        if (ObjectUtils.isEmpty(type)) {
            return null;
        }
        DatasourceTypeEnum[] values = DatasourceTypeEnum.values();
        for (DatasourceTypeEnum value : values) {
            if (value.getName().equalsIgnoreCase(type)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String description() {
        return "";
    }

    @Override
    public String cssTag() {
        return cssTag;
    }
}
