package com.lacus.domain.dataCollect.jobCatalog.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lacus.dao.dataCollect.entity.DataSyncJobCatalogEntity;
import com.lacus.dao.system.query.AbstractPageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.ObjectUtils;

@EqualsAndHashCode(callSuper = true)
@Data
public class JobCatalogQuery extends AbstractPageQuery {

    private String catalogName;

    @Override
    public QueryWrapper toQueryWrapper() {
        QueryWrapper<DataSyncJobCatalogEntity> wrapper = new QueryWrapper<>();
        wrapper.like(ObjectUtils.isNotEmpty(catalogName), "catalog_name", catalogName);
        wrapper.orderByDesc("catalog_id");
        return wrapper;
    }
}
