package com.lacus.factory;

import com.lacus.AbsFlinkProcessor;
import com.lacus.IFlinkProcessor;
import com.lacus.common.utils.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据采集Factory，所有自定义的采集组件必须注册到Factory
 *
 * @created by shengyu on 2024/1/21 20:18
 */
public class DataCollectFactory {
    private static final Logger logger = LoggerFactory.getLogger(DataCollectFactory.class);

    private final Map<String, AbsFlinkProcessor> context = new HashMap<>();
    private static final DataCollectFactory factory = new DataCollectFactory();

    /**
     * 所有processor必须注册到Factory
     */
    @SuppressWarnings("rawtypes")
    public void register() {
        List<Class> classList = ClassUtil.getAllClassByInterface(AbsFlinkProcessor.class);
        for (Class cls : classList) {
            try {
                AbsFlinkProcessor processor = (AbsFlinkProcessor) cls.newInstance();
                register(processor);
            } catch (Exception e) {
                logger.error("new instance " + cls.getName() + " error: ", e);
            }
        }
    }

    private void register(AbsFlinkProcessor processor) {
        context.put(processor.getName(), processor);
    }

    public IFlinkProcessor getProcessor(String name) {
        return context.get(name);
    }

    private DataCollectFactory() {
    }

    public static DataCollectFactory getInstance() {
        return factory;
    }
}