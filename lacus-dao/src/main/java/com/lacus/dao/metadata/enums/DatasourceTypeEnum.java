package com.lacus.dao.metadata.enums;

import com.lacus.dao.system.enums.dictionary.CssTag;
import com.lacus.dao.system.enums.interfaces.DictionaryEnum;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public enum DatasourceTypeEnum implements DictionaryEnum<Integer> {
    MYSQL(1,"MYSQL", "com.mysql.cj.jdbc.Driver", "mysql", "MYSQL", CssTag.NONE),
    DORIS(2,"DORIS", "com.mysql.cj.jdbc.Driver", "mysql", "DORIS", CssTag.NONE);

    private final Integer value;
    private final String name;
    private final String driverName;
    private final String protocol;
    private final String description;

    private final String cssTag;

    DatasourceTypeEnum(Integer value, String name, String driverName, String protocol, String description, String cssTag) {
        this.value = value;
        this.name = name;
        this.driverName = driverName;
        this.protocol = protocol;
        this.description = description;
        this.cssTag = cssTag;
    }

    public static String getDictName() {
        return "datasource_type";
    }

    public String getName() {
        return name;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getProtocol() {return protocol;}

    public String getDescription() {
        return description;
    }

    public String getCssTag() {
        return cssTag;
    }

    public static List<String> listAll() {
        List<String> list = new ArrayList<>();
        DatasourceTypeEnum[] values = DatasourceTypeEnum.values();
        for (DatasourceTypeEnum value : values) {
            list.add(value.getName());
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
        return description;
    }

    @Override
    public String cssTag() {
        return cssTag;
    }
}
