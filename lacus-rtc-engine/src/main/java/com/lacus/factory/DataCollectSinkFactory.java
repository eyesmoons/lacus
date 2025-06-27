package com.lacus.factory;

import com.lacus.sink.ISink;
import com.lacus.sink.BaseSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * 数据采集Factory，所有自定义的采集组件必须注册到Factory
 *
 * @created by shengyu on 2024/1/21 20:18
 */
public class DataCollectSinkFactory implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(DataCollectSinkFactory.class);

    private final Map<String, BaseSink> context = new HashMap<>();
    private static final DataCollectSinkFactory factory = new DataCollectSinkFactory();

    /**
     * 所有processor必须注册到Factory
     */
    public void register() {
        ServiceLoader<BaseSink> sinkList = ServiceLoader.load(BaseSink.class);
        for (BaseSink sink : sinkList) {
            try {
                register(sink);
            } catch (Exception e) {
                logger.error("new instance {} error: ", sink.getClass().getName(), e);
            }
        }
    }

    private void register(BaseSink sink) {
        context.put(sink.getName(), sink);
    }

    public ISink getSink(String name) {
        return context.get(name);
    }

    private DataCollectSinkFactory() {
    }

    public static DataCollectSinkFactory getInstance() {
        return factory;
    }
}
