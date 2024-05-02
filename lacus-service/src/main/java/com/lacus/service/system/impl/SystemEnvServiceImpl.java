package com.lacus.service.system.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.system.entity.SystemEnvEntity;
import com.lacus.dao.system.mapper.SystemEnvMapper;
import com.lacus.service.system.ISystemEnvService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 环境管理 服务实现类
 * </p>
 *
 * @author lacus
 * @since 2024-04-30
 */
@Service
public class SystemEnvServiceImpl extends ServiceImpl<SystemEnvMapper, SystemEnvEntity> implements ISystemEnvService {

}
