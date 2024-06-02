package com.lacus.domain.system.resources.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.dao.system.entity.SysResourcesEntity;
import com.lacus.domain.system.resources.command.ResourceAddCommand;
import com.lacus.service.system.ISysResourcesService;

public class ResourceModelFactory {

    public static ResourceModel loadFromDb(Long resourceId, ISysResourcesService resourcesService) {
        SysResourcesEntity byId = resourcesService.getById(resourceId);

        if (byId == null) {
            throw new ApiException(ErrorCode.Business.OBJECT_NOT_FOUND, resourceId, "资源管理");
        }

        return new ResourceModel(byId);
    }

    public static ResourceModel loadFromAddCommand(ResourceAddCommand command, ResourceModel model) {
        if (command != null && model != null) {
            BeanUtil.copyProperties(command, model);
        }
        return model;
    }
}
