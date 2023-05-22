package com.lacus.domain.metadata.table.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lacus.dao.metadata.entity.MetaTableEntity;
import com.lacus.dao.system.query.AbstractPageQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TableQuery extends AbstractPageQuery {
    private Long datasourceId;
    private List<Long> dbIds;
    private String tableName;
    private Long dbId;
    private String dbName;
    private List<String> dbNames;

    @Override
    public QueryWrapper<MetaTableEntity> toQueryWrapper() {
        QueryWrapper<MetaTableEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(ObjectUtils.isNotEmpty(dbId), "db_id", dbId);
        wrapper.in(ObjectUtils.isNotEmpty(dbIds), "db_id", dbIds);
        wrapper.like(ObjectUtils.isNotEmpty(tableName), "table_name", tableName);
        return wrapper;
    }
}
