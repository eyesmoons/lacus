package com.lacus.exception;

import com.lacus.model.StreamLoadResponse;
import lombok.Getter;

@Getter
public class StreamLoadException extends RuntimeException {
    private static final long serialVersionUID = -1L;
    private static final Integer DEAFULT_EXCEPTION_STATUS = 500;
    private int status;
    private Object errorData;
    private StreamLoadResponse streamLoadResponse;

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

    public StreamLoadException(String message, StreamLoadResponse streamLoadResponse, Object errorData) {
        super(message);
        this.status = DEAFULT_EXCEPTION_STATUS;
        this.errorData = errorData;
        this.streamLoadResponse = streamLoadResponse;
    }

}
