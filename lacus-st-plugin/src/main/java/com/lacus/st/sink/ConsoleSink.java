package com.lacus.st.sink;

import com.google.auto.service.AutoService;
import com.lacus.st.abstracts.AbstractStSink;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.annotation.StField;
import com.lacus.st.interfaces.StComponentInterface;
import com.lacus.st.annotation.StTag;
import com.lacus.st.annotation.StTag.TagDefinition;
import lombok.extern.slf4j.Slf4j;

/**
 * Console Sink组件
 * 控制台数据输出组件，用于将数据打印到控制台
 * 
 * @author lacus
 */
@Slf4j
@StComponent(
        type = StComponent.ComponentType.SINK,
        name = "console_sink",
        displayName = "Console控制台输出",
        description = "接收Source端传入的数据并打印到控制台，支持批同步和流同步两种模式",
        version = "2.0.0",
        author = "lacus"
)
@StTag({
        @TagDefinition(name = "输出配置", displayName = "输出配置", order = 1, description = "控制台输出相关配置")
})

@AutoService(StComponentInterface.class)
public class ConsoleSink extends AbstractStSink {

    @StField(
            tag = "输出配置",
            order = 1,
            required = false,
            enName = "log_print_data",
            cnName = "打印数据到日志",
            defaultValue = "true",
            description = "确定是否应在日志中打印数据的标志",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean logPrintData;

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

    @StField(
            tag = "输出配置",
            order = 3,
            required = false,
            enName = "output_format",
            cnName = "输出格式",
            defaultValue = "JSON",
            description = "控制台输出的数据格式",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"JSON", "TEXT", "TABLE"}
    )
    private String outputFormat;

    @StField(
            tag = "输出配置",
            order = 4,
            required = false,
            enName = "enable_row_index",
            cnName = "启用行索引",
            defaultValue = "true",
            description = "是否在输出中显示行索引信息",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean enableRowIndex;

    @StField(
            tag = "输出配置",
            order = 5,
            required = false,
            enName = "max_output_rows",
            cnName = "最大输出行数",
            defaultValue = "-1",
            description = "最大输出行数，-1表示无限制",
            placeHolder = "-1",
            formType = StField.FormType.NUMBER
    )
    private Integer maxOutputRows;

    @StField(
            tag = "输出配置",
            order = 6,
            required = false,
            enName = "output_prefix",
            cnName = "输出前缀",
            defaultValue = "",
            description = "每行输出数据的前缀",
            placeHolder = "[Console Output]",
            formType = StField.FormType.TEXT
    )
    private String outputPrefix;

    @Override
    protected boolean doCheckConnection() {
        try {
            // Console组件总是可用的，只需要验证参数配置
            if (logPrintDelayMs != null && logPrintDelayMs < 0) {
                log.error("Console Sink配置错误：log_print_delay_ms不能为负数");
                return false;
            }

            if (maxOutputRows != null && maxOutputRows < -1) {
                log.error("Console Sink配置错误：max_output_rows不能小于-1");
                return false;
            }

            // 验证输出格式
            if (outputFormat != null && !outputFormat.trim().isEmpty()) {
                String format = outputFormat.trim().toUpperCase();
                if (!"JSON".equals(format) && !"TEXT".equals(format) && !"TABLE".equals(format)) {
                    log.error("Console Sink配置错误：不支持的输出格式 {}，支持的格式：JSON, TEXT, TABLE", outputFormat);
                    return false;
                }
            }

            log.info("Console Sink连接检查成功");
            return true;
        } catch (Exception e) {
            log.error("Console Sink连接检查失败", e);
            return false;
        }
    }

    /**
     * 获取是否打印数据到日志
     */
    public Boolean getLogPrintData() {
        return logPrintData != null ? logPrintData : true;
    }

    /**
     * 设置是否打印数据到日志
     */
    public void setLogPrintData(Boolean logPrintData) {
        this.logPrintData = logPrintData;
    }

    /**
     * 获取打印延迟时间
     */
    public Integer getLogPrintDelayMs() {
        return logPrintDelayMs != null ? logPrintDelayMs : 0;
    }

    /**
     * 设置打印延迟时间
     */
    public void setLogPrintDelayMs(Integer logPrintDelayMs) {
        this.logPrintDelayMs = logPrintDelayMs;
    }

    /**
     * 获取输出格式
     */
    public String getOutputFormat() {
        return outputFormat != null ? outputFormat : "JSON";
    }

    /**
     * 设置输出格式
     */
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    /**
     * 获取是否启用行索引
     */
    public Boolean getEnableRowIndex() {
        return enableRowIndex != null ? enableRowIndex : true;
    }

    /**
     * 设置是否启用行索引
     */
    public void setEnableRowIndex(Boolean enableRowIndex) {
        this.enableRowIndex = enableRowIndex;
    }

    /**
     * 获取最大输出行数
     */
    public Integer getMaxOutputRows() {
        return maxOutputRows != null ? maxOutputRows : -1;
    }

    /**
     * 设置最大输出行数
     */
    public void setMaxOutputRows(Integer maxOutputRows) {
        this.maxOutputRows = maxOutputRows;
    }

    /**
     * 获取输出前缀
     */
    public String getOutputPrefix() {
        return outputPrefix != null ? outputPrefix : "";
    }

    /**
     * 设置输出前缀
     */
    public void setOutputPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
    }
}