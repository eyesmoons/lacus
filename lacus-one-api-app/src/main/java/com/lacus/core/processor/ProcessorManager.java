package com.lacus.core.processor;

import com.lacus.common.exception.ResultCode;
import com.lacus.common.exception.ApiException;
import com.lacus.common.enums.DatabaseType;
import com.lacus.common.annotation.ProcessorName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ProcessorManager implements ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    private static final Map<DatabaseType, AbstractProcessor> processors = new ConcurrentHashMap<>();

    public ProcessorManager(ApplicationContext context) {
        Map<String, Object> beanMap = context.getBeansWithAnnotation(ProcessorName.class);
        for (Object event : beanMap.values()) {
            ProcessorName annotations = AnnotationUtils.findAnnotation(event.getClass(), ProcessorName.class);
            assert annotations != null;
            DatabaseType[] dbTypes = annotations.value();
            for (DatabaseType dbType : dbTypes) {
                processors.put(dbType, (AbstractProcessor) event);
            }
        }
    }

    public static AbstractProcessor getProcessor(DatabaseType type) {
        try {
            return processors.get(type);
        } catch (Exception e) {
            log.info("根据数据源类型获取解析器失败，数据源类型:{},异常信息:{}", type, e.getMessage());
            throw new ApiException(ResultCode.DATASOURCE_NOT_FOUND_ERROR, e.getMessage());
        }
    }
}
