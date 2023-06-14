package com.lacus.job.exception;


import com.lacus.job.constants.SinkResponse;

public class SinkException extends RuntimeException {


    private SinkResponse sinkResponse;

    private Integer status;

    private Object data;


    public SinkException(SinkResponse sinkResponse) {
        this.sinkResponse = sinkResponse;
    }


    public SinkException(SinkResponse sinkResponse, Throwable throwable) {
        super(throwable);
        this.sinkResponse = sinkResponse;
    }


    public SinkException(String message, Integer status) {
        super(message);
        this.status = status;
    }

    public SinkException(String message, Object data) {
        super(message);
        this.data = data;
    }


    public SinkException(String message, Integer status, Object data) {
        super(message);
        this.status = status;
        this.data = data;
    }


    public Integer getStatus() {
        return status;
    }

    public Object getData() {
        return data;
    }

    public SinkResponse getSinkResponse() {
        return sinkResponse;
    }
}
