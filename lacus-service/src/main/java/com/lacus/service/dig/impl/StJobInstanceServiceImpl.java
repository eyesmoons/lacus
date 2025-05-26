package com.lacus.service.dig.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.dig.entity.StJobInstanceEntity;
import com.lacus.dao.dig.mapper.StJobInstanceMapper;
import com.lacus.service.dig.IStJobInstanceService;
import org.springframework.stereotype.Service;

@Service
public class StJobInstanceServiceImpl extends ServiceImpl<StJobInstanceMapper, StJobInstanceEntity> implements IStJobInstanceService {
}
