package com.lacus.core.web.service;

import cn.hutool.core.collection.CollUtil;
import com.lacus.core.security.AuthenticationUtils;
import com.lacus.core.web.domain.login.LoginUser;
import com.lacus.core.web.domain.permission.DataCondition;
import com.lacus.core.web.domain.permission.DataPermissionChecker;
import com.lacus.core.web.domain.permission.DataPermissionCheckerFactory;
import com.lacus.dao.system.entity.SysUserEntity;
import com.lacus.service.system.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据权限校验服务
 */
@Service("dataScope")
public class DataPermissionService {

    @Autowired
    private ISysUserService userService;

    /**
     * 通过userId 校验当前用户 对 目标用户是否有操作权限
     * @param userId
     * @return
     */
    public boolean checkUserId(Long userId) {
        LoginUser loginUser = AuthenticationUtils.getLoginUser();
        SysUserEntity targetUser = userService.getById(userId);
        if (targetUser == null) {
            return true;
        }
        return checkDataScope(loginUser, targetUser.getDeptId(), userId);
    }

    /**
     * 通过userId 校验当前用户 对 目标用户是否有操作权限
     * @param userIds
     * @return
     */
    public boolean checkUserIds(List<Long> userIds) {
        if (CollUtil.isNotEmpty(userIds)) {
            for (Long userId : userIds) {
                boolean checkResult = checkUserId(userId);
                if (!checkResult) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkDeptId(Long deptId) {
        LoginUser loginUser = AuthenticationUtils.getLoginUser();
        return checkDataScope(loginUser, deptId, null);
    }


    public boolean checkDataScope(LoginUser loginUser, Long targetDeptId, Long targetUserId) {
        DataCondition dataCondition = DataCondition.builder().targetDeptId(targetDeptId).targetUserId(targetUserId).build();
        DataPermissionChecker checker = DataPermissionCheckerFactory.getChecker(loginUser);
        return checker.check(loginUser, dataCondition);
    }



}
