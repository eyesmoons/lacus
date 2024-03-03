package com.lacus.common.exception;

/**
 * 自定义异常
 *
 * @created by shengyu on 2023/8/31 10:55
 */
public class CustomException extends RuntimeException {

    private static final long serialVersionUID = -3320221242086938214L;

    private Integer code;

    private String message;

    public CustomException(String message) {
        this.message = message;
    }

    public CustomException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public CustomException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
