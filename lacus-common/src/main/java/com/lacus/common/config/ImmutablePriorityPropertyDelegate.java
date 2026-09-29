package com.lacus.common.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class will get the property by the priority of the following: env > jvm > properties.
 */
@Slf4j
public class ImmutablePriorityPropertyDelegate extends ImmutablePropertyDelegate {

    private static final Map<String, Optional<ConfigValue<String>>> configValueMap = new ConcurrentHashMap<>();

    public ImmutablePriorityPropertyDelegate(String propertyAbsolutePath) {
        super(propertyAbsolutePath);
    }

    @Override
    public String get(String key) {
        Optional<ConfigValue<String>> configValue = configValueMap.computeIfAbsent(key, k -> {
            Optional<ConfigValue<String>> value = getConfigValueFromEnv(key);
            if (value.isPresent()) {
                log.debug("Override config value from env, key: {} actualKey: {}, value: {}",
                        k,
                        value.get().getActualKey(), value.get().getValue());
                return value;
            }
            value = getConfigValueFromJvm(key);
            if (value.isPresent()) {
                log.debug("Override config value from jvm, key: {} actualKey: {}, value: {}",
                        k, value.get().getActualKey(), value.get().getValue());
                return value;
            }
            value = getConfigValueFromProperties(key);
            value.ifPresent(
                    stringConfigValue -> log.debug("Get config value from properties, key: {} actualKey: {}, value: {}",
                            k, stringConfigValue.getActualKey(), stringConfigValue.getValue()));
            return value;
        });
        return configValue.map(ConfigValue::getValue).orElse(null);
    }

    @Override
    public String get(String key, String defaultValue) {
        String value = get(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    @Override
    public Set<String> getPropertyKeys() {
        Set<String> propertyKeys = new HashSet<>();
        propertyKeys.addAll(super.getPropertyKeys());
        propertyKeys.addAll(System.getProperties().stringPropertyNames());
        propertyKeys.addAll(System.getenv().keySet());
        return propertyKeys;
    }

    private Optional<ConfigValue<String>> getConfigValueFromEnv(String key) {
        String value = System.getenv(key);
        if (value != null) {
            return Optional.of(ConfigValue.fromEnv(key, value));
        }
        String envVarKey = String.valueOf(key).replaceAll("[.-]", "_").toUpperCase();
        String envVarVal = System.getenv(envVarKey);
        if (envVarVal != null) {
            return Optional.of(ConfigValue.fromEnv(key, envVarVal));
        }
        return Optional.empty();
    }

    private Optional<ConfigValue<String>> getConfigValueFromJvm(String key) {
        String value = System.getProperty(key);
        if (value != null) {
            return Optional.of(ConfigValue.fromJvm(key, value));
        }
        return Optional.empty();
    }

    private Optional<ConfigValue<String>> getConfigValueFromProperties(String key) {
        String value = super.get(key);
        if (value != null) {
            return Optional.of(ConfigValue.fromProperties(key, value));
        }
        return Optional.empty();
    }

    @Data
    @AllArgsConstructor
    public static final class ConfigValue<T> {

        private String actualKey;
        private T value;
        private boolean fromProperties;
        private boolean fromJvm;
        private boolean fromEnv;

        public static <T> ConfigValue<T> fromProperties(String actualKey, T value) {
            return new ConfigValue<>(actualKey, value, true, false, false);
        }

        public static <T> ConfigValue<T> fromJvm(String actualKey, T value) {
            return new ConfigValue<>(actualKey, value, false, true, false);
        }

        public static <T> ConfigValue<T> fromEnv(String actualKey, T value) {
            return new ConfigValue<>(actualKey, value, false, false, true);
        }
    }

}
