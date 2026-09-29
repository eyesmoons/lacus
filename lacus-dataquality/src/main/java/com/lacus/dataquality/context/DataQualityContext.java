package com.lacus.dataquality.context;

import com.lacus.dataquality.config.DataQualityConfiguration;
import com.lacus.dataquality.exception.DataQualityException;
import com.lacus.dataquality.flow.batch.source.Source;
import com.lacus.dataquality.flow.batch.source.SourceFactory;
import com.lacus.dataquality.flow.batch.transform.Transform;
import com.lacus.dataquality.flow.batch.transform.TransformFactory;
import com.lacus.dataquality.flow.batch.sink.Sink;
import com.lacus.dataquality.flow.batch.sink.SinkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 数据质量执行上下文
 * 负责创建各组件实例并协调执行流程（source -> transform -> sink）
 */
public class DataQualityContext {

    private static final Logger logger = LoggerFactory.getLogger(DataQualityContext.class);

    private final SparkRuntimeEnvironment sparkEnv;
    private final DataQualityConfiguration configuration;

    public DataQualityContext(SparkRuntimeEnvironment sparkEnv, DataQualityConfiguration configuration) {
        this.sparkEnv = sparkEnv;
        this.configuration = configuration;
    }

    /**
     * 执行数据质量检查流程：source -> transform -> sink
     */
    public void execute() {
        logger.info("Starting DataQuality execution: {}", configuration.getName());

        // Step 1: 创建 Source 实例列表
        List<Source> sources = SourceFactory.getInstance()
                .getSources(sparkEnv, configuration.getSource());

        // Step 2: 创建 Transform 实例列表
        List<Transform> transforms = TransformFactory.getInstance()
                .getTransforms(sparkEnv, configuration.getTransform());

        // Step 3: 创建 Sink 实例列表
        List<Sink> sinks = SinkFactory.getInstance()
                .getSinks(sparkEnv, configuration.getSink());

        // Step 4: 执行批处理
        if (sparkEnv.isBatch()) {
            sparkEnv.getBatchExecution().execute(sources, transforms, sinks);
        } else {
            throw new DataQualityException("Only batch mode is supported currently");
        }

        logger.info("DataQuality execution completed: {}", configuration.getName());
    }
}
