package com.lacus.dao.dataserver.enums;

import com.lacus.dao.system.enums.interfaces.DictionaryEnum;

public enum DriverTypeEnum implements DictionaryEnum<Integer> {

    MYSQL(0, "MYSQL"),
    DORIS(1, "DORIS"),
    SQLSERVER(2, "SQLSERVER"),
    CLICKHOUSE(3, "CLICKHOUSE"),
    ORACLE(4, "ORACLE");


    private Integer value;

    private String description;


    DriverTypeEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }


    @Override
    public Object getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public String cssTag() {
        return null;
    }
}
