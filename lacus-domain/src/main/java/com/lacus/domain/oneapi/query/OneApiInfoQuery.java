package com.lacus.domain.oneapi.query;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lacus.dao.oneapi.entity.OneApiInfoEntity;
import com.lacus.dao.system.query.AbstractPageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class OneApiInfoQuery extends AbstractPageQuery {

    private String apiName;

    @Override
    public QueryWrapper<OneApiInfoEntity> toQueryWrapper() {
        QueryWrapper<OneApiInfoEntity> wrapper = new QueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(apiName), "api_name", apiName);
        return wrapper;
    }
}
