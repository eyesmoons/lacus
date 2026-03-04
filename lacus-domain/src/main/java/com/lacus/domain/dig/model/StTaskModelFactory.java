package com.lacus.domain.dig.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.domain.dig.dto.StTaskConfig;

public class StTaskModelFactory {
    public static StTaskModel loadFromAddCommand(StTaskConfig addCommand, StTaskModel model) {
        if (addCommand != null && model != null) {
            BeanUtil.copyProperties(addCommand, model);
        }
        return model;
    }
}
