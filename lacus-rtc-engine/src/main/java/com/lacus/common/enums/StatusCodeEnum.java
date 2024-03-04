package com.lacus.common.enums;

public enum StatusCodeEnum {

    PARSE_EXCEPTION_CODE(5000,"数据解析异常"),
    DATE_TYPE_ERROR(5001,"日期类型错误"),
    NUMBER_TYPE_ERROR(5002,"数字类型错误"),
    STRING_TYPE_ERROR(5003,"字符串超出长度错误"),
    COLUMN_INCONSISTENT_ERROR(5004,"字段数量不一致错误"),
    COLUMN_NOT_EMPTY_ERROR(5005,"字段不能为空错误"),
    DATA_PARSE_ERROR(5006,"字段解析错误"),
    PARTITION_COL_EMPTY_ERROR(5007,"分区字段为空"),
    STREAM_LOAD_ERROR(5008,"streamload失败"),
    RULE_CHECK_ERROR(5009,"规则校验失败"),
    ENCRYPT_SETTING_ERROR(5010,"数据加密失败");


    private Integer code;
    private String message;

    StatusCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
