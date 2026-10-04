package com.lacus.service.lakeintelligence;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.lakeintelligence.entity.LakeTaskEntity;

/**
 * 训练任务 Service 接口
 */
public interface ILakeTaskService extends IService<LakeTaskEntity> {

    /**
     * 校验任务名称是否重复
     *
     * @param id       任务ID（更新时传入，新增时传 null）
     * @param taskName 任务名称
     * @return true=重复
     */
    boolean isTaskNameDuplicated(Long id, String taskName);
}
