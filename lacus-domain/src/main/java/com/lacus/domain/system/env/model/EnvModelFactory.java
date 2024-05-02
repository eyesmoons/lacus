package com.lacus.domain.system.env.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.dao.system.entity.SystemEnvEntity;
import com.lacus.domain.system.env.command.EnvAddCommand;
import com.lacus.service.system.ISystemEnvService;

public class EnvModelFactory {

    public static EnvModel loadFromDb(Long noticeId, ISystemEnvService envService) {
        SystemEnvEntity byId = envService.getById(noticeId);

        if (byId == null) {
            throw new ApiException(ErrorCode.Business.OBJECT_NOT_FOUND, noticeId, "环境变量管理");
        }

        return new EnvModel(byId);
    }

    public static EnvModel loadFromAddCommand(EnvAddCommand command, EnvModel model) {
        if (command != null && model != null) {
            BeanUtil.copyProperties(command, model);
        }
        return model;
    }
}
