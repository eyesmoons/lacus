package com.lacus.job.constants;


public enum SinkResponseEnums {


    MAPPING_CONF_NOT_FOUND("Stream load table mapping not found", 50001),
    DRUID_DATASOURCE_INIT_FAILED("Druid datasource init failed", 50002),
    DRUID_JDBC_CONNECT_OBTAIN_FAILED("Druid jdbc connect obtain failed", 50003),
    QUERY_SQL_RESULT_ERROR("Query sql result error", 50004),
    DORIS_BACKEND_ALIVE_NOT_FOUND("Can not found alive doris backends", 50005),
    DORIS_SINK_HTTP_CONNECTED_FAILED("Doris streamLoad http connected failed", 5006),
    DORIS_SINK_STREAM_LOAD_FAILED("Doris streamLoad failed", 5007);


    private String msg;

    private Integer code;


    SinkResponseEnums(String msg, Integer code) {
        this.msg = msg;
        this.code = code;
    }


    public String getMsg() {
        return msg;
    }

    public Integer getCode() {
        return code;
    }
}
