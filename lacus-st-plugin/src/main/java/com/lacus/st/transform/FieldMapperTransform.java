package com.lacus.st.transform;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.auto.service.AutoService;
import com.lacus.st.abstracts.AbstractStTransform;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.annotation.StField;
import com.lacus.st.interfaces.StComponentInterface;
import com.lacus.st.annotation.StTag;
import com.lacus.st.annotation.StTag.TagDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

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
        author = "lacus",
        connectorKey = "FieldMapper"
)
@StTag({
        @TagDefinition(name = "映射配置", displayName = "映射配置", order = 1, description = "字段映射相关配置")
})
@AutoService(StComponentInterface.class)
public class FieldMapperTransform extends AbstractStTransform {

    @Override
    public JSONObject buildTaskConfig(JSONObject connectionConfig, Long datasourceId) {
        JSONObject config = new JSONObject();
        JSONObject outputModel = connectionConfig.getJSONObject("outputModel");
        JSONArray relation = outputModel.getJSONArray("relation");
        JSONObject fields = new JSONObject();
        for (int i = 0; i < relation.size(); i++) {
            JSONObject item = relation.getJSONObject(i);
            String replace_from = item.getString("replace_from");
            String replace_to = item.getString("replace_to");
            fields.put(replace_from, replace_to);
        }
        config.put("field_mapper", fields);
        return config;
    }
}
