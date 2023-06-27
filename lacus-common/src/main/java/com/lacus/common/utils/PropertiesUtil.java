package com.lacus.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public class PropertiesUtil {

    private static final String DEFAULT_PROPERTIES = "flink-common.yml";

    /**
     * 获取properties属性值
     */
    public static String getPropValue(String propKey) {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES);
            Map<String, Object> obj = yaml.load(inputStream);
            return getProperty(obj, propKey);
        } catch (Exception e) {
            log.error("获取propKey错误:", e);
        }
        return null;
    }
    private static String getProperty(Map<String, Object> obj, String path) {
        String[] parts = path.split("\\.");
        Object value = obj;
        for (String part : parts) {
            if (value instanceof Map) {
                value = ((Map<String, Object>) value).get(part);
            } else {
                return null;
            }
        }
        return (String) value;
    }

    public static Properties loadPropertiesByStr(String str) {
        Properties props = new Properties();
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(str.getBytes());
            props.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return props;
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
        System.out.println(getPropValue("kafka.bootstrapServers"));
    }
}
