package com.lacus.st.sink;

import com.alibaba.fastjson2.JSONObject;
import com.google.auto.service.AutoService;
import com.lacus.st.abstracts.AbstractStSink;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.annotation.StField;
import com.lacus.st.annotation.StTag;
import com.lacus.st.annotation.StTag.TagDefinition;
import com.lacus.st.interfaces.StComponentInterface;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * Console Sink组件
 * 控制台数据输出组件，用于将数据打印到控制台
 *
 * @author lacus
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
@StComponent(
        type = StComponent.ComponentType.SINK,
        name = "console_sink",
        displayName = "Console控制台输出",
        description = "接收Source端传入的数据并打印到控制台，支持批同步和流同步两种模式",
        version = "2.0.0",
        author = "lacus",
        connectorKey = "Console"
)
@StTag({
        @TagDefinition(name = "输出配置", displayName = "输出配置", order = 1, description = "控制台输出相关配置")
})

@AutoService(StComponentInterface.class)
public class ConsoleSink extends AbstractStSink {

    /**
     * -- SETTER --
     * 设置是否打印数据到日志
     */
    @StField(
            tag = "输出配置",
            order = 1,
            required = false,
            enName = "log_print_data",
            cnName = "打印数据到日志",
            defaultValue = "true",
            description = "确定是否应在日志中打印数据的标志",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"false", "true"}
    )
    private Boolean logPrintData;

    /**
     * -- SETTER --
     * 设置打印延迟时间
     */
    @StField(
            tag = "输出配置",
            order = 2,
            required = false,
            enName = "log_print_delay_ms",
            cnName = "打印延迟时间(毫秒)",
            defaultValue = "0",
            description = "将每个数据项打印到日志之间的延迟(以毫秒为单位)",
            placeHolder = "0",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer logPrintDelayMs;

    @Override
    protected boolean doCheckConnection() {
        return true;
    }

    @Override
    public JSONObject buildTaskConfig(JSONObject connectionConfig, Long datasourceId) {
        JSONObject config = new JSONObject();
        putIfNotEmpty(config, "log.print.data", connectionConfig.getBoolean("logPrintData"));
        putIfNotEmpty(config, "log.print.delay.ms", connectionConfig.getBoolean("logPrintDelayMs"));
        return config;
    }
}
