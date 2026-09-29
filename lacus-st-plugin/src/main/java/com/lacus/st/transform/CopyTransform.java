package com.lacus.st.transform;

import com.alibaba.fastjson2.JSONObject;
import com.google.auto.service.AutoService;
import com.lacus.st.abstracts.AbstractStTransform;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.annotation.StTag;
import com.lacus.st.annotation.StTag.TagDefinition;
import com.lacus.st.interfaces.StComponentInterface;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

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
        author = "lacus",
        connectorKey = "Copy"
)
@StTag({
        @TagDefinition(name = "复制配置", displayName = "复制配置", order = 1, description = "字段复制相关配置"),
        @TagDefinition(name = "输出模型", displayName = "输出模型", order = 2, description = "输出模型")
})
@AutoService(StComponentInterface.class)
public class CopyTransform extends AbstractStTransform {

    @Override
    public JSONObject buildTaskConfig(JSONObject connectionConfig, Long datasourceId) {
        JSONObject config = new JSONObject();
        JSONObject outputModel = connectionConfig.getJSONObject("outputModel");
        JSONObject relation = outputModel.getJSONObject("relation");
        JSONObject fields = new JSONObject();
        for (Map.Entry<String, Object> entry : relation.entrySet()) {
            String newField = entry.getKey();
            String oldField = entry.getValue().toString();
            if (!Objects.equals(newField, oldField)) {
                fields.put(newField, oldField);
            }
        }
        config.put("fields", fields);
        return config;
    }
}
