package com.lacus.domain.lakeintelligence.task;

import java.util.Map;

/**
 * 任务处理器接口
 *
 * <p>不同类型的训练任务（图像相似度、目标检测等）通过实现此接口提供各自的配置 Schema 和业务逻辑。</p>
 */
public interface TaskHandler {

    /**
     * 获取任务类型标识（如 IMAGE_SIMILARITY）
     */
    String getTaskType();

    /**
     * 获取任务配置 Schema（JSON Schema 格式，用于前端动态渲染表单）
     *
     * @return 配置 Schema Map
     */
    Map<String, Object> getConfigSchema();

    /**
     * 获取任务默认参数
     *
     * @return 默认参数 Map
     */
    Map<String, Object> getDefaultParams();

    /**
     * 校验任务参数
     *
     * @param params 参数 Map
     * @return 校验结果，key 为字段名，value 为错误信息（null 表示校验通过）
     */
    Map<String, String> validateParams(Map<String, Object> params);
}
