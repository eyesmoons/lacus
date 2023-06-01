package com.lacus.domain.datasync.job.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lacus.dao.datasync.entity.DataSyncJobEntity;
import com.lacus.dao.system.query.AbstractPageQuery;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

@Data
public class JobQuery extends AbstractPageQuery {

    private String jobName;

    @Override
    public QueryWrapper toQueryWrapper() {
        QueryWrapper<DataSyncJobEntity> wrapper = new QueryWrapper<>();
        wrapper.like(ObjectUtils.isNotEmpty(jobName), "job_name", jobName);
        return wrapper;
    }
}
