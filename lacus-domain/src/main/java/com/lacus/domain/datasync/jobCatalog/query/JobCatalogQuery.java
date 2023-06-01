package com.lacus.domain.datasync.jobCatalog.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lacus.dao.datasync.entity.DataSyncJobCatalogEntity;
import com.lacus.dao.system.query.AbstractPageQuery;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

@Data
public class JobCatalogQuery extends AbstractPageQuery {

    private String catalogName;

    @Override
    public QueryWrapper toQueryWrapper() {
        QueryWrapper<DataSyncJobCatalogEntity> wrapper = new QueryWrapper<>();
        wrapper.like(ObjectUtils.isNotEmpty(catalogName), "catalog_name", catalogName);
        return wrapper;
    }
}
