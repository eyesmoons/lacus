package com.lacus.service.dig;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.dig.entity.StTaskRelationEntity;

import java.util.List;

public interface IStTaskRelationService extends IService<StTaskRelationEntity> {
    List<StTaskRelationEntity> getTaskRelationsByJobId(Long jobId);
}
