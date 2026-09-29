package com.lacus.enums;

public enum ResultCode implements IErrorCode {
    SUCCESS(200, "操作成功"),
    FAILED(500, "内部运行异常！"),
    CALL_THIRD_FAILED(10501, "调用三方异常！"),
    LOGIN_FAILED(10502, "用户名或密码错误！"),
    ACCOUNT_DISABLED(10503, "账号禁用！"),
    CAPTCHA_FAILED(10504, "验证码错误！"),
    FACE_FAILED(10505, "该用户不存在！"),
    VALIDATE_FAILED(10506, "参数检验失败"),
    UNAUTHORIZED(10507, "暂未登录或token已经过期"),
    FORBIDDEN(401, "没有相关权限"),
    RECORD_NOT_EXISTS(10508, "记录不存在"),
    RECORD_ALREADY_EXISTS(10509, "记录已存在"),
    RECORD_MORE_THAN_ONE(10510, "期望一条记录，结果查出多条"),
    RECORD_EXISTS(10511, "系统中存在记录"),
    EXECUTE_TIME_OUT(10512, "查询执行超时!");

    private Integer code;
    private String msg;

    private ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public static ResultCode getByCode(int code) {
        for (ResultCode resultCode : ResultCode.values()) {
            if (resultCode.getCode() == code) {
                return resultCode;
            }
        }
        throw new IllegalArgumentException("Invalid ResultCode code: " + code);
    }
}
