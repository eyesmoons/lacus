package com.lacus.domain.dig.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.domain.dig.command.AddStJobCommand;
import com.lacus.domain.dig.command.UpdateStJobCommand;

public class StJobModelFactory {
    public static StJobModel loadFromAddCommand(AddStJobCommand addCommand, StJobModel model) {
        if (addCommand != null && model != null) {
            BeanUtil.copyProperties(addCommand, model);
        }
        return model;
    }

    public static StJobModel loadFromUpdateCommand(UpdateStJobCommand updateCommand, StJobModel model) {
        if (updateCommand != null && model != null) {
            BeanUtil.copyProperties(updateCommand, model);
        }
        return model;
    }
}