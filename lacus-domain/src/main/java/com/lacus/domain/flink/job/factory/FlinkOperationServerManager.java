package com.lacus.domain.flink.job.factory;

import com.lacus.enums.FlinkDeployModeEnum;
import com.lacus.service.flink.IFlinkOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.lacus.common.constant.Constants.STANDALONE_FLINK_OPERATION_SERVER;
import static com.lacus.common.constant.Constants.YARN_FLINK_OPERATION_SERVER;

@Component
@Slf4j
public class FlinkOperationServerManager implements ApplicationContextAware {

    private static Map<FlinkDeployModeEnum, IFlinkOperationService> flinkOperationMap;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, IFlinkOperationService> map = applicationContext.getBeansOfType(IFlinkOperationService.class);
        flinkOperationMap = new HashMap<>();
        for (Map.Entry<String, IFlinkOperationService> entry : map.entrySet()) {
            if (entry.getKey().equals(STANDALONE_FLINK_OPERATION_SERVER)) {
                flinkOperationMap.put(FlinkDeployModeEnum.LOCAL, entry.getValue());
                flinkOperationMap.put(FlinkDeployModeEnum.STANDALONE, entry.getValue());
            } else if (entry.getKey().equals(YARN_FLINK_OPERATION_SERVER)) {
                flinkOperationMap.put(FlinkDeployModeEnum.YARN_APPLICATION, entry.getValue());
                flinkOperationMap.put(FlinkDeployModeEnum.YARN_PER, entry.getValue());
            }
        }
    }

    public static IFlinkOperationService getFlinkOperationServer(FlinkDeployModeEnum flinkDeployModeEnum) {
        return flinkOperationMap.get(flinkDeployModeEnum);
    }
}
