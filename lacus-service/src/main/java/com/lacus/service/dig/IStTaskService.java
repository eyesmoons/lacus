package com.lacus.service.dig;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.dig.entity.StTaskEntity;

import java.util.List;

public interface IStTaskService extends IService<StTaskEntity> {
    List<StTaskEntity> getTaskListByJobId(Long jobId);
}
