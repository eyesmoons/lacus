//package com.lacus.st.controller;
//
//import com.lacus.st.annotation.StComponent;
//import com.lacus.st.loader.StComponentLoader;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
///**
// * ST组件管理控制器
// * 提供组件查询、配置获取等API接口
// *
// * @author lacus
// */
//@Slf4j
//@RestController
//@RequestMapping("/api/st/components")
//public class StComponentController {
//
//    /**
//     * 获取所有组件列表
//     */
//    @GetMapping("/list")
//    public Map<String, Object> getAllComponents() {
//        Map<String, Object> result = new HashMap<>();
//        try {
//            Map<String, Map<String, Object>> allMetadata = StComponentLoader.getAllComponentMetadata();
//            result.put("success", true);
//            result.put("data", allMetadata);
//            result.put("total", allMetadata.size());
//        } catch (Exception e) {
//            log.error("获取组件列表失败", e);
//            result.put("success", false);
//            result.put("message", "获取组件列表失败: " + e.getMessage());
//        }
//        return result;
//    }
//
//    /**
//     * 根据类型获取组件列表
//     */
//    @GetMapping("/list/{type}")
//    public Map<String, Object> getComponentsByType(@PathVariable String type) {
//        Map<String, Object> result = new HashMap<>();
//        try {
//            StComponent.ComponentType componentType = StComponent.ComponentType.valueOf(type.toUpperCase());
//            Set<String> componentNames = StComponentLoader.getComponentNamesByType(componentType);
//
//            Map<String, Map<String, Object>> components = new HashMap<>();
//            for (String name : componentNames) {
//                components.put(name, StComponentLoader.getComponentMetadata(name));
//            }
//
//            result.put("success", true);
//            result.put("data", components);
//            result.put("total", components.size());
//        } catch (Exception e) {
//            log.error("根据类型获取组件列表失败", e);
//            result.put("success", false);
//            result.put("message", "根据类型获取组件列表失败: " + e.getMessage());
//        }
//        return result;
//    }
//
//    /**
//     * 获取指定组件的详细信息
//     */
//    @GetMapping("/{componentName}")
//    public Map<String, Object> getComponentDetail(@PathVariable String componentName) {
//        Map<String, Object> result = new HashMap<>();
//        try {
//            if (!StComponentLoader.hasComponent(componentName)) {
//                result.put("success", false);
//                result.put("message", "组件不存在: " + componentName);
//                return result;
//            }
//
//            Map<String, Object> metadata = StComponentLoader.getComponentMetadata(componentName);
//            result.put("success", true);
//            result.put("data", metadata);
//        } catch (Exception e) {
//            log.error("获取组件详情失败", e);
//            result.put("success", false);
//            result.put("message", "获取组件详情失败: " + e.getMessage());
//        }
//        return result;
//    }
//
//    /**
//     * 获取指定组件的字段配置信息
//     */
//    @GetMapping("/{componentName}/fields")
//    public Map<String, Object> getComponentFields(@PathVariable String componentName) {
//        Map<String, Object> result = new HashMap<>();
//        try {
//            if (!StComponentLoader.hasComponent(componentName)) {
//                result.put("success", false);
//                result.put("message", "组件不存在: " + componentName);
//                return result;
//            }
//
//            Map<String, Object> metadata = StComponentLoader.getComponentMetadata(componentName);
//            Map<String, Object> fieldConfigs = (Map<String, Object>) metadata.get("fieldConfigs");
//
//            result.put("success", true);
//            result.put("data", fieldConfigs);
//        } catch (Exception e) {
//            log.error("获取组件字段配置失败", e);
//            result.put("success", false);
//            result.put("message", "获取组件字段配置失败: " + e.getMessage());
//        }
//        return result;
//    }
//
//    /**
//     * 验证组件配置
//     */
//    @PostMapping("/{componentName}/validate")
//    public Map<String, Object> validateComponentConfig(@PathVariable String componentName,
//                                                       @RequestBody Map<String, Object> config) {
//        Map<String, Object> result = new HashMap<>();
//        try {
//            if (!StComponentLoader.hasComponent(componentName)) {
//                result.put("success", false);
//                result.put("message", "组件不存在: " + componentName);
//                return result;
//            }
//
//            // 创建组件实例进行配置验证
//            com.lacus.st.interfaces.StComponentInterface component = StComponentLoader.createComponent(componentName);
//            com.lacus.st.interfaces.StComponentInterface.ValidationResult validationResult = component.validateConfig(config);
//
//            result.put("success", validationResult.isValid());
//            result.put("message", validationResult.getMessage());
//        } catch (Exception e) {
//            log.error("验证组件配置失败", e);
//            result.put("success", false);
//            result.put("message", "验证组件配置失败: " + e.getMessage());
//        }
//        return result;
//    }
//
//    /**
//     * 获取组件统计信息
//     */
//    @GetMapping("/stats")
//    public Map<String, Object> getComponentStats() {
//        Map<String, Object> result = new HashMap<>();
//        try {
//            Map<String, Object> stats = new HashMap<>();
//            stats.put("total", StComponentLoader.getComponentCount());
//
//            // 按类型统计
//            Map<String, Integer> typeStats = new HashMap<>();
//            for (StComponent.ComponentType type : StComponent.ComponentType.values()) {
//                Set<String> names = StComponentLoader.getComponentNamesByType(type);
//                typeStats.put(type.getValue(), names.size());
//            }
//            stats.put("byType", typeStats);
//
//            result.put("success", true);
//            result.put("data", stats);
//        } catch (Exception e) {
//            log.error("获取组件统计信息失败", e);
//            result.put("success", false);
//            result.put("message", "获取组件统计信息失败: " + e.getMessage());
//        }
//        return result;
//    }
//}
