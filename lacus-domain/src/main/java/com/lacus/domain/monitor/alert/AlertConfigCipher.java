package com.lacus.domain.monitor.alert;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import com.alibaba.fastjson2.JSON;
import com.lacus.alert.plugin.manager.AlertNotifierManager;
import com.lacus.alert.plugin.spi.AlertConfigField;
import com.lacus.alert.plugin.spi.AlertNotifier;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AlertConfigCipher {

    @Autowired
    private AlertProperties alertProperties;

    @Autowired
    private AlertNotifierManager alertNotifierManager;

    public String encryptConfig(String typeCode, Map<String, Object> rawConfig) {
        Map<String, Object> encrypted = new LinkedHashMap<>();
        if (rawConfig == null) {
            return JSON.toJSONString(encrypted);
        }
        Set<String> sensitiveFields = getSensitiveFields(typeCode);
        rawConfig.forEach((key, value) -> {
            if (value == null) {
                encrypted.put(key, null);
            } else if (sensitiveFields.contains(key) && StrUtil.isNotBlank(String.valueOf(value))) {
                encrypted.put(key, aes().encryptBase64(String.valueOf(value), StandardCharsets.UTF_8));
            } else {
                encrypted.put(key, value);
            }
        });
        return JSON.toJSONString(encrypted);
    }

    public String mergeAndEncryptConfig(String typeCode, Map<String, Object> newConfig, String oldEncryptedConfig) {
        Map<String, Object> merged = new HashMap<>(decryptConfig(typeCode, oldEncryptedConfig));
        if (newConfig != null) {
            newConfig.forEach((key, value) -> {
                if (value == null) {
                    merged.put(key, null);
                    return;
                }
                if (AlertCenterConstants.MASKED_VALUE.equals(String.valueOf(value))) {
                    return;
                }
                merged.put(key, value);
            });
        }
        return encryptConfig(typeCode, merged);
    }

    public Map<String, Object> decryptConfig(String typeCode, String encryptedConfig) {
        if (StrUtil.isBlank(encryptedConfig)) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> raw = JSON.parseObject(encryptedConfig, Map.class);
        if (raw == null) {
            return new LinkedHashMap<>();
        }
        Set<String> sensitiveFields = getSensitiveFields(typeCode);
        Map<String, Object> decrypted = new LinkedHashMap<>();
        raw.forEach((key, value) -> {
            if (value == null) {
                decrypted.put(key, null);
            } else if (sensitiveFields.contains(key) && StrUtil.isNotBlank(String.valueOf(value))) {
                decrypted.put(key, aes().decryptStr(String.valueOf(value), StandardCharsets.UTF_8));
            } else {
                decrypted.put(key, value);
            }
        });
        return decrypted;
    }

    public Map<String, Object> maskConfig(String typeCode, String encryptedConfig) {
        Map<String, Object> config = decryptConfig(typeCode, encryptedConfig);
        Set<String> sensitiveFields = getSensitiveFields(typeCode);
        config.replaceAll((key, value) -> sensitiveFields.contains(key) && value != null ? AlertCenterConstants.MASKED_VALUE : value);
        return config;
    }

    private Set<String> getSensitiveFields(String typeCode) {
        AlertNotifier notifier = alertNotifierManager.getNotifier(typeCode);
        if (notifier == null || notifier.getConfigSchema() == null) {
            return Collections.emptySet();
        }
        Set<String> sensitive = new HashSet<>();
        for (AlertConfigField field : notifier.getConfigSchema()) {
            if (field == null || StrUtil.isBlank(field.getField())) {
                continue;
            }
            if (Boolean.TRUE.equals(field.getSensitive()) || "password".equalsIgnoreCase(field.getType())) {
                sensitive.add(field.getField());
            }
        }
        return sensitive;
    }

    private AES aes() {
        byte[] key = normalizeKey(alertProperties.getSecurity().getEncryptKey());
        return new AES(Mode.CBC, Padding.PKCS5Padding, key, key);
    }

    private byte[] normalizeKey(String value) {
        byte[] source = StrUtil.blankToDefault(value, "LacusAlertKey123").getBytes(StandardCharsets.UTF_8);
        byte[] target = new byte[16];
        for (int i = 0; i < target.length; i++) {
            target[i] = i < source.length ? source[i] : 0;
        }
        return target;
    }
}
