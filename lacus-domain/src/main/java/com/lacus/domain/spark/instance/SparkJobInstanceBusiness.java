package com.lacus.domain.spark.instance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lacus.common.core.page.PageDTO;
import com.lacus.dao.spark.entity.SparkJobEntity;
import com.lacus.dao.spark.entity.SparkJobInstanceEntity;
import com.lacus.domain.spark.instance.query.JobInstancePageQuery;
import com.lacus.service.spark.ISparkJobInstanceService;
import com.lacus.service.spark.ISparkJobService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;

/**
 * @author shengyu
 * @date 2024/12/31 17:56
 */
@Service
public class SparkJobInstanceBusiness {
    @Autowired
    private ISparkJobInstanceService sparkJobInstanceService;

    @Autowired
    private ISparkJobService sparkJobService;

    @SuppressWarnings({"unchecked"})
    public PageDTO pageList(@Valid JobInstancePageQuery query) {
        Page<?> page = sparkJobInstanceService.page(query.toPage(), query.toQueryWrapper());
        List<SparkJobInstanceEntity> records = (List<SparkJobInstanceEntity>) page.getRecords();
        for (SparkJobInstanceEntity record : records) {
            Long jobId = record.getJobId();
            SparkJobEntity job = sparkJobService.getById(jobId);
            record.setJobName(job.getJobName());
            record.setJobType(job.getJobType().name());
        }
        return new PageDTO(records, page.getTotal());
    }

    public SparkJobInstanceEntity detail(Long instanceId) {
        SparkJobInstanceEntity instance = sparkJobInstanceService.getById(instanceId);
        if (ObjectUtils.isNotEmpty(instance)) {
            SparkJobEntity job = sparkJobService.getById(instance.getJobId());
            instance.setJobName(job.getJobName());
            instance.setJobType(job.getJobType().name());
        }
        return instance;
    }
}
