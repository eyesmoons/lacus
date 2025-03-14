package com.lacus.domain.oneapi.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.dao.oneapi.entity.OneApiInfoEntity;
import com.lacus.domain.oneapi.command.ApiAddCommand;
import com.lacus.service.oneapi.IOneApiInfoService;

public class OneApiInfoModelFactory {

    public static OneApiInfoModel loadFromDb(Long apiId, IOneApiInfoService oneApiInfoService) {
        OneApiInfoEntity byId = oneApiInfoService.getById(apiId);

        if (byId == null) {
            throw new ApiException(ErrorCode.Business.OBJECT_NOT_FOUND, apiId, "统一API管理");
        }

        return new OneApiInfoModel(byId);
    }

    public static OneApiInfoModel loadFromAddCommand(ApiAddCommand command, OneApiInfoModel model) {
        if (command != null && model != null) {
            BeanUtil.copyProperties(command, model);
        }
        return model;
    }
}
