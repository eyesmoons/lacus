package com.lacus.core.web.domain.permission.checker;

import com.lacus.core.web.domain.login.LoginUser;
import com.lacus.core.web.domain.permission.DataCondition;
import com.lacus.core.web.domain.permission.DataPermissionChecker;
import com.lacus.service.system.ISysDeptService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 数据权限测试接口
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeptTreeDataPermissionChecker extends DataPermissionChecker {

    private ISysDeptService deptService;

    @Override
    public boolean check(LoginUser loginUser, DataCondition condition) {
        if (condition == null || loginUser == null) {
            return false;
        }

        if (loginUser.getDeptId() == null || condition.getTargetDeptId() == null) {
            return false;
        }

        Long currentDeptId = loginUser.getDeptId();
        Long targetDeptId = condition.getTargetDeptId();

        boolean isContainsTargetDept = deptService.isChildOfTheDept(loginUser.getDeptId(), targetDeptId);
        boolean isSameDept = Objects.equals(currentDeptId, targetDeptId);

        return isContainsTargetDept || isSameDept;
    }

}
