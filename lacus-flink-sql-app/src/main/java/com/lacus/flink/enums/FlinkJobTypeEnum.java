package com.lacus.flink.enums;

/**
 * @author shengyu
 * @date 2024/10/26 18:03
 */
public enum FlinkJobTypeEnum {
    STREAMING_SQL, BATCH_SQL, JAR;

    public static FlinkJobTypeEnum getJobTypeEnum(String sqlModel) {
        if (sqlModel == null) {
            return null;
        }
        for (FlinkJobTypeEnum jobTypeEnum : FlinkJobTypeEnum.values()) {
            if (sqlModel.equals(jobTypeEnum.name())) {
                return jobTypeEnum;
            }
        }
        return null;
    }
}
