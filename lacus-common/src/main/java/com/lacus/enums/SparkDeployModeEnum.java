package com.lacus.enums;

import com.lacus.common.exception.CustomException;
import org.apache.commons.lang3.StringUtils;

public enum SparkDeployModeEnum {
    LOCAL, STANDALONE_CLIENT, STANDALONE_CLUSTER, YARN_CLIENT, YARN_CLUSTER, K8S_CLIENT, K8S_CLUSTER;

    public static SparkDeployModeEnum getModel(String model) {
        if (StringUtils.isEmpty(model)) {
            throw new CustomException("部署模式不能为空");
        }
        for (SparkDeployModeEnum flinkDeployModeEnum : SparkDeployModeEnum.values()) {
            if (flinkDeployModeEnum.name().equals(model.trim().toUpperCase())) {
                return flinkDeployModeEnum;
            }

        }
        throw new CustomException("部署模式不存在");
    }
}
