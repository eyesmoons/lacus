package com.lacus.app;

import com.alibaba.fastjson2.JSON;
import com.lacus.config.FlinkEnvConfig;
import com.lacus.config.ReaderConfig;
import com.lacus.config.WriterConfig;
import com.lacus.exception.CustomException;
import com.lacus.model.JobConf;
import com.lacus.model.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 数据采集引擎统一入口，用户只需要编写自己的 Reader 和 Writer 即可
 */
@Slf4j
public class DataCollectApp {

    public static void main(String[] args) throws Exception {
        // 1. 解析参数
        Parameter parameter = new Parameter(args);
        // 2. 创建flink执行环境
        StreamExecutionEnvironment env = FlinkEnvConfig.createExecutionEnvironment();

        try {
            // 3. 解析任务配置
            JobConf jobConf = parseJobConfig(parameter.jobParams);
            // 4. 配置reader
            ReaderConfig.configureReader(env, jobConf.getSource(), parameter);
            // 5. 配置writer
            WriterConfig.configureWriter(env, jobConf, parameter.writerName);
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
