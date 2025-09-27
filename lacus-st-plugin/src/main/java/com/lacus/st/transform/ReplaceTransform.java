package com.lacus.st.transform;

import com.google.auto.service.AutoService;
import com.lacus.st.abstracts.AbstractStTransform;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.annotation.StField;
import com.lacus.st.interfaces.StComponentInterface;
import com.lacus.st.annotation.StTag;
import com.lacus.st.annotation.StTag.TagDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

/**
 * Replace转换组件
 * 替换转换插件，检查给定字段中的字符串值并替换匹配的子字符串
 * 
 * @author lacus
 */
@Slf4j
@StComponent(
        type = StComponent.ComponentType.TRANSFORM,
        name = "replace_transform",
        displayName = "Replace字符串替换",
        description = "检查给定字段中的字符串值，并用给定的替换项替换与给定字符串字面量或正则表达式匹配的子字符串",
        version = "2.0.0",
        author = "lacus"
)
@StTag({
        @TagDefinition(name = "替换配置", displayName = "替换配置", order = 1, description = "字符串替换相关配置")
})

@AutoService(StComponentInterface.class)
public class ReplaceTransform extends AbstractStTransform {

    @StField(
            tag = "替换配置",
            order = 1,
            required = true,
            enName = "replace_field",
            cnName = "需要替换的字段",
            description = "指定需要进行字符串替换的字段名",
            placeHolder = "name",
            formType = StField.FormType.TEXT
    )
    private String replaceField;

    @StField(
            tag = "替换配置",
            order = 2,
            required = true,
            enName = "pattern",
            cnName = "匹配模式",
            description = "将被替换的旧字符串或正则表达式",
            placeHolder = " ",
            formType = StField.FormType.TEXT
    )
    private String pattern;

    @StField(
            tag = "替换配置",
            order = 3,
            required = true,
            enName = "replacement",
            cnName = "替换字符串",
            description = "用于替换的新字符串",
            placeHolder = "_",
            formType = StField.FormType.TEXT
    )
    private String replacement;

    @StField(
            tag = "替换配置",
            order = 4,
            required = false,
            enName = "is_regex",
            cnName = "使用正则表达式",
            defaultValue = "false",
            description = "是否使用正则表达式进行字符串匹配",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean isRegex;

    @StField(
            tag = "替换配置",
            order = 5,
            required = false,
            enName = "replace_first",
            cnName = "仅替换第一个匹配",
            defaultValue = "false",
            description = "是否仅替换第一个匹配的字符串（仅在is_regex=true时使用）",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean replaceFirst;

    @StField(
            tag = "替换配置",
            order = 6,
            required = false,
            enName = "ignore_case",
            cnName = "忽略大小写",
            defaultValue = "false",
            description = "进行字符串匹配时是否忽略大小写",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean ignoreCase;

    @StField(
            tag = "替换配置",
            order = 7,
            required = false,
            enName = "create_new_field",
            cnName = "创建新字段",
            defaultValue = "false",
            description = "是否创建新字段存储替换结果，而不是修改原字段",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean createNewField;

    @StField(
            tag = "替换配置",
            order = 8,
            required = false,
            enName = "new_field_name",
            cnName = "新字段名",
            description = "当create_new_field为true时，指定新字段的名称",
            placeHolder = "new_field_name",
            formType = StField.FormType.TEXT
    )
    private String newFieldName;

    // 编译后的正则表达式模式，提高性能
    private Pattern compiledPattern;

    public boolean doCheckConnection() {
        try {
            // 验证必填字段
            if (replaceField == null || replaceField.trim().isEmpty()) {
                log.error("Replace转换组件配置错误：replace_field不能为空");
                return false;
            }

            if (pattern == null) {
                log.error("Replace转换组件配置错误：pattern不能为空");
                return false;
            }

            if (replacement == null) {
                log.error("Replace转换组件配置错误：replacement不能为null");
                return false;
            }

            // 验证正则表达式的有效性
            boolean useRegex = isRegex != null ? isRegex : false;
            if (useRegex) {
                try {
                    int flags = 0;
                    if (ignoreCase != null && ignoreCase) {
                        flags |= Pattern.CASE_INSENSITIVE;
                    }
                    compiledPattern = Pattern.compile(pattern, flags);
                    log.info("正则表达式模式编译成功：{}", pattern);
                } catch (Exception e) {
                    log.error("Replace转换组件配置错误：无效的正则表达式模式 {}", pattern, e);
                    return false;
                }
            }

            // 验证创建新字段的配置
            boolean createNew = createNewField != null ? createNewField : false;
            if (createNew && (newFieldName == null || newFieldName.trim().isEmpty())) {
                log.error("Replace转换组件配置错误：当create_new_field为true时，new_field_name不能为空");
                return false;
            }

            log.info("Replace转换组件连接检查成功，字段：{}，模式：{}，替换：{}", replaceField, pattern, replacement);
            return true;
        } catch (Exception e) {
            log.error("Replace转换组件连接检查失败", e);
            return false;
        }
    }

    /**
     * 执行字符串替换
     */
    public String performReplace(String input) {
        if (input == null) {
            return null;
        }

        boolean useRegex = isRegex != null ? isRegex : false;
        
        if (useRegex) {
            if (compiledPattern == null) {
                // 如果没有预编译的模式，重新编译
                try {
                    int flags = 0;
                    if (ignoreCase != null && ignoreCase) {
                        flags |= Pattern.CASE_INSENSITIVE;
                    }
                    compiledPattern = Pattern.compile(pattern, flags);
                } catch (Exception e) {
                    log.error("正则表达式编译失败：{}", pattern, e);
                    return input;
                }
            }
            
            boolean replaceOnlyFirst = replaceFirst != null ? replaceFirst : false;
            if (replaceOnlyFirst) {
                return compiledPattern.matcher(input).replaceFirst(replacement);
            } else {
                return compiledPattern.matcher(input).replaceAll(replacement);
            }
        } else {
            // 字面量替换
            String searchPattern = pattern;
            String inputToSearch = input;
            
            if (ignoreCase != null && ignoreCase) {
                searchPattern = searchPattern.toLowerCase();
                inputToSearch = input.toLowerCase();
            }
            
            if (inputToSearch.contains(searchPattern)) {
                if (ignoreCase != null && ignoreCase) {
                    // 大小写不敏感的替换需要特殊处理
                    return input.replaceAll("(?i)" + Pattern.quote(pattern), replacement);
                } else {
                    return input.replace(pattern, replacement);
                }
            }
            
            return input;
        }
    }

    /**
     * 获取需要替换的字段名
     */
    public String getReplaceField() {
        return replaceField;
    }

    /**
     * 设置需要替换的字段名
     */
    public void setReplaceField(String replaceField) {
        this.replaceField = replaceField;
    }

    /**
     * 获取匹配模式
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * 设置匹配模式
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
        // 重置编译后的模式
        this.compiledPattern = null;
    }

    /**
     * 获取替换字符串
     */
    public String getReplacement() {
        return replacement;
    }

    /**
     * 设置替换字符串
     */
    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    /**
     * 获取是否使用正则表达式
     */
    public Boolean getIsRegex() {
        return isRegex != null ? isRegex : false;
    }

    /**
     * 设置是否使用正则表达式
     */
    public void setIsRegex(Boolean isRegex) {
        this.isRegex = isRegex;
        // 重置编译后的模式
        this.compiledPattern = null;
    }

    /**
     * 获取是否仅替换第一个匹配
     */
    public Boolean getReplaceFirst() {
        return replaceFirst != null ? replaceFirst : false;
    }

    /**
     * 设置是否仅替换第一个匹配
     */
    public void setReplaceFirst(Boolean replaceFirst) {
        this.replaceFirst = replaceFirst;
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
        // 重置编译后的模式
        this.compiledPattern = null;
    }

    /**
     * 获取是否创建新字段
     */
    public Boolean getCreateNewField() {
        return createNewField != null ? createNewField : false;
    }

    /**
     * 设置是否创建新字段
     */
    public void setCreateNewField(Boolean createNewField) {
        this.createNewField = createNewField;
    }

    /**
     * 获取新字段名
     */
    public String getNewFieldName() {
        return newFieldName;
    }

    /**
     * 设置新字段名
     */
    public void setNewFieldName(String newFieldName) {
        this.newFieldName = newFieldName;
    }
}