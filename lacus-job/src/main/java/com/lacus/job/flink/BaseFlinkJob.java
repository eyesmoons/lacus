package com.lacus.job.flink;

import com.lacus.job.AbstractJob;
import com.lacus.job.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Objects;

@Slf4j
public abstract class BaseFlinkJob extends AbstractJob {

    private static final long serialVersionUID = -4382178120041481967L;

    protected static StreamExecutionEnvironment env;

    protected Integer maxBatchInterval;
    protected Integer maxBatchSize;
    protected Integer maxBatchCount;
    protected Integer parallelism;
    protected Integer slotsPerTaskManager;

    public BaseFlinkJob(String[] args) {
        super(args);
    }

    @Override
    public void afterInit() {
        log.info("初始化flink参数");
//        if (StringUtils.checkValNotNull(flinkConf)) {
//            this.jobName = flinkConf.getString("jobName");
//            this.maxBatchInterval = flinkConf.getInteger("maxBatchInterval");
//            this.maxBatchCount = flinkConf.getInteger("maxBatchRows");
//            this.maxBatchSize = flinkConf.getInteger("maxBatchSize");
//            this.parallelism = flinkConf.getInteger("parallelism");
//            if (Objects.isNull(parallelism)) {
//                this.parallelism = 1;
//            }
//            this.slotsPerTaskManager = flinkConf.getInteger("slotsPerTaskManager");
//            if (Objects.isNull(slotsPerTaskManager)) {
//                this.slotsPerTaskManager = 1;
//            }
//        }

        env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);

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