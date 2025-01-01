package com.lacus.domain.dataCollect.job.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lacus.dao.dataCollect.entity.DataSyncJobEntity;
import com.lacus.dao.system.query.AbstractPageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.ObjectUtils;

@EqualsAndHashCode(callSuper = true)
@Data
public class JobPageQuery extends AbstractPageQuery {

    private String jobName;

    @Override
    public QueryWrapper toQueryWrapper() {
        QueryWrapper<DataSyncJobEntity> wrapper = new QueryWrapper<>();
        wrapper.like(ObjectUtils.isNotEmpty(jobName), "job_name", jobName);
        wrapper.orderByDesc("job_id");
        return wrapper;
    }
}
