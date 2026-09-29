package com.lacus.core.web.domain.permission;

import com.lacus.core.web.domain.login.LoginUser;
import com.lacus.service.system.ISysDeptService;
import lombok.Data;

/**
 * 数据权限测试接口
 */
@Data
public abstract class DataPermissionChecker {

    private ISysDeptService deptService;

    /**
     * 检测当前用户对于 给定条件的数据 是否有权限
     * @param loginUser
     * @param condition
     * @return
     */
    public abstract boolean check(LoginUser loginUser, DataCondition condition);

}
