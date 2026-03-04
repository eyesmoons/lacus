package com.lacus.st.utils;

import com.lacus.st.loader.StComponentLoader;
import com.lacus.st.utils.StTagUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 表单构建工具类
 * 用于根据组件字段配置生成前端表单配置
 *
 * @author lacus
 */
@Slf4j
@Component
public class FormBuilder {

    @Autowired
    private StComponentLoader stComponentLoader;

    /**
     * 为指定组件生成表单配置
     * @param componentName 组件名称
     * @return 表单配置
     */
    public Map<String, Object> buildFormConfig(String componentName) {
        Map<String, Object> result = new HashMap<>();

        try {
            stComponentLoader.initialize();
            if (!stComponentLoader.hasComponent(componentName)) {
                result.put("success", false);
                result.put("message", "组件不存在: " + componentName);
                return result;
            }

            Map<String, Object> metadata = stComponentLoader.getComponentMetadata(componentName);
            Map<String, Object> fieldConfigs = (Map<String, Object>) metadata.get("fieldConfigs");
            
            // 按标签分组字段并排序
            Map<String, List<Map<String, Object>>> groupedFields = groupFieldsByTag(fieldConfigs);
            
            // 添加标签排序信息（如果组件类可获取）
            try {
                Class<?> componentClass = Class.forName(componentName);
                List<StTagUtils.TagInfo> tagInfos = StTagUtils.parseAndSortTags(componentClass);
                result.put("tagInfos", tagInfos);
            } catch (ClassNotFoundException e) {
                log.debug("无法获取组件类，跳过标签排序: {}", componentName);
            }

        } catch (Exception e) {
            log.error("构建表单配置失败", e);
            result.put("success", false);
            result.put("message", "构建表单配置失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 按标签分组字段
     */
    private Map<String, List<Map<String, Object>>> groupFieldsByTag(Map<String, Object> fieldConfigs) {
        Map<String, List<Map<String, Object>>> groupedFields = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : fieldConfigs.entrySet()) {
            String fieldName = entry.getKey();
            Map<String, Object> fieldConfig = (Map<String, Object>) entry.getValue();

            String tag = (String) fieldConfig.get("tag");
            if (tag == null || tag.trim().isEmpty()) {
                tag = "其他";
            }

            // 添加字段名到配置中
            fieldConfig.put("fieldName", fieldName);

            groupedFields.computeIfAbsent(tag, k -> new ArrayList<>()).add(fieldConfig);
        }

        return groupedFields;
    }

    /**
     * 生成表单验证规则
     * @param componentName 组件名称
     * @return 验证规则
     */
    public Map<String, Object> buildValidationRules(String componentName) {
        Map<String, Object> result = new HashMap<>();

        try {
            stComponentLoader.initialize();
            if (!stComponentLoader.hasComponent(componentName)) {
                result.put("success", false);
                result.put("message", "组件不存在: " + componentName);
                return result;
            }

            Map<String, Object> metadata = stComponentLoader.getComponentMetadata(componentName);
            Map<String, Object> fieldConfigs = (Map<String, Object>) metadata.get("fieldConfigs");

            Map<String, Map<String, Object>> validationRules = new HashMap<>();

            for (Map.Entry<String, Object> entry : fieldConfigs.entrySet()) {
                String fieldName = entry.getKey();
                Map<String, Object> fieldConfig = (Map<String, Object>) entry.getValue();

                Map<String, Object> rules = new HashMap<>();

                // 必填验证
                if ((Boolean) fieldConfig.get("required")) {
                    rules.put("required", true);
                    rules.put("message", fieldConfig.get("cnName") + "不能为空");
                }

                // 根据表单类型添加特定验证规则
                String formType = (String) fieldConfig.get("formType");
                switch (formType) {
                    case "number":
                    case "positive_number":
                        rules.put("type", "number");
                        if ("positive_number".equals(formType)) {
                            rules.put("min", 0);
                            rules.put("message", fieldConfig.get("cnName") + "必须为正数");
                        }
                        break;
                    case "text":
                    case "text_area":
                        rules.put("type", "string");
                        break;
                    case "password":
                        rules.put("type", "string");
                        rules.put("min", 6);
                        rules.put("message", "密码长度不能少于6位");
                        break;
                }

                if (!rules.isEmpty()) {
                    validationRules.put(fieldName, rules);
                }
            }

            result.put("success", true);
            result.put("rules", validationRules);

        } catch (Exception e) {
            log.error("构建验证规则失败", e);
            result.put("success", false);
            result.put("message", "构建验证规则失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 生成表单默认值
     * @param componentName 组件名称
     * @return 默认值配置
     */
    public Map<String, Object> buildDefaultValues(String componentName) {
        Map<String, Object> result = new HashMap<>();

        try {
            stComponentLoader.initialize();
            if (!stComponentLoader.hasComponent(componentName)) {
                result.put("success", false);
                result.put("message", "组件不存在: " + componentName);
                return result;
            }

            Map<String, Object> metadata = stComponentLoader.getComponentMetadata(componentName);
            Map<String, Object> fieldConfigs = (Map<String, Object>) metadata.get("fieldConfigs");

            Map<String, Object> defaultValues = new HashMap<>();

            for (Map.Entry<String, Object> entry : fieldConfigs.entrySet()) {
                String fieldName = entry.getKey();
                Map<String, Object> fieldConfig = (Map<String, Object>) entry.getValue();

                String defaultValue = (String) fieldConfig.get("defaultValue");
                if (defaultValue != null && !defaultValue.trim().isEmpty()) {
                    // 根据字段类型转换默认值
                    String fieldType = (String) fieldConfig.get("fieldType");
                    Object convertedValue = convertDefaultValue(defaultValue, fieldType);
                    defaultValues.put(fieldName, convertedValue);
                }
            }

            result.put("success", true);
            result.put("defaultValues", defaultValues);

        } catch (Exception e) {
            log.error("构建默认值失败", e);
            result.put("success", false);
            result.put("message", "构建默认值失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 转换默认值类型
     */
    private Object convertDefaultValue(String defaultValue, String fieldType) {
        try {
            switch (fieldType.toLowerCase()) {
                case "integer":
                case "int":
                    return Integer.valueOf(defaultValue);
                case "long":
                    return Long.valueOf(defaultValue);
                case "boolean":
                    return Boolean.valueOf(defaultValue);
                case "double":
                    return Double.valueOf(defaultValue);
                case "float":
                    return Float.valueOf(defaultValue);
                default:
                    return defaultValue;
            }
        } catch (Exception e) {
            log.warn("转换默认值失败: {} -> {}", defaultValue, fieldType, e);
            return defaultValue;
        }
    }
}
