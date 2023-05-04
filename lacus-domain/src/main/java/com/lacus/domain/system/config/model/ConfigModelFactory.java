package com.lacus.domain.system.config.model;

import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.domain.system.config.command.ConfigUpdateCommand;
import com.lacus.dao.system.entity.SysConfigEntity;
import com.lacus.service.system.ISysConfigService;

/**
 * 配置模型工厂
 */
public class ConfigModelFactory {

    public static ConfigModel loadFromDb(Long configId, ISysConfigService configService) {
        SysConfigEntity byId = configService.getById(configId);
        if (byId == null) {
            throw new ApiException(ErrorCode.Business.OBJECT_NOT_FOUND, configId, "参数配置");
        }
        return new ConfigModel(byId);
    }

    public static ConfigModel loadFromUpdateCommand(ConfigUpdateCommand updateCommand, ISysConfigService configService) {
        ConfigModel configModel = loadFromDb(updateCommand.getConfigId(), configService);
        configModel.setConfigValue(updateCommand.getConfigValue());
        return configModel;
    }


}
