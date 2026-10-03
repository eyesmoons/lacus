package com.lacus.common.exception;

import com.lacus.common.exception.error.ErrorCode;

/**
 * 数据源不可达异常
 */
public class SourceUnreachableException extends ApiException {

    public SourceUnreachableException(Object... args) {
        super(ErrorCode.Business.SOURCE_UNREACHABLE, args);
    }

    public SourceUnreachableException(Throwable cause, Object... args) {
        super(cause, ErrorCode.Business.SOURCE_UNREACHABLE, args);
    }
}
