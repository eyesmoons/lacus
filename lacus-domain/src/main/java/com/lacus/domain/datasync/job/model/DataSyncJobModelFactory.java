package com.lacus.domain.datasync.job.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.domain.datasync.job.command.AddJobCommand;

import java.util.UUID;

public class DataSyncJobModelFactory {

    public static DataSyncJobModel loadFromAddCommand(AddJobCommand addCommand, DataSyncJobModel model) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        if (addCommand != null && model != null) {
            BeanUtil.copyProperties(addCommand, model);
            model.setTopic("data_sync_" + uuid);
        }
        return model;
    }
}
