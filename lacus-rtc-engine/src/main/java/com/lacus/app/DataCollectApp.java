package com.lacus.app;

import com.lacus.IFlinkProcessor;
import com.lacus.common.exception.CustomException;
import com.lacus.factory.DataCollectFactory;
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
    // 任务名称
    protected static String jobName;
    // 任务配置json
    protected static String jobConf;
    // 处理器名称，比如：mysql
    protected static String processorName;

    public static void main(String[] args) throws Exception {
        if (ObjectUtils.isEmpty(args) || args.length < 3) {
            throw new CustomException("参数错误");
        }
        processorName = args[0];
        jobName = args[1];
        jobConf = args[2];

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

        DataCollectFactory.getInstance().register();
        IFlinkProcessor processor = DataCollectFactory.getInstance().getProcessor(processorName);
        if (Objects.isNull(processor)) {
            throw new CustomException("can not find processor " + processorName);
        }
        DataStreamSource<String> reader = processor.reader(env, jobName, jobConf);
        processor.transform(reader);
        processor.writer(env, jobName, jobConf);
        env.execute(jobName);
    }
}