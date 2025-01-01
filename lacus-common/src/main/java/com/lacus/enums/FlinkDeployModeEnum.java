package com.lacus.enums;

import com.lacus.common.exception.CustomException;
import org.apache.commons.lang3.StringUtils;

public enum FlinkDeployModeEnum {
    YARN_PER, STANDALONE, LOCAL, YARN_APPLICATION;

    public static FlinkDeployModeEnum getModel(String model) {
        if (StringUtils.isEmpty(model)) {
            throw new CustomException("部署模式不能为空");
        }
        for (FlinkDeployModeEnum flinkDeployModeEnum : FlinkDeployModeEnum.values()) {
            if (flinkDeployModeEnum.name().equals(model.trim().toUpperCase())) {
                return flinkDeployModeEnum;
            }

        }
        throw new CustomException("部署模式不存在");
    }
}
