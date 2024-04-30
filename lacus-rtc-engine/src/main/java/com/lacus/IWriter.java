package com.lacus;

import com.lacus.model.JobConf;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @created by shengyu on 2023/8/31 09:54
 */
public interface IWriter {
    /**
     * 自定义reader
     */
    void write(StreamExecutionEnvironment env, JobConf jobConf);
}