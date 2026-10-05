package com.lacus.domain.lakeintelligence.task;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 图像分类任务处理器
 */
@Component
public class ClassifierTaskHandler implements TaskHandler {

    @Override
    public String getTaskType() {
        return "CLASSIFICATION";
    }

    @Override
    public Map<String, Object> getConfigSchema() {
        Map<String, Object> schema = new HashMap<>();
        schema.put("title", "图像分类训练配置");
        schema.put("type", "object");

        Map<String, Object> properties = new HashMap<>();

        // epochs
        Map<String, Object> epochs = new HashMap<>();
        epochs.put("type", "integer");
        epochs.put("title", "训练轮数");
        epochs.put("default", 10);
        epochs.put("minimum", 1);
        epochs.put("maximum", 1000);
        properties.put("epochs", epochs);

        // learning_rate
        Map<String, Object> learningRate = new HashMap<>();
        learningRate.put("type", "number");
        learningRate.put("title", "学习率");
        learningRate.put("default", 0.001);
        learningRate.put("minimum", 0.00001);
        learningRate.put("maximum", 1.0);
        properties.put("learning_rate", learningRate);

        // batch_size
        Map<String, Object> batchSize = new HashMap<>();
        batchSize.put("type", "integer");
        batchSize.put("title", "批次大小");
        batchSize.put("default", 32);
        batchSize.put("minimum", 1);
        batchSize.put("maximum", 512);
        properties.put("batch_size", batchSize);

        // n_classes
        Map<String, Object> nClasses = new HashMap<>();
        nClasses.put("type", "integer");
        nClasses.put("title", "类别数量");
        nClasses.put("default", 5);
        nClasses.put("minimum", 2);
        nClasses.put("maximum", 1000);
        properties.put("n_classes", nClasses);

        // device
        Map<String, Object> device = new HashMap<>();
        device.put("type", "string");
        device.put("title", "计算设备");
        device.put("default", "cpu");
        device.put("enum", new String[]{"cpu", "cuda"});
        properties.put("device", device);

        schema.put("properties", properties);

        // required fields
        schema.put("required", new String[]{"epochs", "learning_rate", "batch_size", "n_classes", "device"});

        return schema;
    }

    @Override
    public Map<String, Object> getDefaultParams() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("epochs", 10);
        defaults.put("learning_rate", 0.001);
        defaults.put("batch_size", 32);
        defaults.put("n_classes", 5);
        defaults.put("device", "cpu");
        return defaults;
    }

    @Override
    public Map<String, String> validateParams(Map<String, Object> params) {
        Map<String, String> errors = new HashMap<>();

        if (params == null) {
            errors.put("all", "参数不能为空");
            return errors;
        }

        // epochs
        Object epochs = params.get("epochs");
        if (epochs == null) {
            errors.put("epochs", "训练轮数不能为空");
        } else if (epochs instanceof Number) {
            int epochVal = ((Number) epochs).intValue();
            if (epochVal < 1 || epochVal > 1000) {
                errors.put("epochs", "训练轮数必须在 1-1000 之间");
            }
        } else {
            errors.put("epochs", "训练轮数必须为整数");
        }

        // learning_rate
        Object lr = params.get("learning_rate");
        if (lr == null) {
            errors.put("learning_rate", "学习率不能为空");
        } else if (lr instanceof Number) {
            double lrVal = ((Number) lr).doubleValue();
            if (lrVal <= 0 || lrVal > 1) {
                errors.put("learning_rate", "学习率必须在 0-1 之间");
            }
        } else {
            errors.put("learning_rate", "学习率必须为数字");
        }

        // batch_size
        Object batchSize = params.get("batch_size");
        if (batchSize == null) {
            errors.put("batch_size", "批次大小不能为空");
        } else if (batchSize instanceof Number) {
            int bsVal = ((Number) batchSize).intValue();
            if (bsVal < 1 || bsVal > 512) {
                errors.put("batch_size", "批次大小必须在 1-512 之间");
            }
        } else {
            errors.put("batch_size", "批次大小必须为整数");
        }

        // n_classes
        Object nClasses = params.get("n_classes");
        if (nClasses == null) {
            errors.put("n_classes", "类别数量不能为空");
        } else if (nClasses instanceof Number) {
            int ncVal = ((Number) nClasses).intValue();
            if (ncVal < 2 || ncVal > 1000) {
                errors.put("n_classes", "类别数量必须在 2-1000 之间");
            }
        } else {
            errors.put("n_classes", "类别数量必须为整数");
        }

        // device
        Object device = params.get("device");
        if (device == null || device.toString().isEmpty()) {
            errors.put("device", "计算设备不能为空");
        } else {
            String devStr = device.toString();
            if (!"cpu".equalsIgnoreCase(devStr) && !"cuda".equalsIgnoreCase(devStr)) {
                errors.put("device", "计算设备必须为 cpu 或 cuda");
            }
        }

        return errors;
    }
}
