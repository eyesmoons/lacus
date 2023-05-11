package com.lacus.job;

import java.io.Serializable;

public interface IJob extends Runnable, Serializable {
    void init();

    void afterInit();

    void handle() throws Throwable;

    void close();
}
