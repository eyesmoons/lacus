package com.lacus.core.web.domain.permission;

import cn.hutool.extra.spring.SpringUtil;
import com.lacus.core.web.domain.login.LoginUser;
import com.lacus.core.web.domain.permission.checker.AllDataPermissionChecker;
import com.lacus.core.web.domain.permission.checker.CustomDataPermissionChecker;
import com.lacus.core.web.domain.permission.checker.DefaultDataPermissionChecker;
import com.lacus.core.web.domain.permission.checker.DeptTreeDataPermissionChecker;
import com.lacus.core.web.domain.permission.checker.OnlySelfDataPermissionChecker;
import com.lacus.core.web.domain.permission.checker.SingleDeptDataPermissionChecker;
import com.lacus.dao.system.enums.DataScopeEnum;
import com.lacus.service.system.ISysDeptService;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/**
 * 数据权限检测器工厂
 */
@Component
public class DataPermissionCheckerFactory {
    private static DataPermissionChecker allChecker;
    private static DataPermissionChecker customChecker;
    private static DataPermissionChecker singleDeptChecker;
    private static DataPermissionChecker deptTreeChecker;
    private static DataPermissionChecker onlySelfChecker;
    private static DataPermissionChecker defaultSelfChecker;


    @PostConstruct
    public void initAllChecker() {
        ISysDeptService deptService = SpringUtil.getBean(ISysDeptService.class);

        allChecker = new AllDataPermissionChecker();
        customChecker = new CustomDataPermissionChecker(deptService);
        singleDeptChecker = new SingleDeptDataPermissionChecker(deptService);
        deptTreeChecker = new DeptTreeDataPermissionChecker(deptService);
        onlySelfChecker = new OnlySelfDataPermissionChecker(deptService);
        defaultSelfChecker = new DefaultDataPermissionChecker();
    }


    public static DataPermissionChecker getChecker(LoginUser loginUser) {
        if (loginUser == null) {
            return deptTreeChecker;
        }

        DataScopeEnum dataScope = loginUser.getRoleInfo().getDataScope();
        switch (dataScope) {
            case ALL:
                return allChecker;
            case CUSTOM_DEFINE:
                return customChecker;
            case SINGLE_DEPT:
                return singleDeptChecker;
            case DEPT_TREE:
                return deptTreeChecker;
            case ONLY_SELF:
                return onlySelfChecker;
            default:
                return defaultSelfChecker;
        }
    }

}
