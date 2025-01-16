package com.lacus.handler;

import com.lacus.exception.CustomException;
import com.lacus.factory.DataCollectSinkFactory;
import com.lacus.function.BinlogFilterFunction;
import com.lacus.function.BinlogMapFunction;
import com.lacus.function.BinlogProcessWindowFunction;
import com.lacus.function.RateLimiter;
import com.lacus.model.FlinkConf;
import com.lacus.model.JobConf;
import com.lacus.model.SourceConfig;
import com.lacus.sink.ISink;
import com.lacus.utils.KafkaUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;
import java.util.Objects;

/**
 * 写入器配置类
 */
@Slf4j
public class SinkHandler {

    /**
     * 配置并获取写入器
     */
    public static void configureSink(StreamExecutionEnvironment env, JobConf jobConf, String sinkName) {
        DataCollectSinkFactory sinkFactory = DataCollectSinkFactory.getInstance();
        sinkFactory.register();
        ISink sink = sinkFactory.getSink(sinkName);
        if (Objects.isNull(sink)) {
            throw new CustomException("找不到对应的sink: " + sinkName);
        }

        FlinkConf flinkConf = jobConf.getFlinkConf();
        SourceConfig sourceConfig = jobConf.getSource();
        DataStreamSource<ConsumerRecord<String, String>> kafkaSourceDs = env.fromSource(KafkaUtil.getKafkaSource(sourceConfig.getBootStrapServers(), sourceConfig.getTopics(), sourceConfig.getGroupId()), WatermarkStrategy.noWatermarks(), "kafka_source");
        // kafka消息过滤
        SingleOutputStreamOperator<ConsumerRecord<String, String>> filterDs = kafkaSourceDs.filter(new BinlogFilterFunction()).name("kafka_filter");
        // kafka消息增加删除标识和落库时间
        SingleOutputStreamOperator<Map<String, String>> mapDs = filterDs.map(new BinlogMapFunction()).name("kafka_map");
        // 应用开窗函数，主要起限流作用
        SingleOutputStreamOperator<Map<String, String>> triggerDs = mapDs.windowAll(TumblingProcessingTimeWindows.of(Time.seconds(flinkConf.getMaxBatchInterval())))
                .trigger(new RateLimiter<>(flinkConf.getMaxBatchRows(), flinkConf.getMaxBatchSize()))
                .apply(new BinlogProcessWindowFunction()).name("kafka_trigger");
        // 将kafka中的数据写入相应的sink
        triggerDs.addSink(sink.getSink(jobConf)).name(sinkName + "_SINK");
    }
}
