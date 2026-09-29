package com.lacus.sink;

import com.lacus.model.JobConf;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.util.Map;

/**
 * @created by shengyu on 2023/8/31 09:54
 */
public interface ISink {

    RichSinkFunction<Map<String, String>> getSink(JobConf jobConf);
}
