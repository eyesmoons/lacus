package com.lacus.domain.datasync.jobCatelog.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lacus.dao.datasync.entity.DataSyncJobCatelogEntity;
import com.lacus.dao.system.query.AbstractPageQuery;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

@Data
public class JobCateLogQuery extends AbstractPageQuery {

    private String catelogName;

    @Override
    public QueryWrapper toQueryWrapper() {
        QueryWrapper<DataSyncJobCatelogEntity> wrapper = new QueryWrapper<>();
        wrapper.like(ObjectUtils.isNotEmpty(catelogName), "catelog_name", catelogName);
        return wrapper;
    }
}
