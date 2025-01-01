package com.lacus.service.spark;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.spark.entity.SparkJobInstanceEntity;
import com.lacus.enums.SparkStatusEnum;

import java.util.Date;

public interface ISparkJobInstanceService extends IService<SparkJobInstanceEntity> {
    
    void updateStatus(Long instanceId, SparkStatusEnum sparkStatusEnum);

    void updateStatusByJobId(Long jobId, SparkStatusEnum sparkStatusEnum, Date finishedTime);
}
