package com.lacus.st.transform;

import com.google.auto.service.AutoService;
import com.lacus.st.abstracts.AbstractStTransform;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.annotation.StField;
import com.lacus.st.interfaces.StComponentInterface;
import com.lacus.st.annotation.StTag;
import com.lacus.st.annotation.StTag.TagDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Split转换组件
 * 拆分转换插件，拆分一个字段为多个字段
 * 
 * @author lacus
 */
@Slf4j
@StComponent(
        type = StComponent.ComponentType.TRANSFORM,
        name = "split_transform",
        displayName = "Split字段拆分",
        description = "拆分一个字段为多个字段，支持指定分隔符和输出字段名",
        version = "2.0.0",
        author = "lacus"
)
@StTag({
        @TagDefinition(name = "拆分配置", displayName = "拆分配置", order = 1, description = "字段拆分相关配置")
})

@AutoService(StComponentInterface.class)
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

    @StField(
            tag = "拆分配置",
            order = 2,
            required = true,
            enName = "split_field",
            cnName = "需要拆分的字段",
            description = "指定需要拆分的字段名",
            placeHolder = "name",
            formType = StField.FormType.TEXT
    )
    private String splitField;

    @StField(
            tag = "拆分配置",
            order = 3,
            required = true,
            enName = "output_fields",
            cnName = "输出字段列表",
            description = "拆分后的结果字段名列表，用逗号分隔",
            placeHolder = "first_name,last_name",
            formType = StField.FormType.TEXT
    )
    private String outputFields;

    @StField(
            tag = "拆分配置",
            order = 4,
            required = false,
            enName = "max_split",
            cnName = "最大拆分数量",
            defaultValue = "-1",
            description = "最大拆分数量，-1表示不限制",
            placeHolder = "-1",
            formType = StField.FormType.NUMBER
    )
    private Integer maxSplit;

    @StField(
            tag = "拆分配置",
            order = 5,
            required = false,
            enName = "trim_results",
            cnName = "去除空白字符",
            defaultValue = "true",
            description = "是否去除拆分结果的前后空白字符",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean trimResults;

    @StField(
            tag = "拆分配置",
            order = 6,
            required = false,
            enName = "ignore_empty",
            cnName = "忽略空值",
            defaultValue = "false",
            description = "是否忽略拆分后的空值部分",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean ignoreEmpty;

    @StField(
            tag = "拆分配置",
            order = 7,
            required = false,
            enName = "default_value",
            cnName = "默认值",
            defaultValue = "",
            description = "当拆分结果数量不足时，使用的默认值",
            placeHolder = "",
            formType = StField.FormType.TEXT
    )
    private String defaultValue;

    @StField(
            tag = "拆分配置",
            order = 8,
            required = false,
            enName = "keep_original_field",
            cnName = "保留原字段",
            defaultValue = "true",
            description = "是否保留原始字段",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean keepOriginalField;

    public boolean doCheckConnection() {
        try {
            // 验证必填字段
            if (separator == null) {
                log.error("Split转换组件配置错误：separator不能为null");
                return false;
            }

            if (splitField == null || splitField.trim().isEmpty()) {
                log.error("Split转换组件配置错误：split_field不能为空");
                return false;
            }

            if (outputFields == null || outputFields.trim().isEmpty()) {
                log.error("Split转换组件配置错误：output_fields不能为空");
                return false;
            }

            // 验证输出字段列表
            List<String> outputFieldsList = getOutputFieldsList();
            if (outputFieldsList.isEmpty()) {
                log.error("Split转换组件配置错误：output_fields解析后为空列表");
                return false;
            }

            // 检查输出字段名是否有重复
            long distinctCount = outputFieldsList.stream().distinct().count();
            if (distinctCount != outputFieldsList.size()) {
                log.error("Split转换组件配置错误：output_fields中存在重复的字段名");
                return false;
            }

            // 验证maxSplit参数
            int maxSplitCount = maxSplit != null ? maxSplit : -1;
            if (maxSplitCount > 0 && maxSplitCount < outputFieldsList.size()) {
                log.warn("Split转换组件警告：max_split({}) 小于 output_fields的数量({})", maxSplitCount, outputFieldsList.size());
            }

            log.info("Split转换组件连接检查成功，拆分字段：{}，输出字段数量：{}", splitField, outputFieldsList.size());
            return true;
        } catch (Exception e) {
            log.error("Split转换组件连接检查失败", e);
            return false;
        }
    }

    /**
     * 获取输出字段列表
     */
    public List<String> getOutputFieldsList() {
        if (outputFields == null || outputFields.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        
        String[] fields = outputFields.split(",");
        return java.util.Arrays.stream(fields)
                .map(String::trim)
                .filter(field -> !field.isEmpty())
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 执行字段拆分
     */
    public String[] performSplit(String input) {
        if (input == null) {
            return new String[0];
        }

        int maxSplitCount = maxSplit != null ? maxSplit : -1;
        String[] splitResults;
        
        if (maxSplitCount > 0) {
            splitResults = input.split(java.util.regex.Pattern.quote(separator), maxSplitCount);
        } else {
            splitResults = input.split(java.util.regex.Pattern.quote(separator));
        }

        // 处理拆分结果
        java.util.List<String> processedResults = new java.util.ArrayList<>();
        
        for (String result : splitResults) {
            // 去除空白字符
            if (trimResults != null && trimResults) {
                result = result.trim();
            }
            
            // 忽略空值
            if (ignoreEmpty != null && ignoreEmpty && result.isEmpty()) {
                continue;
            }
            
            processedResults.add(result);
        }

        return processedResults.toArray(new String[0]);
    }

    /**
     * 填充输出字段值
     */
    public String[] fillOutputFields(String[] splitResults) {
        List<String> outputFieldsList = getOutputFieldsList();
        String[] result = new String[outputFieldsList.size()];
        String defaultVal = defaultValue != null ? defaultValue : "";
        
        for (int i = 0; i < outputFieldsList.size(); i++) {
            if (i < splitResults.length) {
                result[i] = splitResults[i];
            } else {
                result[i] = defaultVal;
            }
        }
        
        return result;
    }

    /**
     * 获取分隔符
     */
    public String getSeparator() {
        return separator;
    }

    /**
     * 设置分隔符
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    /**
     * 获取拆分字段
     */
    public String getSplitField() {
        return splitField;
    }

    /**
     * 设置拆分字段
     */
    public void setSplitField(String splitField) {
        this.splitField = splitField;
    }

    /**
     * 获取输出字段配置
     */
    public String getOutputFields() {
        return outputFields;
    }

    /**
     * 设置输出字段配置
     */
    public void setOutputFields(String outputFields) {
        this.outputFields = outputFields;
    }

    /**
     * 获取最大拆分数量
     */
    public Integer getMaxSplit() {
        return maxSplit;
    }

    /**
     * 设置最大拆分数量
     */
    public void setMaxSplit(Integer maxSplit) {
        this.maxSplit = maxSplit;
    }

    /**
     * 获取是否去除空白字符
     */
    public Boolean getTrimResults() {
        return trimResults != null ? trimResults : true;
    }

    /**
     * 设置是否去除空白字符
     */
    public void setTrimResults(Boolean trimResults) {
        this.trimResults = trimResults;
    }

    /**
     * 获取是否忽略空值
     */
    public Boolean getIgnoreEmpty() {
        return ignoreEmpty != null ? ignoreEmpty : false;
    }

    /**
     * 设置是否忽略空值
     */
    public void setIgnoreEmpty(Boolean ignoreEmpty) {
        this.ignoreEmpty = ignoreEmpty;
    }

    /**
     * 获取默认值
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * 设置默认值
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * 获取是否保留原字段
     */
    public Boolean getKeepOriginalField() {
        return keepOriginalField != null ? keepOriginalField : true;
    }

    /**
     * 设置是否保留原字段
     */
    public void setKeepOriginalField(Boolean keepOriginalField) {
        this.keepOriginalField = keepOriginalField;
    }
}