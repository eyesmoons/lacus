package com.lacus.st.source;

import com.alibaba.fastjson2.JSONObject;
import com.google.auto.service.AutoService;
import com.lacus.st.abstracts.AbstractStSource;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.annotation.StField;
import com.lacus.st.annotation.StTag;
import com.lacus.st.annotation.StTag.TagDefinition;
import com.lacus.st.interfaces.StComponentInterface;
import lombok.extern.slf4j.Slf4j;

/**
 * Doris数据源组件
 *
 * @author lacus
 */
@Slf4j
@StComponent(
        type = StComponent.ComponentType.SOURCE,
        name = "doris_source",
        displayName = "Doris数据源",
        description = "从Apache Doris数据库读取数据，支持高性能分析查询",
        version = "2.0.0",
        author = "lacus",
        connectorKey = "Doris"
)
@StTag({
        @TagDefinition(name = "数据源配置", displayName = "数据源配置", order = 1, description = "数据源选择和连接配置"),
        @TagDefinition(name = "Doris配置", displayName = "Doris配置", order = 2, description = "Doris配置"),
        @TagDefinition(name = "性能配置", displayName = "性能配置", order = 3, description = "性能优化相关配置"),
})
@AutoService(StComponentInterface.class)
public class DorisSource extends AbstractStSource {

    // 数据源配置
    @StField(
            tag = "数据源配置",
            order = 1,
            required = true,
            enName = "datasourceId",
            cnName = "数据源",
            placeHolder = "请选择数据源",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.URL,
            dictUrl = "/metadata/datasource/list"
    )
    private String datasourceId;

    @StField(
            tag = "数据源配置",
            order = 2,
            required = true,
            enName = "database",
            cnName = "数据库",
            placeHolder = "请选择数据库",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.URL,
            dictUrl = "/metadata/db/list/{datasourceId}"
    )
    private String database;

    @StField(
            tag = "数据源配置",
            order = 3,
            required = true,
            enName = "table",
            cnName = "数据表",
            placeHolder = "请选择数据表",
            formType = StField.FormType.MULTI_SELECT,
            dictType = StField.DictType.URL,
            dictUrl = "/metadata/table/listTable"
    )
    private String table;

    @StField(
            tag = "Doris配置",
            order = 1,
            required = false,
            enName = "fenodes",
            cnName = "FE 地址",
            description = "数据过滤条件，SQL WHERE子句",
            placeHolder = "请输入FE 地址：fe_host:fe_http_port",
            formType = StField.FormType.TEXT
    )
    private String fenodes;

    // 性能配置
    @StField(
            tag = "性能配置",
            order = 1,
            required = false,
            enName = "doris_request_retries",
            cnName = "请求Doris FE的重试次数",
            defaultValue = "3",
            description = "请求Doris FE的重试次数",
            placeHolder = "请输入请求Doris FE的重试次数",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer dorisRequestRetries;

    @StField(
            tag = "性能配置",
            order = 2,
            required = false,
            enName = "doris_request_read_timeout_ms",
            cnName = "读取超时时间(毫秒)",
            defaultValue = "30000",
            description = "读取超时时间(毫秒)",
            placeHolder = "读取超时时间",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer dorisRequestReadTimeoutMs;

    @StField(
            tag = "性能配置",
            order = 3,
            required = false,
            enName = "doris_request_connect_timeout_ms",
            cnName = "doris连接超时时间(毫秒)",
            defaultValue = "30000",
            description = "doris连接超时时间(毫秒)",
            placeHolder = "doris连接超时时间",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer dorisRequestConnectTimeoutMs;

    @StField(
            tag = "性能配置",
            order = 4,
            required = false,
            enName = "doris_request_query_timeout_s",
            cnName = "查询超时时间",
            defaultValue = "3600",
            description = "查询超时时间(秒)",
            placeHolder = "3600",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer dorisRequestQueryTimeoutS;

    @Override
    protected boolean doCheckConnection() {
        return true;
    }

    @Override
    public JSONObject buildTaskConfig(JSONObject connectionConfig, Long datasourceId) {
        JSONObject config = new JSONObject();
        if (datasourceId != null) {
            putIfNotEmpty(config, "fenodes", connectionConfig.getString("fenodes"));
            putIfNotEmpty(config, "query-port", connectionConfig.getString("port"));
            putIfNotEmpty(config, "user", connectionConfig.getString("username"));
            putIfNotEmpty(config, "password", connectionConfig.getString("password"));
        }
        putIfNotEmpty(config, "doris.request.retries", connectionConfig.getInteger("doris_request_retries"));
        putIfNotEmpty(config, "doris.request.read.timeout.ms", connectionConfig.getInteger("doris_request_read_timeout_ms"));
        putIfNotEmpty(config, "doris.request.connect.timeout.ms", connectionConfig.getInteger("doris_request_connect_timeout_ms"));
        putIfNotEmpty(config, "doris.request.query.timeout.s", connectionConfig.getString("doris_request_query_timeout_s"));
        addTableList(config, connectionConfig, null);
        return config;
    }
}
