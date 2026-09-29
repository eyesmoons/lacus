package com.lacus.domain.dig.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lacus.dao.dig.entity.StJobInstanceEntity;
import com.lacus.dao.system.query.AbstractPageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.ObjectUtils;

@EqualsAndHashCode(callSuper = true)
@Data
public class StJobInstanceQuery extends AbstractPageQuery {
    private Long jobId;
    private String instanceName;

    @Override
    public QueryWrapper<StJobInstanceEntity> toQueryWrapper() {
        QueryWrapper<StJobInstanceEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(ObjectUtils.isNotEmpty(jobId), "job_id", jobId);
        wrapper.like(ObjectUtils.isNotEmpty(instanceName), "instance_name", instanceName);
        wrapper.orderByDesc("create_time");
        return wrapper;
    }
}
