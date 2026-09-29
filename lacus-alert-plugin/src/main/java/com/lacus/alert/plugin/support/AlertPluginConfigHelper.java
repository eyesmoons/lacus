package com.lacus.alert.plugin.support;

import cn.hutool.core.util.StrUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class AlertPluginConfigHelper {

    private AlertPluginConfigHelper() {
    }

    public static String getString(Map<String, Object> config, String key) {
        if (config == null) {
            return null;
        }
        Object value = config.get(key);
        return value == null ? null : String.valueOf(value).trim();
    }

    public static Integer getInt(Map<String, Object> config, String key, Integer defaultValue) {
        String value = getString(config, key);
        if (StrUtil.isBlank(value)) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    public static Boolean getBoolean(Map<String, Object> config, String key, Boolean defaultValue) {
        String value = getString(config, key);
        if (StrUtil.isBlank(value)) {
            return defaultValue;
        }
        if ("1".equals(value)) {
            return Boolean.TRUE;
        }
        if ("0".equals(value)) {
            return Boolean.FALSE;
        }
        return Boolean.parseBoolean(value);
    }

    public static List<String> getStringList(Map<String, Object> config, String key) {
        if (config == null || !config.containsKey(key)) {
            return Collections.emptyList();
        }
        Object value = config.get(key);
        if (value instanceof Collection) {
            List<String> result = new ArrayList<>();
            for (Object item : (Collection<?>) value) {
                if (item != null && StrUtil.isNotBlank(String.valueOf(item))) {
                    result.add(String.valueOf(item).trim());
                }
            }
            return result;
        }
        String raw = value == null ? null : String.valueOf(value);
        if (StrUtil.isBlank(raw)) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        for (String item : raw.split("[,，;；\\n\\r]+")) {
            if (StrUtil.isNotBlank(item)) {
                result.add(item.trim());
            }
        }
        return result;
    }

    public static void require(Map<String, Object> config, String... keys) {
        for (String key : keys) {
            if (StrUtil.isBlank(getString(config, key))) {
                throw new IllegalArgumentException("缺少必要配置: " + key);
            }
        }
    }
}
