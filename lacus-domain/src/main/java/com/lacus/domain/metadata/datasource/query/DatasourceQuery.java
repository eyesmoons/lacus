package com.lacus.domain.metadata.datasource.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lacus.dao.metadata.entity.MetaDatasourceEntity;
import com.lacus.dao.system.query.AbstractPageQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DatasourceQuery extends AbstractPageQuery {
    private String type;
    private String datasourceName;
    private String ip;
    private Integer status;

    @Override
    public QueryWrapper<MetaDatasourceEntity> toQueryWrapper() {
        QueryWrapper<MetaDatasourceEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(ObjectUtils.isNotEmpty(type), "type", type);
        wrapper.like(ObjectUtils.isNotEmpty(datasourceName), "datasource_name", datasourceName);
        wrapper.like(ObjectUtils.isNotEmpty(ip), "ip", ip);
        wrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        return wrapper;
    }
}
