package com.lacus.st.interfaces;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;

import java.util.Map;

/**
 * ST组件基础接口
 * 所有ST组件都需要实现此接口
 *
 * @author lacus
 */
public interface StComponentInterface {

    /**
     * 初始化组件
     * @param config 组件配置参数
     * @return 是否初始化成功
     */
    boolean initialize(Map<String, Object> config);

    /**
     * 验证配置参数
     * @param config 配置参数
     * @return 验证结果
     */
    ValidationResult validateConfig(Map<String, Object> config);

    /**
     * 获取组件元数据信息
     * @return 组件元数据
     */
    ComponentMetadata getMetadata();

    /**
     * 组件状态枚举
     */
    @Getter
    enum ComponentStatus {
        INITIALIZED("已初始化"),
        ERROR("错误");

        private final String description;

        ComponentStatus(String description) {
            this.description = description;
        }

    }

    /**
     * 验证结果
     */
    @Getter
    class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, "验证通过");
        }

        public static ValidationResult failure(String message) {
            return new ValidationResult(false, message);
        }

    }

    /**
     * 组件元数据
     */
    @Getter
    class ComponentMetadata {
        // Getters
        private final String name;
        private final String displayName;
        private final String description;
        private final String version;
        private final String author;
        private final String type;

        public ComponentMetadata(String name, String displayName, String description,
                               String version, String author, String type) {
            this.name = name;
            this.displayName = displayName;
            this.description = description;
            this.version = version;
            this.author = author;
            this.type = type;
        }

    }

    /**
     * 构建组件的任务配置JSON
     * @param connectionConfig 连接配置参数
     * @param datasourceId 数据源ID（可选）
     * @return 组件特定的配置JSON
     */
    JSONObject buildTaskConfig(JSONObject connectionConfig, Long datasourceId);
}
