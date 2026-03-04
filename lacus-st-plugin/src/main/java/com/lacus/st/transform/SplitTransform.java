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

/**
 * Split转换组件
 * 拆分转换插件，拆分一个字段为多个字段
 *
 * @author lacus
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@StComponent(
        type = StComponent.ComponentType.TRANSFORM,
        name = "split_transform",
        displayName = "Split字段拆分",
        description = "拆分一个字段为多个字段，支持指定分隔符和输出字段名",
        version = "2.0.0",
        author = "lacus",
        connectorKey = "Split"
)
@StTag({
        @TagDefinition(name = "拆分配置", displayName = "拆分配置", order = 1, description = "字段拆分相关配置")
})
@AutoService(StComponentInterface.class)
@Data
public class SplitTransform extends AbstractStTransform {

    @StField(
            tag = "拆分配置",
            order = 1,
            required = true,
            enName = "separator",
            cnName = "分隔符",
            description = "拆分内容的分隔符",
            placeHolder = " ",
            formType = StField.FormType.TEXT
    )
    private String separator;

    @Override
    public JSONObject buildTaskConfig(JSONObject connectionConfig, Long datasourceId) {
        JSONObject config = new JSONObject();
        putIfNotEmpty(config, "separator", connectionConfig.getString("separator"));
        JSONObject outputModel = connectionConfig.getJSONObject("outputModel");
        JSONArray relation = outputModel.getJSONArray("relation");
        String split_field = null;
        JSONArray output_fields = null;
        for (int i = 0; i < relation.size(); i++) {
            JSONObject item = relation.getJSONObject(i);
            split_field = item.getString("split_from");
            output_fields = item.getJSONArray("split_to");
        }
        if (ObjectUtils.isNotEmpty(split_field)) {
            config.put("split_field", split_field);
        }
        if (ObjectUtils.isNotEmpty(output_fields)) {
            config.put("output_fields", output_fields);
        }
        return config;
    }
}
