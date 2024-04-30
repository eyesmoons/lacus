package com.lacus.app;

import com.alibaba.fastjson2.JSON;
import com.lacus.IReader;
import com.lacus.IWriter;
import com.lacus.common.exception.CustomException;
import com.lacus.factory.DataCollectReaderFactory;
import com.lacus.factory.DataCollectWriterFactory;
import com.lacus.model.JobConf;
import com.lacus.model.SourceV2;
import com.lacus.utils.KafkaUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Objects;

/**
 * 数据采集引擎统一入口，用户只需要编写自己采集程序，注册到DataCollectFactory即可
 *
 * @created by shengyu on 2024/1/21 20:14
 */
public class DataCollectApp {
    protected static String jobName;
    protected static String jobParams;
    protected static String readerName;
    protected static String writerName;

    public static void main(String[] args) throws Exception {
        if (ObjectUtils.isEmpty(args) || args.length < 3) {
            throw new CustomException("参数错误");
        }
        readerName = args[0];
        writerName = args[1];
        jobName = args[2];
        jobParams = args[3];

        // 获取flink上下文环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 设置全局并行度
        env.setParallelism(1);
        // 设置时间语义为ProcessingTime
        env.getConfig().setAutoWatermarkInterval(0);
        // 每隔60s启动一个检查点
        env.enableCheckpointing(60000, CheckpointingMode.EXACTLY_ONCE);
        // checkpoint最小间隔
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(1000);
        // checkpoint超时时间
        env.getCheckpointConfig().setCheckpointTimeout(60000);
        // 同一时间只允许一个checkpoint
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        // Flink处理程序被cancel后，会保留Checkpoint数据
        env.getCheckpointConfig().setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

        DataCollectReaderFactory readerFactory = DataCollectReaderFactory.getInstance();
        readerFactory.register();
        IReader reader = readerFactory.getReader(readerName);
        if (Objects.isNull(reader)) {
            throw new CustomException("can not find reader " + readerName);
        }
        DataStreamSource<String> sourceReader = reader.read(env, jobName, jobParams);
        JobConf jobConf = JSON.parseObject(jobParams, JobConf.class);
        SourceV2 source = jobConf.getSource();
        sourceReader.sinkTo(KafkaUtil.getKafkaSink(source.getBootStrapServers(), source.getTopics())).name("_kafka_channel");

        DataCollectWriterFactory writerFactory = DataCollectWriterFactory.getInstance();
        writerFactory.register();
        IWriter writer = writerFactory.getWriter(writerName);
        if (Objects.isNull(writer)) {
            throw new CustomException("can not find writer " + writerName);
        }
        writer.write(env, jobConf);
        env.execute(jobName);
    }
}