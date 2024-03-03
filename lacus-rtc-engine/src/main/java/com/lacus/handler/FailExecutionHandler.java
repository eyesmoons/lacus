package com.lacus.handler;


import com.lacus.model.ErrorMsgModel;
import com.lacus.model.RespContent;

public interface FailExecutionHandler {
    void failExecution(String key , String errorData, String targetDorisDatabase, String targetDorisTable, RespContent resp, String stackTrace, Integer errorCode);
    void failExecution(ErrorMsgModel errorMsgModel, String msg);
    void failExecutionInit();
    void failExecutionClose();
    Long getJobId();
}
