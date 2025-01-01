package com.lacus.service.flink;

import com.lacus.enums.FlinkDeployModeEnum;
import com.lacus.service.flink.model.StandaloneFlinkJobInfo;

public interface IStandaloneRpcService {

    /**
     * Standalone 模式下获取状态
     */
    StandaloneFlinkJobInfo getJobInfoForStandaloneByAppId(String appId, FlinkDeployModeEnum flinkDeployModeEnum);

    /**
     * 基于flink rest API取消任务
     */
    void cancelJobForFlinkByAppId(String jobId, FlinkDeployModeEnum flinkDeployModeEnum);


    /**
     * 获取savepoint路径
     */
    String savepointPath(String jobId, FlinkDeployModeEnum flinkDeployModeEnum);

    String getFlinkHttpAddress(FlinkDeployModeEnum flinkDeployModeEnum);
}
