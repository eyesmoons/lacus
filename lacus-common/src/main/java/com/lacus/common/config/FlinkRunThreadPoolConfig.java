package com.lacus.common.config;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class FlinkRunThreadPoolConfig {

    private static final int corePoolSize = 10;

    private static final int maximumPoolSize = 100;

    private static final long keepAliveTime = 10;

    private static ThreadPoolExecutor threadPoolExecutor;

    private static FlinkRunThreadPoolConfig alarmPoolConfig;

    private FlinkRunThreadPoolConfig() {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(100, true);
        threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MINUTES, workQueue, new ThreadPoolExecutor.AbortPolicy());
    }

    public static synchronized FlinkRunThreadPoolConfig getInstance() {
        if (null == alarmPoolConfig) {
            synchronized (FlinkRunThreadPoolConfig.class) {
                if (null == alarmPoolConfig) {
                    alarmPoolConfig = new FlinkRunThreadPoolConfig();
                }
            }
        }
        return alarmPoolConfig;
    }

    public synchronized ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }
}
