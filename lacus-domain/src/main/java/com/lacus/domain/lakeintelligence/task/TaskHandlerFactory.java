package com.lacus.domain.lakeintelligence.task;

import com.lacus.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 任务处理器工厂
 *
 * <p>通过 Spring 自动注入所有 TaskHandler 实现，根据任务类型路由到对应处理器。</p>
 */
@Slf4j
@Component
public class TaskHandlerFactory {

    private final Map<String, TaskHandler> handlerMap;

    @Autowired
    public TaskHandlerFactory(List<TaskHandler> handlers) {
        this.handlerMap = new HashMap<>();
        for (TaskHandler handler : handlers) {
            log.info("注册任务处理器：{}", handler.getTaskType());
            handlerMap.put(handler.getTaskType(), handler);
        }
    }

    /**
     * 根据任务类型获取处理器
     */
    public TaskHandler getHandler(String taskType) {
        TaskHandler handler = handlerMap.get(taskType);
        if (handler == null) {
            throw new CustomException("不支持的任务类型：" + taskType + "，已注册类型：" + handlerMap.keySet());
        }
        return handler;
    }

    /**
     * 获取所有已注册的任务类型
     */
    public List<String> getRegisteredTypes() {
        return handlerMap.keySet().stream().sorted().collect(Collectors.toList());
    }

    /**
     * 是否支持指定任务类型
     */
    public boolean supports(String taskType) {
        return handlerMap.containsKey(taskType);
    }
}
