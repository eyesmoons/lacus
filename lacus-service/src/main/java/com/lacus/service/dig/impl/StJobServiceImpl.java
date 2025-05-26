package com.lacus.service.dig.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.dig.entity.StJobEntity;
import com.lacus.dao.dig.mapper.StJobMapper;
import com.lacus.service.dig.IStJobService;
import org.springframework.stereotype.Service;

@Service
public class StJobServiceImpl extends ServiceImpl<StJobMapper, StJobEntity> implements IStJobService {
}
