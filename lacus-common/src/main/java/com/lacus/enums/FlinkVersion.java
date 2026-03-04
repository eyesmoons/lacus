package com.lacus.enums;

import lombok.Getter;

@Getter
public enum FlinkVersion {

    FLINK_1_12_TO_1_14("FLINK_1_12_TO_1_14", "1.12.x到1.14.x", "start-seatunnel-flink-13-connector-v2.sh"),
    FLINK_1_15_TO_1_18("FLINK_1_15_TO_1_18", "1.15.x到1.18.x", "start-seatunnel-flink-15-connector-v2.sh");

    private final String name;
    private final String version;
    private final String starupScript;

    FlinkVersion(String name, String version, String starupScript) {
        this.name = name;
        this.version = version;
        this.starupScript = starupScript;
    }

    public static FlinkVersion fromVersion(String version) {
        for (FlinkVersion value : values()) {
            if (value.getName().equals(version)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unsupported Flink version: " + version);
    }

}
