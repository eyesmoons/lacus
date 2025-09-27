package com.lacus.st.transform;

import com.google.auto.service.AutoService;
import com.lacus.st.abstracts.AbstractStTransform;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.annotation.StField;
import com.lacus.st.annotation.StTag;
import com.lacus.st.annotation.StTag.TagDefinition;
import com.lacus.st.interfaces.StComponentInterface;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Copy转换组件
 * 复制字段到新字段的转换插件
 * 
 * @author lacus
 */
@Slf4j
@StComponent(
        type = StComponent.ComponentType.TRANSFORM,
        name = "copy_transform",
        displayName = "Copy字段复制",
        description = "将字段复制到一个新字段，支持一对一和一对多的字段复制",
        version = "2.0.0",
        author = "lacus"
)
@StTag({
        @TagDefinition(name = "复制配置", displayName = "复制配置", order = 1, description = "字段复制相关配置")
})
@AutoService(StComponentInterface.class)
public class CopyTransform extends AbstractStTransform {

    @StField(
            tag = "复制配置",
            order = 1,
            required = true,
            enName = "fields",
            cnName = "字段复制映射",
            description = "定义字段复制的映射关系，格式：{\"新字段名1\": \"源字段名\", \"新字段名2\": \"源字段名\"}",
            placeHolder = "{\"name1\": \"name\", \"name2\": \"name\", \"age1\": \"age\"}",
            formType = StField.FormType.TEXT_AREA
    )
    private Map<String, String> fields;

    @StField(
            tag = "复制配置",
            order = 2,
            required = false,
            enName = "overwrite_existing",
            cnName = "覆盖已存在字段",
            defaultValue = "false",
            description = "是否覆盖已存在的字段，默认为false",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean overwriteExisting;

    /**
     * 获取字段复制映射配置
     */
    public Map<String, String> getFields() {
        return fields;
    }

    /**
     * 设置字段复制映射配置
     */
    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    /**
     * 获取是否覆盖已存在字段
     */
    public Boolean getOverwriteExisting() {
        return overwriteExisting != null ? overwriteExisting : false;
    }

    /**
     * 设置是否覆盖已存在字段
     */
    public void setOverwriteExisting(Boolean overwriteExisting) {
        this.overwriteExisting = overwriteExisting;
    }
}