package com.lacus.dataquality.flow.batch.transform;

import com.lacus.dataquality.config.TransformConfig;
import com.lacus.dataquality.context.SparkRuntimeEnvironment;
import com.lacus.dataquality.exception.DataQualityException;

import java.util.ArrayList;
import java.util.List;

/**
 * Transform 工厂类
 */
public class TransformFactory {

    private static final TransformFactory INSTANCE = new TransformFactory();

    private TransformFactory() {
    }

    public static TransformFactory getInstance() {
        return INSTANCE;
    }

    /**
     * 根据配置列表创建所有 Transform
     */
    public List<Transform> getTransforms(SparkRuntimeEnvironment env, List<TransformConfig> configs) {
        List<Transform> transforms = new ArrayList<>();
        if (configs == null || configs.isEmpty()) {
            return transforms;
        }
        for (TransformConfig config : configs) {
            Transform transform = createTransform(config);
            if (transform != null) {
                String error = transform.validateConfig();
                if (error != null) {
                    throw new DataQualityException("Transform config validation failed: " + error);
                }
                transforms.add(transform);
            }
        }
        return transforms;
    }

    private Transform createTransform(TransformConfig config) {
        String type = config.getType();
        if (type == null) {
            throw new DataQualityException("Transform type must not be null");
        }
        if ("SQL".equalsIgnoreCase(type.trim())) {
            return new SqlTransform(config.getConfig());
        }
        throw new DataQualityException("Unsupported transform type: " + type);
    }
}
