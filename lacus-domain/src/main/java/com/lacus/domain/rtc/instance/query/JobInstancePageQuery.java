package com.lacus.domain.rtc.instance.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lacus.dao.rtc.entity.DataSyncJobEntity;
import com.lacus.dao.system.query.AbstractPageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.ObjectUtils;

@EqualsAndHashCode(callSuper = true)
@Data
public class JobInstancePageQuery extends AbstractPageQuery {

    private String jobName;

    @Override
    public QueryWrapper toQueryWrapper() {
        QueryWrapper<DataSyncJobEntity> wrapper = new QueryWrapper<>();
        wrapper.like(ObjectUtils.isNotEmpty(jobName), "instance_name", jobName);
        wrapper.orderByDesc("instance_id");
        return wrapper;
    }
}
