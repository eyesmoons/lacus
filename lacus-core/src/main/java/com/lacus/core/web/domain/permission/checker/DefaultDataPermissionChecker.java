package com.lacus.core.web.domain.permission.checker;

import com.lacus.core.web.domain.permission.DataCondition;
import com.lacus.core.web.domain.permission.DataPermissionChecker;
import com.lacus.core.web.domain.login.LoginUser;
import com.lacus.service.system.ISysDeptService;
import lombok.Data;

/**
 * 数据权限测试接口
 */
@Data
public class DefaultDataPermissionChecker extends DataPermissionChecker {

    private ISysDeptService deptService;

    @Override
    public boolean check(LoginUser loginUser, DataCondition condition) {
        return false;
    }

}
