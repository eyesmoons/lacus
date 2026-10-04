package com.lacus.common.exception;

import com.lacus.common.exception.error.ErrorCode;

/**
 * 配额超出异常
 */
public class QuotaExceededException extends ApiException {

    public QuotaExceededException(Object... args) {
        super(ErrorCode.Business.QUOTA_EXCEEDED, args);
    }

    public QuotaExceededException(Throwable cause, Object... args) {
        super(cause, ErrorCode.Business.QUOTA_EXCEEDED, args);
    }
}
