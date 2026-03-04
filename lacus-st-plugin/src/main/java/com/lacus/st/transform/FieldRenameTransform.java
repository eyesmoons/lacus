package com.lacus.st.transform;

import com.alibaba.fastjson2.JSONArray;
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
import org.apache.commons.lang3.ObjectUtils;

import java.util.Objects;

/**
 * FieldRename转换组件
 * 用于重命名字段，支持大小写转换、前缀后缀和正则替换
 *
 * @author lacus
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@StComponent(
        type = StComponent.ComponentType.TRANSFORM,
        name = "field_rename_transform",
        displayName = "FieldRename字段重命名",
        description = "用于重命名字段，支持大小写转换、前缀后缀添加和正则表达式替换",
        version = "2.0.0",
        author = "lacus",
        connectorKey = "FieldRename"
)
@StTag({
        @TagDefinition(name = "重命名配置", displayName = "重命名配置", order = 1, description = "字段重命名相关配置")
})

@AutoService(StComponentInterface.class)
@Data
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
            dictEnum = {"UPPER", "LOWER"}
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

    @Override
    public JSONObject buildTaskConfig(JSONObject connectionConfig, Long datasourceId) {
        JSONObject config = new JSONObject();
        putIfNotEmpty(config, "convert_case", connectionConfig.getString("convert_case"));
        putIfNotEmpty(config, "prefix", connectionConfig.getString("prefix"));
        putIfNotEmpty(config, "suffix", connectionConfig.getString("suffix"));
        JSONObject outputModel = connectionConfig.getJSONObject("outputModel");
        JSONArray relation = outputModel.getJSONArray("relation");
        JSONArray replacements_with_regex = new JSONArray();
        for (int i = 0; i < relation.size(); i++) {
            JSONObject item = relation.getJSONObject(i);
            String replace_from = item.getString("replace_from");
            String replace_to = item.getString("replace_to");
            if (!Objects.equals(replace_from, replace_to)) {
                JSONObject regex = new JSONObject();
                regex.put("replace_from", replace_from);
                regex.put("replace_to", replace_to);
                replacements_with_regex.add(regex);
            }
        }
        if (ObjectUtils.isNotEmpty(replacements_with_regex)) {
            config.put("replacements_with_regex", replacements_with_regex);
        }
        return config;
    }
}
