package com.lacus.domain.system.operationlog.query;

import cn.hutool.core.util.StrUtil;
import com.lacus.dao.system.entity.SysLoginInfoEntity;
import com.lacus.dao.system.entity.SysOperationLogEntity;
import com.lacus.dao.system.query.AbstractPageQuery;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OperationLogQuery extends AbstractPageQuery {

    private String businessType;
    private String status;
    private String username;
    private String requestModule;


    @Override
    public QueryWrapper toQueryWrapper() {
        QueryWrapper<SysOperationLogEntity> queryWrapper = new QueryWrapper<>();

        queryWrapper.like(businessType!=null, "business_type", businessType)
            .eq(status != null, "status", status)
            .like(StrUtil.isNotEmpty(username), "username", username)
            .like(StrUtil.isNotEmpty(requestModule), "request_module", requestModule);
        addSortCondition(queryWrapper);
        addTimeCondition(queryWrapper, "operation_time");

        return queryWrapper;
    }
}
