package com.lacus.dataquality.exception;

/**
 * 配置运行时异常
 */
public class ConfigRuntimeException extends RuntimeException {

    public ConfigRuntimeException(String message) {
        super(message);
    }

    public ConfigRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
