package com.lacus.factory;

import com.lacus.sink.ISink;
import com.lacus.sink.impl.BaseSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * 数据采集Factory，所有自定义的采集组件必须注册到Factory
 *
 * @created by shengyu on 2024/1/21 20:18
 */
public class DataCollectWriterFactory {
    private static final Logger logger = LoggerFactory.getLogger(DataCollectWriterFactory.class);

    private final Map<String, BaseSink> context = new HashMap<>();
    private static final DataCollectWriterFactory factory = new DataCollectWriterFactory();

    /**
     * 所有processor必须注册到Factory
     */
    public void register() {
        ServiceLoader<BaseSink> writers = ServiceLoader.load(BaseSink.class);
        for (BaseSink writer : writers) {
            try {
                register(writer);
            } catch (Exception e) {
                logger.error("new instance {} error: ", writer.getClass().getName(), e);
            }
        }
    }

    private void register(BaseSink writer) {
        context.put(writer.getName(), writer);
    }

    public ISink getWriter(String name) {
        return context.get(name);
    }

    private DataCollectWriterFactory() {
    }

    public static DataCollectWriterFactory getInstance() {
        return factory;
    }
}
