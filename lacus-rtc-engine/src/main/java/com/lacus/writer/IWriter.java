package com.lacus.writer;

import com.lacus.model.JobConf;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @created by shengyu on 2023/8/31 09:54
 */
public interface IWriter {

    void write(StreamExecutionEnvironment env, JobConf jobConf);
}