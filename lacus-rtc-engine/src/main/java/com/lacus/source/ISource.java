package com.lacus.source;

import com.lacus.model.JobConf;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 数据读取接口
 *
 * @param <T> 数据类型
 */
public interface ISource {

    /**
     * 读取数据源
     *
     * @param env     Flink执行环境
     * @param jobName 任务名称
     * @param jobConf 任务配置
     * @return Flink Source
     */
    Source<String, ?, ?> getSource(StreamExecutionEnvironment env, String jobName, JobConf jobConf);

    /**
     * 数据转换
     *
     * @param input 输入数据
     * @return 转换后的数据
     */
    String transform(String input);
}
