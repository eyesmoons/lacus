package com.lacus.service.flink;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.flink.entity.FlinkJobInstanceEntity;
import com.lacus.enums.FlinkStatusEnum;

import java.util.Date;

public interface IFlinkJobInstanceService extends IService<FlinkJobInstanceEntity> {
    void updateStatus(Long instanceId, FlinkStatusEnum flinkStatusEnum);
    void updateStatusByJobId(Long jobId, FlinkStatusEnum flinkStatusEnum, Date finishedTime);
}
