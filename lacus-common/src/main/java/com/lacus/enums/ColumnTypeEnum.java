package com.lacus.enums;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

@Getter
public enum ColumnTypeEnum {

    C_STRING("STRING", "字符串"),

    C_INT("INT", "数值"),

    C_STRING_ARRAY("STRING_ARRAY", "字符数组"),

    C_INT_ARRAY("INT_ARRAY", "数值数组"),

    C_DATE("DATE", "日期");


    private final String name;

    private final String desc;


    ColumnTypeEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }


    public static List<JSONObject> getList() {
        List<JSONObject> res = Lists.newArrayList();
        for (ColumnTypeEnum enums : ColumnTypeEnum.values()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("desc", enums.getDesc());
            jsonObject.put("name", enums.getName());
            res.add(jsonObject);
        }
        return res;
    }


}
