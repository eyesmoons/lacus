package com.lacus.domain.flink.job.factory;

import com.google.common.collect.Maps;
import com.lacus.common.exception.CustomException;
import com.lacus.enums.FlinkDeployModeEnum;
import com.lacus.service.flink.IFlinkOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.lacus.common.constant.Constants.STANDALONE_FLINK_OPERATION_SERVER;
import static com.lacus.common.constant.Constants.YARN_FLINK_OPERATION_SERVER;

@Component
@Slf4j
public class FlinkOperationServerManager implements ApplicationContextAware {

    private static Map<FlinkDeployModeEnum, IFlinkOperationService> beanMap;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, IFlinkOperationService> map = applicationContext.getBeansOfType(IFlinkOperationService.class);
        beanMap = Maps.newHashMap();
        for (Map.Entry<String, IFlinkOperationService> entry : map.entrySet()) {
            switch (entry.getKey()) {
                case STANDALONE_FLINK_OPERATION_SERVER:
                    beanMap.put(FlinkDeployModeEnum.LOCAL, entry.getValue());
                    beanMap.put(FlinkDeployModeEnum.STANDALONE, entry.getValue());
                    break;
                case YARN_FLINK_OPERATION_SERVER:
                    beanMap.put(FlinkDeployModeEnum.YARN_APPLICATION, entry.getValue());
                    beanMap.put(FlinkDeployModeEnum.YARN_PER, entry.getValue());
                    break;
                default:
                    log.error("不存在的bean类型 name：{}", entry.getKey());
                    throw new CustomException("不存在的bean类型");
            }
        }
    }

    public static IFlinkOperationService getFlinkOperationServer(FlinkDeployModeEnum flinkDeployModeEnum) {
        return beanMap.get(flinkDeployModeEnum);
    }
}
