package com.lacus.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.util.Properties;

@Slf4j
public class PropertiesUtil {

    public static Properties loadPropertiesByStr(String str) {
        Properties props = new Properties();
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(str.getBytes());
            props.load(inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return props;
    }
}
