package com.lacus.service.system.impl;

import com.lacus.dao.system.entity.SysOperationLogEntity;
import com.lacus.dao.system.mapper.SysOperationLogMapper;
import com.lacus.service.system.ISysOperationLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 操作日志记录 服务实现类
 */
@Service
public class SysOperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLogEntity> implements
        ISysOperationLogService {

}
