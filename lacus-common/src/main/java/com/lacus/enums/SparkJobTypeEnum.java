package com.lacus.enums;

public enum SparkJobTypeEnum {
    BATCH_SQL, JAR;

    public static SparkJobTypeEnum getJobTypeEnum(String type) {
        if (type == null) {
            return null;
        }
        for (SparkJobTypeEnum jobTypeEnum : SparkJobTypeEnum.values()) {
            if (type.equals(jobTypeEnum.name())) {
                return jobTypeEnum;
            }
        }
        return null;
    }
} 