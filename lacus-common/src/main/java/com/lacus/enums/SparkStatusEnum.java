package com.lacus.enums;

public enum SparkStatusEnum {
    SUBMITTED, RUNNING, FINISHED, FAILED, KILLED, UNKNOWN, CREATED;

    public static SparkStatusEnum getSparkStateEnum(String state) {
        for (SparkStatusEnum stateEnum : SparkStatusEnum.values()) {
            if (stateEnum.name().equals(state)) {
                return stateEnum;
            }
        }
        return UNKNOWN;
    }
}
