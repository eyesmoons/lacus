package com.lacus.st.transform;

import com.google.auto.service.AutoService;
import com.lacus.st.abstracts.AbstractStTransform;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.annotation.StField;
import com.lacus.st.interfaces.StComponentInterface;
import com.lacus.st.annotation.StTag;
import com.lacus.st.annotation.StTag.TagDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * FieldRename转换组件
 * 用于重命名字段，支持大小写转换、前缀后缀和正则替换
 * 
 * @author lacus
 */
@Slf4j
@StComponent(
        type = StComponent.ComponentType.TRANSFORM,
        name = "field_rename_transform",
        displayName = "FieldRename字段重命名",
        description = "用于重命名字段，支持大小写转换、前缀后缀添加和正则表达式替换",
        version = "2.0.0",
        author = "lacus"
)
@StTag({
        @TagDefinition(name = "重命名配置", displayName = "重命名配置", order = 1, description = "字段重命名相关配置")
})

@AutoService(StComponentInterface.class)
public class FieldRenameTransform extends AbstractStTransform {

    @StField(
            tag = "重命名配置",
            order = 1,
            required = false,
            enName = "convert_case",
            cnName = "大小写转换",
            description = "字段名大小写转换类型",
            defaultValue = "NONE",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"NONE", "UPPER", "LOWER"}
    )
    private String convertCase;

    @StField(
            tag = "重命名配置",
            order = 2,
            required = false,
            enName = "prefix",
            cnName = "字段前缀",
            description = "为字段名添加的前缀",
            placeHolder = "F_",
            formType = StField.FormType.TEXT
    )
    private String prefix;

    @StField(
            tag = "重命名配置",
            order = 3,
            required = false,
            enName = "suffix",
            cnName = "字段后缀",
            description = "为字段名添加的后缀",
            placeHolder = "_S",
            formType = StField.FormType.TEXT
    )
    private String suffix;

    @StField(
            tag = "重命名配置",
            order = 4,
            required = false,
            enName = "replacements_with_regex",
            cnName = "正则替换规则",
            description = "使用正则表达式的替换规则列表，格式：[{\"replace_from\": \"原字符串\", \"replace_to\": \"新字符串\"}]",
            placeHolder = "[{\"replace_from\": \"create_time\", \"replace_to\": \"SOURCE_CREATE_TIME\"}]",
            formType = StField.FormType.TEXT_AREA
    )
    private String replacementsWithRegex;

    @StField(
            tag = "重命名配置",
            order = 5,
            required = false,
            enName = "apply_to_all_fields",
            cnName = "应用到所有字段",
            defaultValue = "true",
            description = "是否将重命名规则应用到所有字段",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean applyToAllFields;

    @StField(
            tag = "重命名配置",
            order = 6,
            required = false,
            enName = "target_fields",
            cnName = "目标字段列表",
            description = "指定需要重命名的字段列表，用逗号分隔（当apply_to_all_fields为false时使用）",
            placeHolder = "field1,field2,field3",
            formType = StField.FormType.TEXT
    )
    private String targetFields;

    public boolean doCheckConnection() {
        try {
            // 验证大小写转换配置
            if (convertCase != null && !convertCase.trim().isEmpty()) {
                String caseType = convertCase.trim().toUpperCase();
                if (!"NONE".equals(caseType) && !"UPPER".equals(caseType) && !"LOWER".equals(caseType)) {
                    log.error("FieldRename转换组件配置错误：不支持的大小写转换类型 {}，支持的类型：NONE, UPPER, LOWER", convertCase);
                    return false;
                }
            }

            // 验证是否至少配置了一种重命名规则
            boolean hasRenameRule = false;
            
            if (convertCase != null && !convertCase.trim().isEmpty() && !"NONE".equalsIgnoreCase(convertCase.trim())) {
                hasRenameRule = true;
            }
            
            if (prefix != null && !prefix.trim().isEmpty()) {
                hasRenameRule = true;
            }
            
            if (suffix != null && !suffix.trim().isEmpty()) {
                hasRenameRule = true;
            }
            
            if (replacementsWithRegex != null && !replacementsWithRegex.trim().isEmpty()) {
                hasRenameRule = true;
            }
            
            if (!hasRenameRule) {
                log.error("FieldRename转换组件配置错误：必须至少配置一种重命名规则（大小写转换、前缀、后缀或正则替换）");
                return false;
            }

            // 验证当apply_to_all_fields为false时，target_fields不能为空
            boolean applyAll = applyToAllFields != null ? applyToAllFields : true;
            if (!applyAll && (targetFields == null || targetFields.trim().isEmpty())) {
                log.error("FieldRename转换组件配置错误：当apply_to_all_fields为false时，target_fields不能为空");
                return false;
            }

            log.info("FieldRename转换组件连接检查成功");
            return true;
        } catch (Exception e) {
            log.error("FieldRename转换组件连接检查失败", e);
            return false;
        }
    }

    /**
     * 获取目标字段列表
     */
    public List<String> getTargetFieldsList() {
        if (targetFields == null || targetFields.trim().isEmpty()) {
            return null;
        }
        String[] fields = targetFields.split(",");
        return java.util.Arrays.stream(fields)
                .map(String::trim)
                .filter(field -> !field.isEmpty())
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 应用重命名规则到字段名
     */
    public String applyRenameRules(String originalFieldName) {
        if (originalFieldName == null) {
            return null;
        }
        
        String result = originalFieldName;
        
        // 应用前缀
        if (prefix != null && !prefix.trim().isEmpty()) {
            result = prefix.trim() + result;
        }
        
        // 应用后缀
        if (suffix != null && !suffix.trim().isEmpty()) {
            result = result + suffix.trim();
        }
        
        // 应用大小写转换
        if (convertCase != null && !convertCase.trim().isEmpty()) {
            String caseType = convertCase.trim().toUpperCase();
            switch (caseType) {
                case "UPPER":
                    result = result.toUpperCase();
                    break;
                case "LOWER":
                    result = result.toLowerCase();
                    break;
                case "NONE":
                default:
                    // 不做转换
                    break;
            }
        }
        
        // TODO: 应用正则替换规则（需要解析replacementsWithRegex JSON）
        
        return result;
    }

    /**
     * 获取大小写转换类型
     */
    public String getConvertCase() {
        return convertCase;
    }

    /**
     * 设置大小写转换类型
     */
    public void setConvertCase(String convertCase) {
        this.convertCase = convertCase;
    }

    /**
     * 获取字段前缀
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * 设置字段前缀
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * 获取字段后缀
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * 设置字段后缀
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /**
     * 获取正则替换规则
     */
    public String getReplacementsWithRegex() {
        return replacementsWithRegex;
    }

    /**
     * 设置正则替换规则
     */
    public void setReplacementsWithRegex(String replacementsWithRegex) {
        this.replacementsWithRegex = replacementsWithRegex;
    }

    /**
     * 获取是否应用到所有字段
     */
    public Boolean getApplyToAllFields() {
        return applyToAllFields != null ? applyToAllFields : true;
    }

    /**
     * 设置是否应用到所有字段
     */
    public void setApplyToAllFields(Boolean applyToAllFields) {
        this.applyToAllFields = applyToAllFields;
    }

    /**
     * 获取目标字段配置
     */
    public String getTargetFields() {
        return targetFields;
    }

    /**
     * 设置目标字段配置
     */
    public void setTargetFields(String targetFields) {
        this.targetFields = targetFields;
    }
}