package com.lacus.service.spark;

public interface ISparkOperationService {

    /**
     * 启动任务
     */
    void start(Long jobId);

    /**
     * 停止任务
     */
    void stop(Long jobId);
} 