package com.lacus.common.exception;

import com.lacus.common.exception.error.ErrorCode;

/**
 * 下载失败异常
 */
public class DownloadFailedException extends ApiException {

    public DownloadFailedException(Object... args) {
        super(ErrorCode.Business.DOWNLOAD_FAILED, args);
    }

    public DownloadFailedException(Throwable cause, Object... args) {
        super(cause, ErrorCode.Business.DOWNLOAD_FAILED, args);
    }
}
