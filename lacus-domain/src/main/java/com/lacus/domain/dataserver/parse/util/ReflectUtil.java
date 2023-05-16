package com.lacus.domain.dataserver.parse.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;


public class ReflectUtil {

    protected static Logger Logger = LoggerFactory.getLogger(ReflectUtil.class);

    public static Object reflectPrivateFiled(String declaredFieldName, Object sourceObject) {
        try {
            Field declaredField = sourceObject.getClass().getDeclaredField(declaredFieldName);
            declaredField.setAccessible(true);
            return declaredField.get(sourceObject);
        } catch (ReflectiveOperationException re) {
            Logger.info("反射获取私有属性出错");
            throw new RuntimeException("", re);
        }
    }

}
