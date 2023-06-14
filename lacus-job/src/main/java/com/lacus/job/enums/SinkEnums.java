package com.lacus.job.enums;


public enum SinkEnums {


    DORIS(0, "doris"),
    CLICKHOUSE(1, "clickhouse"),
    PRESTO(2, "presto");


    private String type;

    private Integer code;


    public String getType() {
        return type;
    }

    SinkEnums(Integer code, String type) {
        this.code = code;
        this.type = type;
    }


    public static SinkEnums getSinkEnums(String type) {
        for (SinkEnums sinkEnums : SinkEnums.values()) {
            if (sinkEnums.getType().equals(type)) {
                return sinkEnums;
            }
        }
        return null;
    }


}
