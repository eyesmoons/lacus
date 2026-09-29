package com.lacus.st.abstracts;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.lacus.st.annotation.StField;
import com.lacus.st.interfaces.StSourceInterface;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * ST数据源组件抽象基类
 * 提供数据源组件的通用功能实现
 *
 * @author lacus
 */
@Slf4j
public abstract class AbstractStSource extends AbstractStComponent implements StSourceInterface {

    @StField(
            tag = "任务配置",
            required = true,
            enName = "plugin_input",
            cnName = "输入表名",
            description = "当未指定 plugin_output 时，此插件处理的数据将不会被注册为可由其他插件直接访问的数据集 (dataStream/dataset)，或称为临时表 (table)。\n" +
                    "当指定了 plugin_output 时，此插件处理的数据将被注册为可由其他插件直接访问的数据集 (dataStream/dataset)，或称为临时表 (table)。此处注册的数据集 (dataStream/dataset) 可通过指定 plugin_input 直接被其他插件访问。",
            placeHolder = "请输入表名",
            formType = StField.FormType.TEXT
    )
    private String pluginInput;

    @StField(
            tag = "任务配置",
            required = true,
            enName = "plugin_output",
            cnName = "输出表名",
            description = "当未指定 plugin_output 时，此插件处理的数据将不会被注册为可由其他插件直接访问的数据集 (dataStream/dataset)，或称为临时表 (table)。\n" +
                    "当指定了 plugin_output 时，此插件处理的数据将被注册为可由其他插件直接访问的数据集 (dataStream/dataset)，或称为临时表 (table)。此处注册的数据集 (dataStream/dataset) 可通过指定 plugin_input 直接被其他插件访问。",
            placeHolder = "请输入输出表名",
            formType = StField.FormType.TEXT
    )
    private String pluginOutput;

    @StField(
            tag = "任务配置",
            required = false,
            enName = "parallelism",
            cnName = "并行度",
            description = "当未指定 parallelism 时，默认使用环境中的 parallelism。当指定了 parallelism 时，将覆盖环境中的 parallelism 设置。",
            placeHolder = "请输入并行度",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer parallelism;


    @Override
    public boolean checkConnection() {
        return doCheckConnection();
    }

    @Override
    public Map<String, Object> getSourceInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("componentName", getClass().getSimpleName());
        return info;
    }

    /**
     * 子类实现具体的连接检查逻辑
     *
     * @return 连接是否正常
     */
    protected abstract boolean doCheckConnection();

    protected JSONArray getDbTables(JSONObject connectionConfig) {
        JSONArray dbTables = new JSONArray();
        JSONObject outputModel = connectionConfig.getJSONObject("outputModel");
        String database = connectionConfig.getString("database");
        JSONArray tables = outputModel.getJSONArray("tables");
        for (Object table : tables) {
            dbTables.add(database + "." + table.toString());
        }
        return dbTables;
    }

    protected void addTableList(JSONObject config, JSONObject connectionConfig, String whereCondition) {
        JSONArray table_list = new JSONArray();
        JSONObject outputModel = connectionConfig.getJSONObject("outputModel");
        JSONObject tableFields = outputModel.getJSONObject("tableFields");
        String database = connectionConfig.getString("database");
        for (Map.Entry<String, Object> entry : tableFields.entrySet()) {
            String table = entry.getKey();
            JSONArray fields = (JSONArray) entry.getValue();
            JSONObject tableItem = new JSONObject();
            String dbTable = database + "." + table;
            tableItem.put("table_path", dbTable);
            String fieldStr = String.join(",", fields.toJavaList(String.class));
            String query = "select " + fieldStr + " from " + dbTable;
            if (ObjectUtils.isNotEmpty(whereCondition)) {
                query += " where " + whereCondition;
            }
            tableItem.put("query", query);
            table_list.add(tableItem);
        }
        config.put("table_list", table_list);
    }
}
