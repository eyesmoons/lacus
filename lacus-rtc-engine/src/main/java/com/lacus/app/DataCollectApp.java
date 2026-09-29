package com.lacus.app;

import com.alibaba.fastjson2.JSON;
import com.lacus.config.FlinkEnvConfig;
import com.lacus.handler.SourceHandler;
import com.lacus.handler.SinkHandler;
import com.lacus.exception.CustomException;
import com.lacus.model.JobConf;
import com.lacus.model.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.Serializable;

/**
 * 数据采集引擎统一入口，用户只需要编写自己的 source 和 sink 即可
 */
@Slf4j
public class DataCollectApp implements Serializable {
    private static final long serialVersionUID = 1L;

    public static void main(String[] args) throws Exception {
        // 1. 解析参数
        Parameter parameter = new Parameter(args);
        // 2. 创建flink执行环境
        StreamExecutionEnvironment env = FlinkEnvConfig.createExecutionEnvironment();

        try {
            // 3. 解析任务配置
            JobConf jobConf = parseJobConfig(parameter.jobParams);
            // 4. 配置source
            SourceHandler.configureSource(env, jobConf.getSource(), parameter);
            // 5. 配置sink
            SinkHandler.configureSink(env, jobConf, parameter.sinkName);
            // 6. 执行任务
            env.execute(parameter.jobName);
        } catch (Exception e) {
            throw new CustomException("数据采集任务执行异常", e);
        }
    }

    private static JobConf parseJobConfig(String jobParams) {
        try {
            return JSON.parseObject(jobParams, JobConf.class);
        } catch (Exception e) {
            throw new CustomException("解析任务配置失败: " + e.getMessage());
        }
    }
}
