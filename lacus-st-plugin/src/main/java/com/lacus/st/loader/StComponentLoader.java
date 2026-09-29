package com.lacus.st.loader;

import com.lacus.st.annotation.StComponent;
import com.lacus.st.interfaces.StComponentInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ST组件加载器
 * 负责在项目启动时自动发现和加载所有ST组件
 *
 * @author lacus
 */
@Slf4j
@Component
public class StComponentLoader {

    private static final Map<String, StComponentInterface> COMPONENT_MAP = new ConcurrentHashMap<>();
    private static final Map<String, Class<? extends StComponentInterface>> COMPONENT_CLASS_MAP = new ConcurrentHashMap<>();
    private static final Map<String, Map<String, Object>> COMPONENT_METADATA_MAP = new ConcurrentHashMap<>();

    private static volatile boolean initialized = false;

    /**
     * 初始化组件加载器
     */
    public synchronized void initialize() {
        if (initialized) {
            return;
        }

        try {
            log.info("开始初始化ST组件加载器...");

            // 使用ServiceLoader加载所有组件
            ServiceLoader<StComponentInterface> serviceLoader = ServiceLoader.load(StComponentInterface.class);

            for (StComponentInterface component : serviceLoader) {
                registerComponent(component);
            }

            initialized = true;
            log.info("ST组件加载器初始化完成，共加载 {} 个组件", COMPONENT_MAP.size());

        } catch (Exception e) {
            log.error("ST组件加载器初始化失败", e);
            throw new RuntimeException("ST组件加载器初始化失败", e);
        }
    }

    /**
     * 注册组件
     *
     * @param component 组件实例
     */
    private void registerComponent(StComponentInterface component) {
        try {
            StComponent annotation = component.getClass().getAnnotation(StComponent.class);
            if (annotation == null) {
                log.warn("组件 {} 缺少@StComponent注解，跳过注册", component.getClass().getName());
                return;
            }

            String componentName = annotation.name();
            if (COMPONENT_MAP.containsKey(componentName)) {
                log.warn("组件名称 {} 已存在，跳过注册", componentName);
                return;
            }

            // 注册组件实例
            COMPONENT_MAP.put(componentName, component);

            // 注册组件类
            COMPONENT_CLASS_MAP.put(componentName, component.getClass());

            // 注册组件元数据
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("name", annotation.name());
            metadata.put("displayName", annotation.displayName());
            metadata.put("description", annotation.description());
            metadata.put("version", annotation.version());
            metadata.put("author", annotation.author());
            metadata.put("type", annotation.type().getValue());
            metadata.put("className", component.getClass().getName());
            metadata.put("connectorKey", annotation.connectorKey());

            // 获取字段配置信息
            Map<String, Object> fieldConfigs = getFieldConfigs(component.getClass());
            metadata.put("fieldConfigs", fieldConfigs);

            COMPONENT_METADATA_MAP.put(componentName, metadata);

            log.info("成功注册组件: {} ({})", componentName, annotation.displayName());

        } catch (Exception e) {
            log.error("注册组件失败: {}", component.getClass().getName(), e);
        }
    }

    /**
     * 获取字段配置信息
     *
     * @param componentClass 组件类
     * @return 字段配置信息
     */
    private Map<String, Object> getFieldConfigs(Class<?> componentClass) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取标签配置信息
        Map<String, Object> tagConfigs = getTagConfigs(componentClass);
        result.put("tags", tagConfigs);
        
        // 获取字段配置信息
        Map<String, Object> fieldConfigs = new HashMap<>();

        java.lang.reflect.Field[] fields = componentClass.getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            com.lacus.st.annotation.StField fieldAnnotation = field.getAnnotation(com.lacus.st.annotation.StField.class);
            if (fieldAnnotation != null) {
                Map<String, Object> fieldConfig = new HashMap<>();
                fieldConfig.put("tag", fieldAnnotation.tag());
                fieldConfig.put("order", fieldAnnotation.order()); // 添加字段order属性
                fieldConfig.put("required", fieldAnnotation.required());
                fieldConfig.put("enName", fieldAnnotation.enName());
                fieldConfig.put("cnName", fieldAnnotation.cnName());
                fieldConfig.put("defaultValue", fieldAnnotation.defaultValue());
                fieldConfig.put("placeHolder", fieldAnnotation.placeHolder());
                fieldConfig.put("description", fieldAnnotation.description()); // 添加描述
                fieldConfig.put("formType", fieldAnnotation.formType().getValue());
                fieldConfig.put("dictType", fieldAnnotation.dictType().getValue());
                fieldConfig.put("dictUrl", fieldAnnotation.dictUrl());
                fieldConfig.put("dictEnum", fieldAnnotation.dictEnum());
                fieldConfig.put("fieldType", field.getType().getSimpleName());

                fieldConfigs.put(field.getName(), fieldConfig);
            }
        }
        
        result.put("fields", fieldConfigs);
        return result;
    }
    
    /**
     * 获取标签配置信息
     *
     * @param componentClass 组件类
     * @return 标签配置信息
     */
    private Map<String, Object> getTagConfigs(Class<?> componentClass) {
        Map<String, Object> tagConfigs = new HashMap<>();
        
        com.lacus.st.annotation.StTag stTagAnnotation = componentClass.getAnnotation(com.lacus.st.annotation.StTag.class);
        if (stTagAnnotation != null) {
            com.lacus.st.annotation.StTag.TagDefinition[] tagDefinitions = stTagAnnotation.value();
            for (com.lacus.st.annotation.StTag.TagDefinition tagDef : tagDefinitions) {
                Map<String, Object> tagConfig = new HashMap<>();
                tagConfig.put("name", tagDef.name());
                tagConfig.put("displayName", tagDef.displayName());
                tagConfig.put("order", tagDef.order()); // 添加标签order属性
                tagConfig.put("description", tagDef.description());
                
                tagConfigs.put(tagDef.name(), tagConfig);
            }
        }
        
        return tagConfigs;
    }

    /**
     * 获取组件实例
     *
     * @param componentName 组件名称
     * @return 组件实例
     */
    public StComponentInterface getComponent(String componentName) {
        if (!initialized) {
            initialize();
        }
        return COMPONENT_MAP.get(componentName);
    }

    /**
     * 创建组件实例
     *
     * @param componentName 组件名称
     * @return 组件实例
     */
    public StComponentInterface createComponent(String componentName) {
        if (!initialized) {
            initialize();
        }

        Class<? extends StComponentInterface> componentClass = COMPONENT_CLASS_MAP.get(componentName);
        if (componentClass == null) {
            throw new IllegalArgumentException("未找到组件: " + componentName);
        }

        try {
            Constructor<? extends StComponentInterface> constructor = componentClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            log.error("创建组件实例失败: {}", componentName, e);
            throw new RuntimeException("创建组件实例失败: " + componentName, e);
        }
    }

    /**
     * 获取所有组件名称
     *
     * @return 组件名称列表
     */
    public Set<String> getAllComponentNames() {
        if (!initialized) {
            initialize();
        }
        return new HashSet<>(COMPONENT_MAP.keySet());
    }

    /**
     * 根据类型获取组件名称
     *
     * @param type 组件类型
     * @return 组件名称列表
     */
    public Set<String> getComponentNamesByType(StComponent.ComponentType type) {
        if (!initialized) {
            initialize();
        }

        Set<String> result = new HashSet<>();
        for (Map.Entry<String, Map<String, Object>> entry : COMPONENT_METADATA_MAP.entrySet()) {
            if (type.getValue().equals(entry.getValue().get("type"))) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    /**
     * 获取组件元数据
     *
     * @param componentName 组件名称
     * @return 组件元数据
     */
    public Map<String, Object> getComponentMetadata(String componentName) {
        if (!initialized) {
            initialize();
        }
        return COMPONENT_METADATA_MAP.get(componentName);
    }

    /**
     * 获取所有组件元数据
     *
     * @return 所有组件元数据
     */
    public Map<String, Map<String, Object>> getAllComponentMetadata() {
        if (!initialized) {
            initialize();
        }
        return new HashMap<>(COMPONENT_METADATA_MAP);
    }

    /**
     * 检查组件是否存在
     *
     * @param componentName 组件名称
     * @return 是否存在
     */
    public boolean hasComponent(String componentName) {
        if (!initialized) {
            initialize();
        }
        return COMPONENT_MAP.containsKey(componentName);
    }

    /**
     * 获取组件数量
     *
     * @return 组件数量
     */
    public int getComponentCount() {
        if (!initialized) {
            initialize();
        }
        return COMPONENT_MAP.size();
    }
}
