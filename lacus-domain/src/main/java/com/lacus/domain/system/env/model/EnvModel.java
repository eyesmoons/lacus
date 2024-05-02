package com.lacus.domain.system.env.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.dao.system.entity.SystemEnvEntity;
import com.lacus.domain.system.env.command.EnvAddCommand;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shengyu
 * @date 2024/4/30 17:46
 */
@Data
@NoArgsConstructor
public class EnvModel extends SystemEnvEntity {

    public EnvModel(SystemEnvEntity entity) {
        if (entity != null) {
            BeanUtil.copyProperties(entity, this);
        }
    }

    public void loadUpdateCommand(EnvAddCommand command) {
        if (command != null) {
            BeanUtil.copyProperties(command, this);
        }
    }
}