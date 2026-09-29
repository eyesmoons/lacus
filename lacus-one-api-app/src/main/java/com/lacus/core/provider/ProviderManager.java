package com.lacus.core.provider;

import com.lacus.common.annotation.ProviderName;
import com.lacus.common.enums.DatabaseType;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.ResultCode;
import com.lacus.dao.entity.DataSourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ProviderManager implements ApplicationContextAware {

    private static final Map<DatabaseType, AbstractProvider> providers = new ConcurrentHashMap<>();

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    public ProviderManager(ApplicationContext context) {
        Map<String, Object> beanMap = context.getBeansWithAnnotation(ProviderName.class);
        for (Object event : beanMap.values()) {
            ProviderName annotations = AnnotationUtils.findAnnotation(event.getClass(), ProviderName.class);
            assert annotations != null;
            DatabaseType[] dbTypes = annotations.value();
            for (DatabaseType dbType : dbTypes) {
                providers.put(dbType, (AbstractProvider) event);
            }
        }
    }


    public static AbstractProvider provide(DataSourceEntity datasource) {
        DatabaseType event = datasource.getDatabaseType();
        if (event == null) {
            event = DatabaseType.MySQL;
        }
        try {
            AbstractProvider provider = providers.get(event);
            provider.initDatasource(datasource);
            return provider;
        } catch (Exception e) {
            log.info("根据数据源类型获取注册驱动失败，数据源类型:{},异常信息:{}", event, e.getMessage());
            throw new ApiException(ResultCode.DATASOURCE_NOT_FOUND_ERROR, e.getMessage());
        }
    }
}
