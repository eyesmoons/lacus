package com.lacus.core.mybatisplus;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import java.util.List;

/**
 * 由于H2不支持大部分Mysql的函数  所以要自己实现
 * 在H2的初始化 h2sql/lacus_schema.sql加上这句
 * CREATE ALIAS FIND_IN_SET FOR "com.lacus.infrastructure.mybatisplus.MySqlFunction.find_in_set";
 */
public class MySqlFunction {

    public static boolean findInSet(String target, String setString) {
        if (setString == null) {
            return false;
        }

        List<String> split = StrUtil.split(setString, ",");

        return CollUtil.contains(split, target);
    }

}
