package com.lacus.factory;

import com.lacus.source.ISource;
import com.lacus.source.BaseSource;
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
public class DataCollectSourceFactory implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(DataCollectSourceFactory.class);

    private final Map<String, BaseSource> context = new HashMap<>();
    private static final DataCollectSourceFactory factory = new DataCollectSourceFactory();
    private boolean isRegistered = false;

    private DataCollectSourceFactory() {
    }

    public static DataCollectSourceFactory getInstance() {
        return factory;
    }

    /**
     * 所有processor必须注册到Factory
     */
    public synchronized void register() {
        if (isRegistered) {
            return;
        }

        ServiceLoader<BaseSource> sourceList = ServiceLoader.load(BaseSource.class);
        for (BaseSource source : sourceList) {
            try {
                register(source);
            } catch (Exception e) {
                logger.error("new source instance {} error: ", source.getClass().getName(), e);
            }
        }
        isRegistered = true;
    }

    private void register(BaseSource source) {
        context.put(source.getName(), source);
    }

    /**
     * 获取指定名称的source
     *
     * @param name source名称
     * @return source实例
     */
    public ISource getSource(String name) {
        return context.get(name);
    }
}
