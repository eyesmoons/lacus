package com.lacus.domain.monitor.alert;

import cn.hutool.core.collection.CollUtil;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AlertDispatchScheduler {

    @Autowired
    private AlertProperties alertProperties;
    @Autowired
    private AlertDispatchBusiness alertDispatchBusiness;

    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        int poolSize = Math.max(alertProperties.getDispatch().getThreadPoolSize(), 1);
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    @PreDestroy
    public void destroy() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

//    @Scheduled(fixedDelayString = "${lacus.alert.dispatch.fixed-delay-ms:3000}")
    public void dispatch() {
        List<Long> taskIds = alertDispatchBusiness.claimReadyTaskIds(alertProperties.getDispatch().getBatchSize());
        if (CollUtil.isEmpty(taskIds)) {
            return;
        }
        for (Long taskId : taskIds) {
            executorService.submit(() -> {
                try {
                    alertDispatchBusiness.dispatchTask(taskId);
                } catch (Exception ex) {
                    log.error("派发告警任务异常, taskId={}", taskId, ex);
                }
            });
        }
    }
}
