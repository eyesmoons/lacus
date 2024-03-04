package com.lacus;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @created by shengyu on 2023/8/31 09:54
 */
public interface IFlinkProcessor {
    /**
     * 自定义reader
     */
    DataStreamSource<String> reader(StreamExecutionEnvironment env, String jobName, String jobParams);

    /**
     * 自定义transform，比如转换之后发往kafka，写入文件，或者打印
     */
    void transform(DataStreamSource<String> reader);

    /**
     * 自定义writer
     */
    void writer(StreamExecutionEnvironment env, String jobName, String jobParams);
}