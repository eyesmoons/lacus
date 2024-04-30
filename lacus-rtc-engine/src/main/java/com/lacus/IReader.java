package com.lacus;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @created by shengyu on 2023/8/31 09:54
 */
public interface IReader {
    /**
     * 自定义reader
     */
    DataStreamSource<String> read(StreamExecutionEnvironment env, String jobName, String jobParams);
}