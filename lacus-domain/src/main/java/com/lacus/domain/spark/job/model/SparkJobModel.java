package com.lacus.domain.spark.job.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.dao.spark.entity.SparkJobEntity;
import com.lacus.service.spark.ISparkJobService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class SparkJobModel extends SparkJobEntity {

    public SparkJobModel(SparkJobEntity entity) {
        BeanUtil.copyProperties(entity, this);
    }

    public void checkJobNameUnique(ISparkJobService jobService) {
        if (jobService.isJobNameDuplicated(getJobId(), getJobName())) {
            throw new ApiException(ErrorCode.Business.JOB_NAME_IS_NOT_UNIQUE, getJobName());
        }
    }
} 