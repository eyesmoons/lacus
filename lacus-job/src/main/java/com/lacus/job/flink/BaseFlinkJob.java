package com.lacus.job.flink;

import com.lacus.job.AbstractJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

@Slf4j
public abstract class BaseFlinkJob extends AbstractJob {

    private static final long serialVersionUID = -4382178120041481967L;

    protected static StreamExecutionEnvironment env;


    public BaseFlinkJob(String[] args) {
        super(args);
    }

    @Override
    public void afterInit() {
        log.info("初始化flink参数");

        env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);

        // 设置时间语义为processingTime
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
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
    }
}