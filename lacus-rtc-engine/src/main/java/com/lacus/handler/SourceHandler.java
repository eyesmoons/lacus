package com.lacus.handler;

import com.alibaba.fastjson2.JSON;
import com.lacus.exception.CustomException;
import com.lacus.factory.DataCollectSourceFactory;
import com.lacus.model.JobConf;
import com.lacus.model.Parameter;
import com.lacus.source.ISource;
import com.lacus.utils.KafkaUtil;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.Serializable;
import java.util.Objects;

/**
 * 读取器配置类
 */
public class SourceHandler implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String KAFKA_CHANNEL_NAME = "_kafka_channel";

    public static void configureSource(StreamExecutionEnvironment env, com.lacus.model.SourceConfig sourceConfig, Parameter parameter) {
        DataCollectSourceFactory sourceFactory = DataCollectSourceFactory.getInstance();
        sourceFactory.register();
        ISource source = sourceFactory.getSource(parameter.sourceName);
        if (Objects.isNull(source)) {
            throw new CustomException("找不到对应的source: " + parameter.sourceName);
        }

        JobConf jobConf = JSON.parseObject(parameter.jobParams, JobConf.class);
        Source<String, ?, ?> dataSource = source.getSource(env, parameter.jobName, jobConf);
        DataStreamSource<String> sourceReader = env.fromSource(dataSource, WatermarkStrategy.noWatermarks(), parameter.sourceName + "_SOURCE");
        SingleOutputStreamOperator<String> transform = sourceReader.map((MapFunction<String, String>) source::transform);

        // 写入Kafka，便于sink读取
        transform.sinkTo(KafkaUtil.getKafkaSink(sourceConfig.getBootStrapServers(), sourceConfig.getTopics())).name(KAFKA_CHANNEL_NAME);
    }
}
