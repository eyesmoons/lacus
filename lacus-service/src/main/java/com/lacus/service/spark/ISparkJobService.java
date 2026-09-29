package com.lacus.service.spark;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.spark.entity.SparkJobEntity;
import com.lacus.enums.SparkStatusEnum;

public interface ISparkJobService extends IService<SparkJobEntity> {

    boolean isJobNameDuplicated(Long jobId, String jobName);

    void updateStatus(Long jobId, SparkStatusEnum sparkStatusEnum);

    void updateStatus(Long jobId, String appId, SparkStatusEnum sparkStatusEnum);
}
