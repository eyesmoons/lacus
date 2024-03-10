package com.lacus.domain.metadata.datasource.factory;

import com.lacus.common.utils.ClassUtil;
import com.lacus.domain.metadata.datasource.procesors.AbsDatasourceProcessor;
import com.lacus.domain.metadata.datasource.procesors.IDatasourceProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatasourceFactory {
    private static final Logger logger = LoggerFactory.getLogger(DatasourceFactory.class);

    private final Map<String, AbsDatasourceProcessor> context = new HashMap<>();
    private static final DatasourceFactory factory = new DatasourceFactory();

    /**
     * 所有processor必须注册到Factory
     */
    @SuppressWarnings("rawtypes")
    public void register() {
        List<Class> classList = ClassUtil.getAllClassByInterface(AbsDatasourceProcessor.class);
        for (Class cls : classList) {
            try {
                AbsDatasourceProcessor processor = (AbsDatasourceProcessor) cls.newInstance();
                register(processor);
            } catch (Exception e) {
                logger.error("new instance " + cls.getName() + " error: ", e);
            }
        }
    }

    private void register(AbsDatasourceProcessor processor) {
        context.put(processor.getDatasourceName(), processor);
    }

    public IDatasourceProcessor getProcessor(String name) {
        return context.get(name);
    }

    private DatasourceFactory() {
    }

    public static DatasourceFactory getInstance() {
        return factory;
    }
}