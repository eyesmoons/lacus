package com.lacus.service.dig.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.dig.entity.StTaskEntity;
import com.lacus.dao.dig.mapper.StTaskMapper;
import com.lacus.service.dig.IStTaskService;
import org.springframework.stereotype.Service;

@Service
public class StTaskServiceImpl extends ServiceImpl<StTaskMapper, StTaskEntity> implements IStTaskService {
}
