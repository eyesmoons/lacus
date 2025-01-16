package com.lacus.factory;

import com.lacus.source.ISource;
import com.lacus.source.impl.BaseSource;
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

    private final Map<String, BaseSource> context = new HashMap<>();
    private static final DataCollectReaderFactory factory = new DataCollectReaderFactory();
    private boolean isRegistered = false;

    private DataCollectReaderFactory() {
    }

    public static DataCollectReaderFactory getInstance() {
        return factory;
    }

    /**
     * 所有processor必须注册到Factory
     */
    public synchronized void register() {
        if (isRegistered) {
            return;
        }

        ServiceLoader<BaseSource> readers = ServiceLoader.load(BaseSource.class);
        for (BaseSource reader : readers) {
            try {
                register(reader);
            } catch (Exception e) {
                logger.error("new source instance {} error: ", reader.getClass().getName(), e);
            }
        }
        isRegistered = true;
    }

    private void register(BaseSource reader) {
        context.put(reader.getName(), reader);
    }

    /**
     * 获取指定名称的Reader
     *
     * @param name Reader名称
     * @return Reader实例
     */
    public ISource getReader(String name) {
        return context.get(name);
    }
}
