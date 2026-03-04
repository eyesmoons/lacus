package com.lacus.service.dig.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.dig.entity.StJobInstanceEntity;
import com.lacus.dao.dig.mapper.StJobInstanceMapper;
import com.lacus.service.dig.IStJobInstanceService;
import org.springframework.stereotype.Service;

@Service
public class StJobInstanceServiceImpl extends ServiceImpl<StJobInstanceMapper, StJobInstanceEntity> implements IStJobInstanceService {

    @Override
    public void complete(Long jobInstanceId, Integer status, String errorMsg) {
        StJobInstanceEntity jobInstance = this.getById(jobInstanceId);
        jobInstance.setStatus(status);
        jobInstance.setLogInfo(errorMsg);
        this.updateById(jobInstance);
    }
}
