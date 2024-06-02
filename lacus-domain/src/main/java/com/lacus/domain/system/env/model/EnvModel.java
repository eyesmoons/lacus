package com.lacus.domain.system.env.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.dao.system.entity.SysEnvEntity;
import com.lacus.domain.system.env.command.EnvAddCommand;
import com.lacus.domain.system.env.command.EnvUpdateCommand;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shengyu
 * @date 2024/4/30 17:46
 */
@Data
@NoArgsConstructor
public class EnvModel extends SysEnvEntity {

    public EnvModel(SysEnvEntity entity) {
        if (entity != null) {
            BeanUtil.copyProperties(entity, this);
        }
    }

    public void loadUpdateCommand(EnvUpdateCommand command) {
        if (command != null) {
            BeanUtil.copyProperties(command, this);
        }
    }
}