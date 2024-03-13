package com.lacus.domain.metadata.datasource.factory;

import com.lacus.domain.metadata.datasource.procesors.AbsDatasourceProcessor;
import com.lacus.domain.metadata.datasource.procesors.IDatasourceProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

@Component
public class MetaDatasourceFactory {
    private static final Logger logger = LoggerFactory.getLogger(MetaDatasourceFactory.class);

    private final Map<String, AbsDatasourceProcessor> context = new HashMap<>();
    private static final MetaDatasourceFactory factory = new MetaDatasourceFactory();

    /**
     * 所有processor必须注册到Factory
     */
    public void register() {
        ServiceLoader<AbsDatasourceProcessor> classList = ServiceLoader.load(AbsDatasourceProcessor.class);
        for (AbsDatasourceProcessor processor : classList) {
            try {
                register(processor);
            } catch (Exception e) {
                logger.error("new instance " + processor.getClass().getName() + " error: ", e);
            }
        }
    }

    private void register(AbsDatasourceProcessor processor) {
        context.put(processor.getDatasourceName(), processor);
    }

    public IDatasourceProcessor getProcessor(String name) {
        return context.get(name);
    }

    private MetaDatasourceFactory() {
    }
}