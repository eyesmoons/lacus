package com.lacus.utils;

import com.lacus.common.exception.CustomException;

import java.lang.reflect.Field;

public class ReflectUtil {


    public static Object reflectPrivateFiled(String declaredFieldName, Object sourceObject) {
        try {
            Field declaredField = sourceObject.getClass().getDeclaredField(declaredFieldName);
            declaredField.setAccessible(true);
            return declaredField.get(sourceObject);
        } catch (ReflectiveOperationException re) {
            throw new CustomException(String.format("反射获取私有属性出错[%s]", re));
        }
    }


}
