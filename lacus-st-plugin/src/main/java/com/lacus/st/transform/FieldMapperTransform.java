package com.lacus.st.transform;

import com.google.auto.service.AutoService;
import com.lacus.st.abstracts.AbstractStTransform;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.annotation.StField;
import com.lacus.st.interfaces.StComponentInterface;
import com.lacus.st.annotation.StTag;
import com.lacus.st.annotation.StTag.TagDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * FieldMapper转换组件
 * 字段映射转换插件，添加输入模式和输出模式映射
 * 
 * @author lacus
 */
@Slf4j
@StComponent(
        type = StComponent.ComponentType.TRANSFORM,
        name = "field_mapper_transform",
        displayName = "FieldMapper字段映射",
        description = "添加输入模式和输出模式映射，支持字段重命名、删除和重新排序",
        version = "2.0.0",
        author = "lacus"
)
@StTag({
        @TagDefinition(name = "映射配置", displayName = "映射配置", order = 1, description = "字段映射相关配置")
})

@AutoService(StComponentInterface.class)
public class FieldMapperTransform extends AbstractStTransform {

    @StField(
            tag = "映射配置",
            order = 1,
            required = true,
            enName = "field_mapper",
            cnName = "字段映射关系",
            description = "指定输入和输出之间的字段映射关系，格式：{\"输入字段名\": \"输出字段名\"}",
            placeHolder = "{\"id\": \"id\", \"card\": \"card\", \"name\": \"new_name\"}",
            formType = StField.FormType.TEXT_AREA
    )
    private Map<String, String> fieldMapper;

    @StField(
            tag = "映射配置",
            order = 2,
            required = false,
            enName = "ignore_missing_fields",
            cnName = "忽略缺失字段",
            defaultValue = "false",
            description = "当输入字段不存在时，是否忽略该字段映射",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean ignoreMissingFields;

    @StField(
            tag = "映射配置",
            order = 3,
            required = false,
            enName = "allow_null_values",
            cnName = "允许空值",
            defaultValue = "true",
            description = "是否允许映射空值字段",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean allowNullValues;

    public boolean doCheckConnection() {
        try {
            // 验证fieldMapper配置不为空
            if (fieldMapper == null || fieldMapper.isEmpty()) {
                log.error("FieldMapper转换组件配置错误：field_mapper字段映射不能为空");
                return false;
            }

            // 验证字段映射配置的有效性
            for (Map.Entry<String, String> entry : fieldMapper.entrySet()) {
                String inputField = entry.getKey();
                String outputField = entry.getValue();
                
                if (inputField == null || inputField.trim().isEmpty()) {
                    log.error("FieldMapper转换组件配置错误：输入字段名不能为空");
                    return false;
                }
                
                if (outputField == null || outputField.trim().isEmpty()) {
                    log.error("FieldMapper转换组件配置错误：输出字段名不能为空");
                    return false;
                }
            }

            log.info("FieldMapper转换组件连接检查成功，配置了 {} 个字段映射", fieldMapper.size());
            return true;
        } catch (Exception e) {
            log.error("FieldMapper转换组件连接检查失败", e);
            return false;
        }
    }

    /**
     * 获取字段映射配置
     */
    public Map<String, String> getFieldMapper() {
        return fieldMapper;
    }

    /**
     * 设置字段映射配置
     */
    public void setFieldMapper(Map<String, String> fieldMapper) {
        this.fieldMapper = fieldMapper;
    }

    /**
     * 获取是否忽略缺失字段
     */
    public Boolean getIgnoreMissingFields() {
        return ignoreMissingFields != null ? ignoreMissingFields : false;
    }

    /**
     * 设置是否忽略缺失字段
     */
    public void setIgnoreMissingFields(Boolean ignoreMissingFields) {
        this.ignoreMissingFields = ignoreMissingFields;
    }

    /**
     * 获取是否允许空值
     */
    public Boolean getAllowNullValues() {
        return allowNullValues != null ? allowNullValues : true;
    }

    /**
     * 设置是否允许空值
     */
    public void setAllowNullValues(Boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }
}