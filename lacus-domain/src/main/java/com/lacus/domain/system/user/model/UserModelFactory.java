package com.lacus.domain.system.user.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.domain.system.user.command.AddUserCommand;
import com.lacus.dao.system.entity.SysUserEntity;
import com.lacus.service.system.ISysUserService;

/**
 * 用户模型工厂
 */
public class UserModelFactory {

    public static UserModel loadFromDb(Long userId, ISysUserService userService) {
        SysUserEntity byId = userService.getById(userId);
        if (byId == null) {
            throw new ApiException(ErrorCode.Business.OBJECT_NOT_FOUND, userId, "用户");
        }
        return new UserModel(byId);
    }

    public static UserModel loadFromAddCommand(AddUserCommand command, UserModel model) {
        if (command != null && model != null) {
            BeanUtil.copyProperties(command, model);
        }
        return model;
    }

}
