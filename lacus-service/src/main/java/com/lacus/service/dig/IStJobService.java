package com.lacus.service.dig;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.dig.entity.StJobEntity;

public interface IStJobService extends IService<StJobEntity> {

    /**
     * 校验任务名称是否重复
     * @param jobId 任务ID
     * @param jobName 任务名称
     * @return 是否重复
     */
    boolean isJobNameDuplicated(Long jobId, String jobName);
}
