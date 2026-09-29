package com.lacus.st.annotation;

import lombok.Getter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ST组件字段属性注解
 * 用于定义组件字段的各种属性，用于前端动态构建表单
 * 
 * @author lacus
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StField {
    
    /**
     * 标签，定义这个属性所属归类
     * 比如：基本信息，数据源配置，连接器配置
     */
    String tag() default "基本信息";
    
    /**
     * 是否必填
     */
    boolean required() default false;
    
    /**
     * 英文名称
     */
    String enName();
    
    /**
     * 中文名称
     */
    String cnName();
    
    /**
     * 默认值
     */
    String defaultValue() default "";
    
    /**
     * 输入框显示的提示信息
     */
    String placeHolder() default "";
    
    /**
     * 表单类型
     */
    FormType formType() default FormType.TEXT;

    String description() default "";
    
    /**
     * 字典类型
     */
    DictType dictType() default DictType.NONE;
    
    /**
     * 字典URL（当dictType为URL时使用）
     */
    String dictUrl() default "";
    
    /**
     * 字典枚举值（当dictType为ENUM时使用）
     */
    String[] dictEnum() default {};
    
    /**
     * 字段排序，用于控制字段在表单中的显示顺序
     * 数值越小越靠前，默认为0
     */
    int order() default 0;
    
    /**
     * 表单类型枚举
     */
    @Getter
    enum FormType {
        TEXT("text", "文本输入框"),
        TEXT_AREA("text_area", "多行文本输入框"),
        NUMBER("number", "数字输入框"),
        POSITIVE_NUMBER("positive_number", "正数输入框"),
        DATE("date", "日期选择器"),
        SINGLE_SELECT("single_select", "单选下拉框"),
        MULTI_SELECT("multi_select", "多选下拉框"),
        RADIO("radio", "单选按钮"),
        CHECKBOX("checkbox", "复选框"),
        PASSWORD("password", "密码输入框");
        
        private final String value;
        private final String description;
        
        FormType(String value, String description) {
            this.value = value;
            this.description = description;
        }

    }
    
    /**
     * 字典类型枚举
     */
    @Getter
    enum DictType {
        NONE("none", "无字典"),
        URL("url", "URL数据源"),
        ENUM("enum", "枚举值");
        
        private final String value;
        private final String description;
        
        DictType(String value, String description) {
            this.value = value;
            this.description = description;
        }

    }
}
