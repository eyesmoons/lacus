package com.lacus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.entity.ApiHistoryEntity;
import com.lacus.dao.mapper.ApiHistoryMapper;
import com.lacus.service.IApiHistoryService;
import org.springframework.stereotype.Service;

@Service
public class ApiHistoryServiceImpl extends ServiceImpl<ApiHistoryMapper, ApiHistoryEntity> implements IApiHistoryService {

}
