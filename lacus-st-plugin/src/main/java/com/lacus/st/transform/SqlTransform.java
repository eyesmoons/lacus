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
 * Sql转换组件
 * SQL转换插件，使用SQL来转换给定的输入行
 *
 * @author lacus
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@StComponent(
        type = StComponent.ComponentType.TRANSFORM,
        name = "sql_transform",
        displayName = "Sql查询转换",
        description = "使用SQL来转换给定的输入行，支持基本的SQL函数和条件过滤操作",
        version = "2.0.0",
        author = "lacus",
        connectorKey = "Sql"
)
@StTag({
        @TagDefinition(name = "SQL配置", displayName = "SQL配置", order = 1, description = "SQL查询和转换相关配置")
})
@AutoService(StComponentInterface.class)
@Data
public class SqlTransform extends AbstractStTransform {

    @StField(
            tag = "SQL配置",
            order = 1,
            required = true,
            enName = "query",
            cnName = "查询SQL",
            description = "查询SQL语句，支持基本的函数和条件过滤操作",
            placeHolder = "select id, concat(name, '_') as name, age+1 as age from dual where id>0",
            formType = StField.FormType.TEXT_AREA
    )
    private String query;

    @Override
    public JSONObject buildTaskConfig(JSONObject connectionConfig, Long datasourceId) {
        JSONObject config = new JSONObject();
        putIfNotEmpty(config, "query", connectionConfig.getString("query"));
        return config;
    }
}
