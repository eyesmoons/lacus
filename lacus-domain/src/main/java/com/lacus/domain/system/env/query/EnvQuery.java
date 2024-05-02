package com.lacus.domain.system.env.query;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lacus.dao.system.entity.SysEnvEntity;
import com.lacus.dao.system.query.AbstractPageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class EnvQuery extends AbstractPageQuery {

    private String name;

    @Override
    public QueryWrapper<SysEnvEntity> toQueryWrapper() {
        QueryWrapper<SysEnvEntity> wrapper = new QueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(name), "name", name);
        return wrapper;
    }
}
