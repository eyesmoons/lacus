package com.lacus.dataquality.exception;

/**
 * 数据质量运行时异常
 */
public class DataQualityException extends RuntimeException {

    public DataQualityException(String message) {
        super(message);
    }

    public DataQualityException(String message, Throwable cause) {
        super(message, cause);
    }
}
