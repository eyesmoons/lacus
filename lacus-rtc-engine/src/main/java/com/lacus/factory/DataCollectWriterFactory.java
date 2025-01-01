package com.lacus.factory;

import com.lacus.writer.IWriter;
import com.lacus.writer.impl.BaseWriter;
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

    private final Map<String, BaseWriter> context = new HashMap<>();
    private static final DataCollectWriterFactory factory = new DataCollectWriterFactory();

    /**
     * 所有processor必须注册到Factory
     */
    public void register() {
        ServiceLoader<BaseWriter> writers = ServiceLoader.load(BaseWriter.class);
        for (BaseWriter writer : writers) {
            try {
                register(writer);
            } catch (Exception e) {
                logger.error("new instance {} error: ", writer.getClass().getName(), e);
            }
        }
    }

    private void register(BaseWriter writer) {
        context.put(writer.getName(), writer);
    }

    public IWriter getWriter(String name) {
        return context.get(name);
    }

    private DataCollectWriterFactory() {
    }

    public static DataCollectWriterFactory getInstance() {
        return factory;
    }
}