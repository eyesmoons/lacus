package com.lacus.domain.dig;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lacus.common.core.page.PageDTO;
import com.lacus.dao.dig.entity.StJobEntity;
import com.lacus.dao.dig.entity.StJobInstanceEntity;
import com.lacus.domain.dig.query.StJobInstanceQuery;
import com.lacus.service.dig.IStJobInstanceService;
import com.lacus.service.dig.IStJobService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StJobInstanceQueryBusiness {

    @Autowired
    private IStJobInstanceService jobInstanceService;

    @Autowired
    private IStJobService jobService;

    public PageDTO pageList(StJobInstanceQuery query) {
        LambdaQueryWrapper<StJobInstanceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectUtils.isNotEmpty(query.getJobId()), StJobInstanceEntity::getJobId, query.getJobId());
        wrapper.like(ObjectUtils.isNotEmpty(query.getInstanceName()), StJobInstanceEntity::getInstanceName, query.getInstanceName());
        wrapper.orderByDesc(StJobInstanceEntity::getCreateTime);
        Page<StJobInstanceEntity> page = jobInstanceService.page(query.toPage(), wrapper);

        List<StJobInstanceEntity> records = page.getRecords();
        if (!records.isEmpty()) {
            List<Long> jobIds = records.stream().map(StJobInstanceEntity::getJobId).distinct().collect(Collectors.toList());
            List<StJobEntity> jobs = jobService.listByIds(jobIds);
            Map<Long, String> jobNameMap = jobs.stream().collect(Collectors.toMap(StJobEntity::getJobId, StJobEntity::getJobName));
            records.forEach(record -> {
                record.setJobConfig(record.getJobConfig().replaceAll("password\\s*=\\s*(\"[^\"]*\"|[^\\s,{}]+)", "password = \"******\""));
                record.setJobName(jobNameMap.get(record.getJobId()));
            });
        }
        return new PageDTO(records, page.getTotal());
    }

    public StJobInstanceEntity getInstanceById(Long instanceId) {
        StJobInstanceEntity instance = jobInstanceService.getById(instanceId);
        if (instance != null) {
            StJobEntity job = jobService.getById(instance.getJobId());
            if (job != null) {
                instance.setJobName(job.getJobName());
            }
        }
        return instance;
    }
}
