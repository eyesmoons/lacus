package com.lacus.common.config;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

/**
 * This class is used to get the properties from the classpath.
 */
@Slf4j
public class ImmutablePropertyDelegate implements IPropertyDelegate {

    private static final String COMMON_PROPERTIES_NAME = "/common.properties";

    private final Properties properties;

    public ImmutablePropertyDelegate() {
        this(COMMON_PROPERTIES_NAME);
    }

    public ImmutablePropertyDelegate(String... propertyAbsolutePath) {
        properties = new Properties();
        // read from classpath
        for (String fileName : propertyAbsolutePath) {
            try (InputStream fis = getClass().getResourceAsStream(fileName)) {
                Properties subProperties = new Properties();
                subProperties.load(fis);
                properties.putAll(subProperties);
            } catch (IOException e) {
                log.error("Load property: {} error, please check if the file exist under classpath",
                        propertyAbsolutePath, e);
                System.exit(1);
            }
        }
        printProperties();
    }

    public ImmutablePropertyDelegate(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String get(String key) {
        return properties.getProperty(key);
    }

    @Override
    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    @Override
    public Set<String> getPropertyKeys() {
        return properties.stringPropertyNames();
    }

    private void printProperties() {
        properties.forEach((k, v) -> log.debug("Get property {} -> {}", k, v));
    }
}
