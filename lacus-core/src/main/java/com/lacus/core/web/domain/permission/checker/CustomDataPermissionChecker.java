package com.lacus.core.web.domain.permission.checker;

import cn.hutool.core.collection.CollUtil;
import com.lacus.core.web.domain.login.LoginUser;
import com.lacus.core.web.domain.permission.DataCondition;
import com.lacus.core.web.domain.permission.DataPermissionChecker;
import com.lacus.service.system.ISysDeptService;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据权限测试接口
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomDataPermissionChecker extends DataPermissionChecker {

    private ISysDeptService deptService;


    @Override
    public boolean check(LoginUser loginUser, DataCondition condition) {
        if (condition == null || loginUser == null) {
            return false;
        }

        if (loginUser.getRoleInfo() == null) {
            return false;
        }

        Set<Long> deptIdSet = loginUser.getRoleInfo().getDeptIdSet();
        Long targetDeptId = condition.getTargetDeptId();

        return condition.getTargetDeptId() != null && CollUtil.safeContains(deptIdSet, targetDeptId);
    }
}
