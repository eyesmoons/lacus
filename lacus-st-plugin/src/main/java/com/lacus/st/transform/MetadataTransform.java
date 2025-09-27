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
 * Metadata转换组件
 * 元数据转换插件，用于将元数据字段添加到数据中
 * 
 * @author lacus
 */
@Slf4j
@StComponent(
        type = StComponent.ComponentType.TRANSFORM,
        name = "metadata_transform",
        displayName = "Metadata元数据",
        description = "将元数据字段添加到数据中，包括数据库名、表名、行类型等信息",
        version = "2.0.0",
        author = "lacus"
)
@StTag({
        @TagDefinition(name = "元数据配置", displayName = "元数据配置", order = 1, description = "元数据字段相关配置")
})

@AutoService(StComponentInterface.class)
public class MetadataTransform extends AbstractStTransform {

    @StField(
            tag = "元数据配置",
            order = 1,
            required = true,
            enName = "metadata_fields",
            cnName = "元数据字段映射",
            description = "元数据字段与输入字段相应的映射关系，格式：{\"输出字段名\": \"元数据类型\"}",
            placeHolder = "{\"database\": \"Database\", \"table\": \"Table\", \"rowKind\": \"RowKind\", \"ts_ms\": \"EventTime\", \"delay\": \"Delay\"}",
            formType = StField.FormType.TEXT_AREA
    )
    private Map<String, String> metadataFields;

    @StField(
            tag = "元数据配置",
            order = 2,
            required = false,
            enName = "add_prefix",
            cnName = "添加前缀",
            defaultValue = "",
            description = "为元数据字段添加统一前缀",
            placeHolder = "_meta_",
            formType = StField.FormType.TEXT
    )
    private String addPrefix;

    @StField(
            tag = "元数据配置",
            order = 3,
            required = false,
            enName = "add_suffix",
            cnName = "添加后缀",
            defaultValue = "",
            description = "为元数据字段添加统一后缀",
            placeHolder = "_metadata",
            formType = StField.FormType.TEXT
    )
    private String addSuffix;

    @StField(
            tag = "元数据配置",
            order = 4,
            required = false,
            enName = "ignore_null_metadata",
            cnName = "忽略空元数据",
            defaultValue = "false",
            description = "当元数据值为空时，是否忽略该字段",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean ignoreNullMetadata;

    public boolean doCheckConnection() {
        try {
            // 验证metadataFields配置不为空
            if (metadataFields == null || metadataFields.isEmpty()) {
                log.error("Metadata转换组件配置错误：metadata_fields元数据字段映射不能为空");
                return false;
            }

            // 验证支持的元数据类型
            String[] supportedMetadataTypes = {"Database", "Table", "RowKind", "EventTime", "Delay", "Partition"};
            
            for (Map.Entry<String, String> entry : metadataFields.entrySet()) {
                String fieldName = entry.getKey();
                String metadataType = entry.getValue();
                
                if (fieldName == null || fieldName.trim().isEmpty()) {
                    log.error("Metadata转换组件配置错误：输出字段名不能为空");
                    return false;
                }
                
                if (metadataType == null || metadataType.trim().isEmpty()) {
                    log.error("Metadata转换组件配置错误：元数据类型不能为空");
                    return false;
                }
                
                // 检查是否为支持的元数据类型
                boolean isSupported = false;
                for (String supportedType : supportedMetadataTypes) {
                    if (supportedType.equalsIgnoreCase(metadataType.trim())) {
                        isSupported = true;
                        break;
                    }
                }
                
                if (!isSupported) {
                    log.error("Metadata转换组件配置错误：不支持的元数据类型 {}，支持的类型：{}", 
                            metadataType, String.join(", ", supportedMetadataTypes));
                    return false;
                }
            }

            log.info("Metadata转换组件连接检查成功，配置了 {} 个元数据字段映射", metadataFields.size());
            return true;
        } catch (Exception e) {
            log.error("Metadata转换组件连接检查失败", e);
            return false;
        }
    }

    /**
     * 获取完整的输出字段名（包含前缀和后缀）
     */
    public String getFullOutputFieldName(String originalFieldName) {
        StringBuilder fullName = new StringBuilder();
        
        if (addPrefix != null && !addPrefix.trim().isEmpty()) {
            fullName.append(addPrefix.trim());
        }
        
        fullName.append(originalFieldName);
        
        if (addSuffix != null && !addSuffix.trim().isEmpty()) {
            fullName.append(addSuffix.trim());
        }
        
        return fullName.toString();
    }

    /**
     * 获取元数据字段映射配置
     */
    public Map<String, String> getMetadataFields() {
        return metadataFields;
    }

    /**
     * 设置元数据字段映射配置
     */
    public void setMetadataFields(Map<String, String> metadataFields) {
        this.metadataFields = metadataFields;
    }

    /**
     * 获取字段前缀
     */
    public String getAddPrefix() {
        return addPrefix;
    }

    /**
     * 设置字段前缀
     */
    public void setAddPrefix(String addPrefix) {
        this.addPrefix = addPrefix;
    }

    /**
     * 获取字段后缀
     */
    public String getAddSuffix() {
        return addSuffix;
    }

    /**
     * 设置字段后缀
     */
    public void setAddSuffix(String addSuffix) {
        this.addSuffix = addSuffix;
    }

    /**
     * 获取是否忽略空元数据
     */
    public Boolean getIgnoreNullMetadata() {
        return ignoreNullMetadata != null ? ignoreNullMetadata : false;
    }

    /**
     * 设置是否忽略空元数据
     */
    public void setIgnoreNullMetadata(Boolean ignoreNullMetadata) {
        this.ignoreNullMetadata = ignoreNullMetadata;
    }
}