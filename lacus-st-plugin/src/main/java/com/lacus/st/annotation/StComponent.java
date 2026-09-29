package com.lacus.st.annotation;

import lombok.Getter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ST组件注解
 * 用于标识ST组件，支持SPI自动发现和加载
 * 
 * @author lacus
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StComponent {
    
    /**
     * 组件类型
     */
    ComponentType type();
    
    /**
     * 组件名称（唯一标识）
     */
    String name();
    
    /**
     * 组件显示名称
     */
    String displayName();
    
    /**
     * 组件描述
     */
    String description() default "";
    
    /**
     * 组件版本
     */
    String version() default "1.0.0";
    
    /**
     * 组件作者
     */
    String author() default "";

    /**
     * DAG 中连接器名称（生成 JSON 时 source/transform/sink 下的 key）。
     * 为空则使用 name。例如 Jdbc、Replace、Kafka，与 SeaTunnel 配置名一致。
     */
    String connectorKey() default "";
    
    /**
     * 组件类型枚举
     */
    @Getter
    enum ComponentType {
        SOURCE("source", "输入组件"),
        SINK("sink", "输出组件"),
        TRANSFORM("transform", "转换组件");
        
        private final String value;
        private final String description;
        
        ComponentType(String value, String description) {
            this.value = value;
            this.description = description;
        }

    }
}
