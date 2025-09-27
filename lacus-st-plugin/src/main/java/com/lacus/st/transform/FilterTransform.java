package com.lacus.st.transform;

import com.google.auto.service.AutoService;
import com.lacus.st.abstracts.AbstractStTransform;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.annotation.StField;
import com.lacus.st.annotation.StTag;
import com.lacus.st.annotation.StTag.TagDefinition;
import com.lacus.st.interfaces.StComponentInterface;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Filter转换组件
 * 过滤器转换插件，用于过滤字段
 * 
 * @author lacus
 */
@Slf4j
@StComponent(
        type = StComponent.ComponentType.TRANSFORM,
        name = "filter_transform",
        displayName = "Filter字段过滤",
        description = "用于过滤字段，支持包含字段列表和排除字段列表",
        version = "2.0.0",
        author = "lacus"
)
@StTag({
        @TagDefinition(name = "过滤配置", displayName = "过滤配置", order = 1, description = "字段过滤相关配置")
})
@AutoService(StComponentInterface.class)
public class FilterTransform extends AbstractStTransform {

    @StField(
            tag = "过滤配置",
            order = 1,
            required = false,
            enName = "include_fields",
            cnName = "包含字段列表",
            description = "需要保留的字段列表，不在列表中的字段将被删除",
            placeHolder = "name,age,email",
            formType = StField.FormType.TEXT
    )
    private String includeFields;

    @StField(
            tag = "过滤配置",
            order = 2,
            required = false,
            enName = "exclude_fields",
            cnName = "排除字段列表", 
            description = "需要删除的字段列表，不在列表中的字段将被保留",
            placeHolder = "temp_field,internal_id",
            formType = StField.FormType.TEXT
    )
    private String excludeFields;

    @StField(
            tag = "过滤配置",
            order = 3,
            required = false,
            enName = "field_separator",
            cnName = "字段分隔符",
            defaultValue = ",",
            description = "字段列表的分隔符",
            placeHolder = ",",
            formType = StField.FormType.TEXT
    )
    private String fieldSeparator;

    @StField(
            tag = "过滤配置",
            order = 4,
            required = false,
            enName = "ignore_case",
            cnName = "忽略大小写",
            defaultValue = "false",
            description = "字段名匹配时是否忽略大小写",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean ignoreCase;

    public boolean doCheckConnection() {
        try {
            // 验证includeFields和excludeFields不能同时为空
            if ((includeFields == null || includeFields.trim().isEmpty()) && 
                (excludeFields == null || excludeFields.trim().isEmpty())) {
                log.error("Filter转换组件配置错误：include_fields和exclude_fields不能同时为空");
                return false;
            }

            // 验证includeFields和excludeFields不能同时设置
            if ((includeFields != null && !includeFields.trim().isEmpty()) && 
                (excludeFields != null && !excludeFields.trim().isEmpty())) {
                log.error("Filter转换组件配置错误：include_fields和exclude_fields不能同时设置");
                return false;
            }

            // 验证字段分隔符
            if (fieldSeparator == null || fieldSeparator.isEmpty()) {
                log.warn("Filter转换组件：字段分隔符为空，使用默认分隔符逗号");
                fieldSeparator = ",";
            }

            log.info("Filter转换组件连接检查成功");
            return true;
        } catch (Exception e) {
            log.error("Filter转换组件连接检查失败", e);
            return false;
        }
    }

    /**
     * 获取包含字段列表
     */
    public List<String> getIncludeFieldsList() {
        if (includeFields == null || includeFields.trim().isEmpty()) {
            return null;
        }
        String separator = fieldSeparator != null ? fieldSeparator : ",";
        String[] fields = includeFields.split(separator);
        return java.util.Arrays.stream(fields)
                .map(String::trim)
                .filter(field -> !field.isEmpty())
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取排除字段列表
     */
    public List<String> getExcludeFieldsList() {
        if (excludeFields == null || excludeFields.trim().isEmpty()) {
            return null;
        }
        String separator = fieldSeparator != null ? fieldSeparator : ",";
        String[] fields = excludeFields.split(separator);
        return java.util.Arrays.stream(fields)
                .map(String::trim)
                .filter(field -> !field.isEmpty())
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取包含字段配置
     */
    public String getIncludeFields() {
        return includeFields;
    }

    /**
     * 设置包含字段配置
     */
    public void setIncludeFields(String includeFields) {
        this.includeFields = includeFields;
    }

    /**
     * 获取排除字段配置
     */
    public String getExcludeFields() {
        return excludeFields;
    }

    /**
     * 设置排除字段配置
     */
    public void setExcludeFields(String excludeFields) {
        this.excludeFields = excludeFields;
    }

    /**
     * 获取字段分隔符
     */
    public String getFieldSeparator() {
        return fieldSeparator != null ? fieldSeparator : ",";
    }

    /**
     * 设置字段分隔符
     */
    public void setFieldSeparator(String fieldSeparator) {
        this.fieldSeparator = fieldSeparator;
    }

    /**
     * 获取是否忽略大小写
     */
    public Boolean getIgnoreCase() {
        return ignoreCase != null ? ignoreCase : false;
    }

    /**
     * 设置是否忽略大小写
     */
    public void setIgnoreCase(Boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }
}