package com.lacus.st.abstracts;

import com.alibaba.fastjson2.JSONObject;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.interfaces.StComponentInterface;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * ST组件抽象基类
 * 提供通用的组件功能实现
 *
 * @author lacus
 */
@Slf4j
public abstract class AbstractStComponent implements StComponentInterface {

    protected Map<String, Object> config = new HashMap<>();
    protected StComponentInterface.ComponentStatus status = StComponentInterface.ComponentStatus.INITIALIZED;

    @Override
    public boolean initialize(Map<String, Object> config) {
        try {
            this.config = config != null ? new HashMap<>(config) : new HashMap<>();
            // 将配置参数设置到组件的字段中
            setConfigToFields();
            status = StComponentInterface.ComponentStatus.INITIALIZED;
            log.info("组件 {} 初始化成功", getClass().getSimpleName());
            return true;
        } catch (Exception e) {
            log.error("组件 {} 初始化失败", getClass().getSimpleName(), e);
            status = StComponentInterface.ComponentStatus.ERROR;
            return false;
        }
    }

    @Override
    public ValidationResult validateConfig(Map<String, Object> config) {
        try {
            // 获取组件注解信息
            StComponent componentAnnotation = getClass().getAnnotation(StComponent.class);
            if (componentAnnotation == null) {
                return ValidationResult.failure("组件缺少@StComponent注解");
            }

            // 递归获取所有父类字段，包括继承的字段
            Set<Field> allFields = getAllFields(getClass());

            // 验证必填字段
            for (Field field : allFields) {
                com.lacus.st.annotation.StField fieldAnnotation = field.getAnnotation(com.lacus.st.annotation.StField.class);
                if (fieldAnnotation != null && fieldAnnotation.required()) {
                    String fieldName = field.getName();
                    if (!config.containsKey(fieldName) || config.get(fieldName) == null ||
                        config.get(fieldName).toString().trim().isEmpty()) {
                        return ValidationResult.failure("必填字段 " + fieldAnnotation.cnName() + " (" + fieldName + ") 不能为空");
                    }
                }
            }

            return ValidationResult.success();
        } catch (Exception e) {
            log.error("配置验证失败", e);
            return ValidationResult.failure("配置验证异常: " + e.getMessage());
        }
    }

    /**
     * 递归获取所有字段，包括父类字段
     */
    private Set<Field> getAllFields(Class<?> clazz) {
        Set<Field> fields = new HashSet<>();

        while (clazz != null && clazz != Object.class) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                // 过滤掉静态和 final 字段
                if (!java.lang.reflect.Modifier.isStatic(field.getModifiers()) &&
                    !java.lang.reflect.Modifier.isFinal(field.getModifiers())) {
                    fields.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }

        return fields;
    }

    @Override
    public ComponentMetadata getMetadata() {
        StComponent componentAnnotation = getClass().getAnnotation(StComponent.class);
        if (componentAnnotation == null) {
            return null;
        }

        return new StComponentInterface.ComponentMetadata(
                componentAnnotation.name(),
                componentAnnotation.displayName(),
                componentAnnotation.description(),
                componentAnnotation.version(),
                componentAnnotation.author(),
                componentAnnotation.type().getValue()
        );
    }

    /**
     * 将配置参数设置到组件的字段中
     */
    private void setConfigToFields() {
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            com.lacus.st.annotation.StField fieldAnnotation = field.getAnnotation(com.lacus.st.annotation.StField.class);
            if (fieldAnnotation != null && config.containsKey(field.getName())) {
                try {
                    field.setAccessible(true);
                    Object value = config.get(field.getName());
                    if (value != null) {
                        Object convertedValue = convertValue(value, field.getType());
                        if (convertedValue != null) {
                            field.set(this, convertedValue);
                        }
                    }
                } catch (Exception e) {
                    log.warn("设置字段 {} 的值失败: {}", field.getName(), e.getMessage());
                }
            }
        }
    }

    @Override
    public JSONObject buildTaskConfig(JSONObject connectionConfig, Long datasourceId) {
        // 默认实现：直接返回连接配置，子类可以重写此方法以实现特定逻辑
        return connectionConfig;
    }

    /**
     * 类型转换方法
     */
    private Object convertValue(Object value, Class<?> targetType) {
        try {
            String valueStr = value.toString().trim();

            if (targetType == String.class) {
                return valueStr;
            } else if (targetType == Integer.class || targetType == int.class) {
                return valueStr.isEmpty() ? null : Integer.valueOf(valueStr);
            } else if (targetType == Long.class || targetType == long.class) {
                return valueStr.isEmpty() ? null : Long.valueOf(valueStr);
            } else if (targetType == Boolean.class || targetType == boolean.class) {
                return valueStr.isEmpty() ? null : Boolean.valueOf(valueStr);
            } else if (targetType == Double.class || targetType == double.class) {
                return valueStr.isEmpty() ? null : Double.valueOf(valueStr);
            } else if (targetType == Float.class || targetType == float.class) {
                return valueStr.isEmpty() ? null : Float.valueOf(valueStr);
            } else {
                return value;
            }
        } catch (NumberFormatException e) {
            log.warn("数值类型转换失败: {} -> {}", value, targetType.getSimpleName());
            return null;
        } catch (Exception e) {
            log.warn("类型转换失败: {} -> {}", value, targetType.getSimpleName());
            return null;
        }
    }

    protected void putIfNotEmpty(JSONObject config, String key, Object value) {
        if (ObjectUtils.isNotEmpty(value)) {
            config.put(key, value);
        }
    }
}
