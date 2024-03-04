package com.lacus.common.exception;

import com.lacus.model.RespContent;

public class StreamLoadException extends RuntimeException {
    private static final long serialVersionUID = -1L;
    private static final Integer DEAFULT_EXCEPTION_STATUS = 500;
    private int status;
    private Object errorData;
    private RespContent respContent;

    public StreamLoadException(String message) {
        this(message, DEAFULT_EXCEPTION_STATUS);
    }

    public StreamLoadException(String message, int status) {
        super(message);
        this.status = status;
    }

    public StreamLoadException(String message, Object errorData) {
        super(message);
        this.status = DEAFULT_EXCEPTION_STATUS;
        this.errorData = errorData;
    }

    public StreamLoadException(String message, RespContent respContent, Object errorData) {
        super(message);
        this.status = DEAFULT_EXCEPTION_STATUS;
        this.errorData = errorData;
        this.respContent = respContent;
    }

    public int getStatus() {
        return status;
    }

    public Object getErrorData() {
        return errorData;
    }

    public RespContent getRespContent() {
        return respContent;
    }
}
