package com.lacus.dao.dataserver.enums;

import com.lacus.dao.system.enums.interfaces.DictionaryEnum;

/**
 * Created by:
 *
 * @Author: lit
 * @Date: 2023/04/27/10:31
 * @Description:
 */
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

    @Override
    public String description() {
        return description;
    }

    @Override
    public String cssTag() {
        return null;
    }
}
