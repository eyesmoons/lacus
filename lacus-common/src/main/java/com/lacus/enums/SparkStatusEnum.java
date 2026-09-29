package com.lacus.enums;

public enum SparkStatusEnum {
    SUBMITTED, CONNECTED, RUNNING, FINISHED, FAILED, KILLED, UNKNOWN, CREATED, LOST;

    public static SparkStatusEnum getSparkStateEnum(String state) {
        for (SparkStatusEnum stateEnum : SparkStatusEnum.values()) {
            if (stateEnum.name().equals(state)) {
                return stateEnum;
            }
        }
        return UNKNOWN;
    }
}
