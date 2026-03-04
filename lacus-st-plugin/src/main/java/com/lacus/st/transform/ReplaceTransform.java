package com.lacus.st.transform;

import com.alibaba.fastjson2.JSONObject;
import com.google.auto.service.AutoService;
import com.lacus.st.abstracts.AbstractStTransform;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.annotation.StField;
import com.lacus.st.annotation.StTag;
import com.lacus.st.annotation.StTag.TagDefinition;
import com.lacus.st.interfaces.StComponentInterface;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * Replace转换组件
 * 替换转换插件，检查给定字段中的字符串值并替换匹配的子字符串
 *
 * @author lacus
 */
@StComponent(
        type = StComponent.ComponentType.TRANSFORM,
        name = "replace_transform",
        displayName = "Replace字符串替换",
        description = "检查给定字段中的字符串值，并用给定的替换项替换与给定字符串字面量或正则表达式匹配的子字符串",
        version = "2.0.0",
        author = "lacus",
        connectorKey = "Replace"
)
@StTag({
        @TagDefinition(name = "替换配置", displayName = "替换配置", order = 1, description = "字符串替换相关配置")
})
@AutoService(StComponentInterface.class)
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
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
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"true", "false"}
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
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"true", "false"}
    )
    private Boolean replaceFirst;

    @Override
    public JSONObject buildTaskConfig(JSONObject connectionConfig, Long datasourceId) {
        JSONObject config = new JSONObject();
        putIfNotEmpty(config, "replace_field", connectionConfig.getString("replace_field"));
        putIfNotEmpty(config, "pattern", connectionConfig.getString("pattern"));
        putIfNotEmpty(config, "replacement", connectionConfig.getString("replacement"));
        putIfNotEmpty(config, "is_regex", connectionConfig.getBoolean("is_regex"));
        putIfNotEmpty(config, "replace_first", connectionConfig.getBoolean("replace_first"));
        return config;
    }
}
