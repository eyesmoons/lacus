package com.lacus.domain.dig.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lacus.dao.dig.entity.StJobEntity;
import com.lacus.dao.system.query.AbstractPageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.ObjectUtils;

@EqualsAndHashCode(callSuper = true)
@Data
public class StJobQuery extends AbstractPageQuery {

    private String jobName;
    
    private String engineName;
    
    private Integer status;
    
    private Long envId;

    @Override
    public QueryWrapper<StJobEntity> toQueryWrapper() {
        QueryWrapper<StJobEntity> wrapper = new QueryWrapper<>();
        wrapper.like(ObjectUtils.isNotEmpty(jobName), "job_name", jobName);
        wrapper.eq(ObjectUtils.isNotEmpty(engineName), "engine_name", engineName);
        wrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        wrapper.eq(ObjectUtils.isNotEmpty(envId), "env_id", envId);
        wrapper.orderByDesc("job_id");
        return wrapper;
    }
}
