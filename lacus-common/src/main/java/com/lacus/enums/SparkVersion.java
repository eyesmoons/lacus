package com.lacus.enums;

import lombok.Getter;

@Getter
public enum SparkVersion {

    SPARK_2_4_x("SPARK_2_4_x", "Spark 2.4.x", "start-seatunnel-spark-2-connector-v2.sh"),
    SPARK_3_x_x("SPARK_3_x_x", "Spark 3.x.x", "start-seatunnel-spark-3-connector-v2.sh");

    private final String name;
    private final String version;
    private final String starupScript;

    SparkVersion(String name, String version, String starupScript) {
        this.name = name;
        this.version = version;
        this.starupScript = starupScript;
    }

    public static SparkVersion fromVersion(String version) {
        for (SparkVersion value : values()) {
            if (value.getName().equals(version)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unsupported Flink version: " + version);
    }

}
