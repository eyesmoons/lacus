package com.lacus.common.exception;

public enum ResultCode implements IErrorCode {
    /**
     * success
     */
    SUCCESS(0, "操作成功"),
    /**
     * failed
     */
    FAILED(99999, "接口调用异常！"),



    METHOD_NOT_MATCH(70000,"当前接口请求方式与预设接口请求方式不符！"),

    /**
     * 接口请求方式未知
     */
    METHOD_NOT_FOUND(70001, "接口请求方式未知！"),
    /**
     * 当前接口请求地址不存在
     */
    API_URL_NOT_FOUND(70002, "当前接口请求地址不存在！"),
    /**
     * 接口地址未发布
     */
    API_STATUS_NOT_ONLINE(70003, "接口地址未发布！"),

    API_MUST_COLUMN_IS_NULL(70004, "接口参数必填项缺失！"),

    API_DATASOURCE_NOT_FOUND(70005, "接口数据源ID不存在！"),


    API_PARAMETER_ERROR(70006, "SQL解析错误！"),

    API_PARAMS_TYPE_NOT_MATCH(70007, "接口参数类型不匹配！"),

    UNSUPPORT_QUERY_ERROR(70008, "不支持的查询类型！"),


    SQL_PARSE_ERROR(70009, "SQL解析失败！"),

    COUNT_SQL_PARSE_ERROR(70010, "COUNT SQL解析错误！"),

    LIMIT_SQL_PARSE_ERROR(70011, "分页查询SQL构建失败！"),


    LIMIT_OFFSET_MUST_BE_ORDER_BY(700012, "分页接口必须有排序字段！"),


    PAGE_SIZE_MORE_THAN_MAX(700013, "pageSize最大支持65535条！"),

    DATASOURCE_NOT_FOUND_ERROR(80001, "根据数据源类型获取注册驱动失败！"),

    DATASOURCE_INIT_ERROR(80002, "数据源连接池初始化失败！"),

    DATASOURCE_CANNOT_GET_CONNECT(80003, "数据源连接获取失败！"),

    SQL_RUNTIME_ERROR(80004, "SQL运行失败"),

    SQL_RUNNING_TIMEOUT(80005, "SQL查询请求超时"),

    /**
     * validate failed
     */
    VALIDATE_FAILED(400, "参数检验失败"),

    /**
     * unauthorized
     */
    UNAUTHORIZED(401, "暂未登录或token已经过期"),

    /**
     * forbidden
     */
    FORBIDDEN(403, "没有相关权限"),

    RECORD_NOT_EXISTS(10005, "记录不存在"),

    RECORD_ALREADY_EXISTS(10005, "记录已存在"),

    RECORD_MORE_THAN_ONE(10006, "期望一条记录，结果查出多条");

    private long code;
    private String message;

    private ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
