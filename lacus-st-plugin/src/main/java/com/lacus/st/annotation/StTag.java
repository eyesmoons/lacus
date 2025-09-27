package com.lacus.st.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ST组件标签注解
 * 用于定义组件的配置分组标签及其排序
 * 
 * @author lacus
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StTag {
    
    /**
     * 标签定义列表
     */
    TagDefinition[] value();
    
    /**
     * 标签定义
     */
    @interface TagDefinition {
        /**
         * 标签名称
         */
        String name();
        
        /**
         * 标签显示名称
         */
        String displayName() default "";
        
        /**
         * 标签排序，数值越小越靠前
         */
        int order();
        
        /**
         * 标签描述
         */
        String description() default "";
    }
}