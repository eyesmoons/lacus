package com.lacus.job.flink.warehouse;


public abstract class AbstractSink<S, T> {

    protected abstract void init();

    protected abstract S sink(T stream);


}
