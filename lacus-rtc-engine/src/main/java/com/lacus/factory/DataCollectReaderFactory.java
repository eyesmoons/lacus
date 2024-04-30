package com.lacus.factory;

import com.lacus.reader.BaseReader;
import com.lacus.IReader;
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
public class DataCollectReaderFactory {
    private static final Logger logger = LoggerFactory.getLogger(DataCollectReaderFactory.class);

    private final Map<String, BaseReader> context = new HashMap<>();
    private static final DataCollectReaderFactory factory = new DataCollectReaderFactory();

    /**
     * 所有processor必须注册到Factory
     */
    public void register() {
        ServiceLoader<BaseReader> readers = ServiceLoader.load(BaseReader.class);
        for (BaseReader reader : readers) {
            try {
                register(reader);
            } catch (Exception e) {
                logger.error("new reader instance {} error: ", reader.getClass().getName(), e);
            }
        }
    }

    private void register(BaseReader reader) {
        context.put(reader.getName(), reader);
    }

    public IReader getReader(String name) {
        return context.get(name);
    }

    private DataCollectReaderFactory() {
    }

    public static DataCollectReaderFactory getInstance() {
        return factory;
    }
}