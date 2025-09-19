package com.lacus.st.abstracts;

import com.lacus.st.annotation.StField;
import com.lacus.st.interfaces.StSourceInterface;
import lombok.extern.slf4j.Slf4j;

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
}
