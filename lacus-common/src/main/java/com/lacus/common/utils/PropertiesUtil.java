package com.lacus.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public class PropertiesUtil {

    private static final String DEFAULT_PROPERTIES = "/application.properties";

    /**
     * 获取properties属性值
     */
    public static String getPropValue(String propKey) {
        try {
            Properties props = new Properties();
            InputStream inputStream = PropertiesUtil.class.getResourceAsStream(DEFAULT_PROPERTIES);
            if (Objects.isNull(inputStream)) {
                throw new RuntimeException("输入流获取失败");
            }
            BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            props.load(bf);
            return props.getProperty(propKey);
        } catch (IOException e) {
            log.error("获取propKey错误:", e);
        }
        return null;
    }

    /**
     * 根据正则获取properties属性值集合
     */
    public static Properties getProps(String regex) {
        Properties propsResult = new Properties();
        try {
            Properties props = new Properties();
            InputStream inputStream = PropertiesUtil.class.getResourceAsStream(DEFAULT_PROPERTIES);
            if (Objects.isNull(inputStream)) {
                throw new RuntimeException("输入流获取失败");
            }
            BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            props.load(bf);
            for (Map.Entry<Object, Object> entry : props.entrySet()) {
                String key = entry.getKey().toString();
                if (key.matches(regex)) {
                    propsResult.put(key, entry.getValue());
                }
            }
        } catch (IOException e) {
            log.error("获取propKey错误:", e);
        }
        return propsResult;
    }

    /**
     * 根据前缀获取properties属性值集合，并且key去掉前缀
     */
    public static Properties getPropsWithoutPrefix(String prefix) {
        Properties propsResult = new Properties();
        try {
            Properties props = new Properties();
            InputStream inputStream = PropertiesUtil.class.getResourceAsStream(DEFAULT_PROPERTIES);
            BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            props.load(bf);
            for (Map.Entry<Object, Object> entry : props.entrySet()) {
                String key = entry.getKey().toString();
                if (key.startsWith(prefix)) {
                    propsResult.put(key.substring(prefix.length()), entry.getValue());
                }
            }
        } catch (IOException e) {
            log.error("获取propKey错误:", e);
        }
        return propsResult;
    }

    public static void main(String[] args) {
        System.out.println(getPropValue("kafka.properties.bootstrap-servers"));
        System.out.println(getProps("kafka.properties.*"));
        System.out.println(getPropsWithoutPrefix("kafka.properties."));
    }
}
