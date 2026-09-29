package com.lacus.st.transform;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.auto.service.AutoService;
import com.lacus.st.abstracts.AbstractStTransform;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.interfaces.StComponentInterface;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Metadata转换组件
 * 元数据转换插件，用于将元数据字段添加到数据中
 *
 * @author lacus
 */
@Setter
@Slf4j
@StComponent(
        type = StComponent.ComponentType.TRANSFORM,
        name = "metadata_transform",
        displayName = "Metadata元数据",
        description = "将元数据字段添加到数据中，包括数据库名、表名、行类型等信息",
        version = "2.0.0",
        author = "lacus",
        connectorKey = "Metadata"
)
@AutoService(StComponentInterface.class)
public class MetadataTransform extends AbstractStTransform {

    @Override
    public JSONObject buildTaskConfig(JSONObject connectionConfig, Long datasourceId) {
        JSONObject config = new JSONObject();
        JSONObject outputModel = connectionConfig.getJSONObject("outputModel");
        JSONArray fields = outputModel.getJSONArray("fields");
        JSONObject metadata_fields = new JSONObject();
        for (int i = 0; i < fields.size(); i++) {
            String field = fields.getString(i);
            metadata_fields.put(field, field);
        }
        config.putIfAbsent("metadata_fields", metadata_fields);
        return config;
    }
}
